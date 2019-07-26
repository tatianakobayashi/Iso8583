package test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket server = null;
		Socket client = null;
		try {
			server = new ServerSocket(6666);
			System.out.println("Porta 6666 aberta");

			client = server.accept();
			System.out.println("Nova conexão com " + client.getInetAddress().getHostAddress());

			Scanner input = new Scanner(System.in);
			
			Scanner clientInput = new Scanner(client.getInputStream());
			

			Thread clientThread = new Thread(new PrintThread(clientInput, System.out));
			Thread serverThread = new Thread(new PrintThread(input, client.getOutputStream()));
			
			clientThread.start();
			serverThread.start();
			
			clientThread.join();
			serverThread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(!server.isClosed()) {
				server.close();
			}
			if(!client.isClosed()) {
				client.close();
			}
		}
	}
}
