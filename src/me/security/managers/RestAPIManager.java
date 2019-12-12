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
			System.out.println("Listening on port" + server.getLocalPort());

			int threadCount = 0;
			while (true) {
				Socket client = server.accept();
				ConnectionThread thread = new ConnectionThread(client, threadCount);
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
			int clientPort = client.getPort();
			System.out.println("Connection " + counter + " : connected from " + clientIP + " with port " + clientPort + ".");
			try (InputStream input = client.getInputStream(); OutputStream output = client.getOutputStream()) {

				String url = getRequestUrl(input);
				if (url == null) {
					System.out.println("null");
					return;
				}
				
				PrintStream out = new PrintStream(output);
				out.println("HTTP/1.0 404 Not Found");
				out.println("");
				out.println("NOT FOUND");
				out.println("");
				out.println("nope");
				out.println("");
				out.flush();
/*
				switch(url) {
				
				case "/notify":
					sendHeader(output);
					sendText(output, "[{\"id_alerte\":1,\"date\":1576166249948,\"type\":\"motion\"},{\"id_alerte\":2,\"date\":1576100220948,\"type\":\"gas\"},{\"id_alerte\":3,\"date\":1576189249948,\"type\":\"motion\"}]");
					break;
				
				case "/sensors":
					sendHeader(output);
					sendText(output, "[{\"id\":1,\"type\":\"MOTION\",\"lastActive\":1576165908504,\"isEnabled\":true},{\"id\":2,\"type\":\"MOTION\",\"lastActive\":1576234568504,\"isEnabled\":true},{\"id\":3,\"type\":\"GAS\",\"lastActive\":-1,\"isEnabled\":true},{\"id\":4,\"type\":\"HEAT\",\"lastActive\":-1,\"isEnabled\":false},{\"id\":5,\"type\":\"MOTION\",\"lastActive\":1576146546544,\"isEnabled\":true}]");
					break;
				
				default:
					sendNotFound(output);
					break;
				
				}*/
				
				output.flush();
				output.close();
				input.close();
				this.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("resource")
		private String getRequestUrl(InputStream stream) {
			Scanner reader = new Scanner(stream).useDelimiter("\n");
			if (!reader.hasNext()) {
				reader.close();
				return null;
			}

			String line = reader.next();
			String url = line.split(" ")[1];
			reader.close();
			return url;
		}

		private void sendNotFound(OutputStream output) {
			PrintStream out = new PrintStream(output);
			out.println("HTTP/1.0 404 Not Found");
			out.println("");
			out.println("NOT FOUND");
			out.println("");
			out.println("nope");
			out.println("");
			out.flush();
		}

		private void sendHeader(OutputStream output) {
			PrintStream out = new PrintStream(output);
			out.println("HTTP/1.0 200 OK");
			out.println("MIME_version:1.0");
			out.println("Content-Type:application/json");
			out.println("");
			out.flush();
		}
		
		private void sendText(OutputStream output, String text) {
			PrintStream out = new PrintStream(output);
			out.println(text);
			out.println("");
			out.flush();
		}

		@SuppressWarnings("unused")
		private void sendFile(OutputStream output, File file) throws IOException {
			try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
				int len = (int) file.length();
				byte buf[] = new byte[len];
				in.readFully(buf);
				output.write(buf, 0, len);
			}
		}
	}
}