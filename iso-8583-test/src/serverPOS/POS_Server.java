package serverPOS;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class POS_Server {

	private static final int PORT = 2201;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		ExecutorService pool = Executors.newCachedThreadPool();
		
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server listening on port " + PORT);
		} catch (IOException e) {
			System.out.println("Failed to open listening socket");
			return;
		}
		
		// Listening for new connections
		while (true) {
			// Accepting new clients
			try {
				socket = serverSocket.accept();
			} catch(IOException e) {
				System.out.println("error on accept: " + e);
			}
			// Starting new client thread
			System.out.println("New connection to " + socket.getInetAddress().getHostAddress());
			pool.execute(new POS_ServerThread(socket));
		}
		//pool.shutdown();
	}
}