package me.security.managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.security.hardware.sensors.Sensor;
import me.security.managers.DatabaseManager.Log;

/**
 * @author Ekinoxx
 *
 */
public class RestAPIManager {
	
	private static final int PORT = 8080;
	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	
	private final SecuManager security;

	public RestAPIManager(SecuManager security) {
		this.security = security;
		try (ServerSocket server = new ServerSocket(PORT)) {
			System.out.println("Listening on port " + server.getLocalPort());

			while (true) {
				ConnectionThread thread = new ConnectionThread(this.security, server.accept());
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class ConnectionThread extends Thread {
		
		private final SecuManager security;
		private Socket client;

		public ConnectionThread(SecuManager security, Socket client) {
			this.security = security;
			this.client = client;
		}

		public void run() {
			//String clientIP = this.client.getInetAddress().toString();
			//System.out.println("Connection from " + clientIP);
			
			InputStream input = null;
			OutputStream output = null;
			Scanner inputReader = null;
			
			try {
				input = this.client.getInputStream();
				output = this.client.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} 
			
			try {
				inputReader = new Scanner(input);
				inputReader.useDelimiter("\n");
				if (!inputReader.hasNext()) {
					throw new Exception("Input reader has nothing.");
				}

				String url = inputReader.next().split(" ")[1];
				switch(url) {
				
				case "/alarm":
					sendText(output, this.security.isEnabled() + "");
					break;
					
				case "/alarm/toggle":
					this.security.toggleAlarm("APP.");
					sendText(output, this.security.isEnabled() + "");
					break;
					
				case "/alarm/test":
					this.security.triggerAlarm("TEST", "Activation de l'alarme de test.");
					sendText(output, "true");
					break;
				
				case "/notify":
					List<Log> logs = this.security.getDb().getLast10Logs();
					sendText(output, GSON.toJson(logs));
					break;
					
				case "/sensors":
					sendText(output, GSON.toJson(this.security.getSensors()));
					break;
				
				default:
					if(url.startsWith("/sensor/")) {
						try {
							Sensor target = null;
							int id = Integer.parseInt(url.split("/")[2]);
							for(Sensor s : this.security.getSensors()) {
								if(s.getId() == id) {
									target = s;
								}
							}
							
							if(target == null) {
								throw new IllegalArgumentException(id + "");
							}
							
							switch(url.replaceFirst("/sensor/" + id, "")) {
							
							case "/toggle":
								target.toggle();
								sendText(output, target.isEnabled() + "");
								break;
								
							//Potentially add other endpoint to manage sensor ?
								
							default:
								sendNotFound(output, url);
								break;
							
							}
						} catch(NumberFormatException ex) {
							throw new Exception("Invalid sensor ID.");
						}
					} else {
						throw new IllegalAccessException("Unknown endpoint.");
					}
					break;
				}

				inputReader.close();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				sendNotFound(output, e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				sendError(output, e.getMessage());
			} finally {
				if(inputReader != null)
					inputReader.close();
				
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void sendError(OutputStream output, String msg) {
			PrintStream out = new PrintStream(output);
			out.println("HTTP/1.0 500 Internal Server Error");
			out.println("");
			out.println("" + msg);
			out.println("");
			out.flush();
		}
		
		private void sendNotFound(OutputStream output, String url) {
			PrintStream out = new PrintStream(output);
			out.println("HTTP/1.0 404 Not Found");
			out.println("");
			out.println("NOT FOUND : " + url);
			out.println("");
			out.flush();
		}

		private void sendText(OutputStream output, String text) {
			PrintStream out = new PrintStream(output);
			out.println("HTTP/1.0 200 OK");
			out.println("Content-Type:application/json");
			out.println("");
			out.println(text);
			out.println("");
		}


	}
}