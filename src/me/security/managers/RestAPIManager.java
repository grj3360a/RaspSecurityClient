package me.security.managers;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Ekinoxx
 *
 */
public class RestAPIManager {
	private static final int PORT = 8080;
	
	private final SecuManager security;

	public RestAPIManager(SecuManager security) {
		this.security = security;
		try (ServerSocket server = new ServerSocket(PORT)) {
			System.out.println("Listening on port " + server.getLocalPort());

			int threadCount = 0;
			while (true) {
				ConnectionThread thread = new ConnectionThread(server.accept(), threadCount);
				thread.start();
				threadCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class ConnectionThread extends Thread {
		private Socket client;
		private int counter;

		public ConnectionThread(Socket client, int counter) {
			this.client = client;
			this.counter = counter;
		}

		public void run() {
			String clientIP = client.getInetAddress().toString();
			System.out.println("Connection " + counter + " from " + clientIP);
			try (InputStream input = client.getInputStream(); OutputStream output = client.getOutputStream()) {
				Scanner inputReader = new Scanner(input);
				inputReader.useDelimiter("\n");
				if (!inputReader.hasNext()) {
					sendNotFound(output, "?");
					inputReader.close();
					return;
				}

				String url = inputReader.next().split(" ")[1];
				
				switch(url) {
				
				case "/notify":
					sendText(output, "[{\"id_alerte\":1,\"date\":1576166249948,\"type\":\"motion\"}]");
					break;
					
				case "/sensors":
					sendText(output, "[{\"id\":1,\"type\":\"MOTION\",\"lastActive\":1576165908504,\"isEnabled\":true},{\"id\":2,\"type\":\"MOTION\",\"lastActive\":1576234568504,\"isEnabled\":true},{\"id\":3,\"type\":\"GAS\",\"lastActive\":-1,\"isEnabled\":true},{\"id\":4,\"type\":\"HEAT\",\"lastActive\":-1,\"isEnabled\":false},{\"id\":5,\"type\":\"MOTION\",\"lastActive\":1576146546544,\"isEnabled\":true}]");
					break;
				
				default:
					sendNotFound(output, url);
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