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
			String clientIP = this.client.getInetAddress().toString();
			System.out.println("Connection from " + clientIP);
			try (InputStream input = this.client.getInputStream(); OutputStream output = client.getOutputStream()) {
				Scanner inputReader = new Scanner(input);
				inputReader.useDelimiter("\n");
				if (!inputReader.hasNext()) {
					sendNotFound(output, "?");
					inputReader.close();
					return;
				}

				String url = inputReader.next().split(" ")[1];
				switch(url) {
				
				case "/isEnabled":
					sendText(output, this.security.isEnabled() + "");
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
							int id = Integer.parseInt(url.replaceFirst("/sensor/", ""));
							for(Sensor s : this.security.getSensors()) {
							}
						} catch(NumberFormatException ex) {
							sendNotFound(output, url);
						}
					} else {
						sendNotFound(output, url);
					}
					break;
				}

				inputReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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