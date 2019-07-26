package test;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws IOException {
		Socket client = null;
		try {
			client = new Socket("127.0.0.1", 6666);
			System.out.println("Cliente conectado com o servidor");

			Scanner keyboard = new Scanner(System.in);

			
			Scanner serverScanner = new Scanner(client.getInputStream());

			
			Thread clientThread =  new Thread(new PrintThread(keyboard, client.getOutputStream()));
			Thread serverThread =  new Thread(new PrintThread(serverScanner, System.out));
			
			clientThread.start();
			serverThread.start();
			
			clientThread.join();
			serverThread.join();
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(!client.isClosed()) {
				client.close();
			}
		}
	}
}
