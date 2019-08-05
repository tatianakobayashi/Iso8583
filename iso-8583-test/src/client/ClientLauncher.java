package client;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientLauncher {
	public static void main(String args[]) {
		int numberOfTerminals = 100;
		int requestsPerTerminal = 15;
//		String serverIP = "192.168.41.105";
		String serverIP = "127.0.0.1";
		final int PORT = 2201;
		String[] requestsPath = new String[4];
		Random random = new Random();
		ExecutorService pool = Executors.newFixedThreadPool(numberOfTerminals);
		
		getRequestsPaths(requestsPath);
		
		for(int i = 0; i < numberOfTerminals; i ++) {
			pool.execute(new ClientTerminal(serverIP, PORT, requestsPath[random.nextInt(3)], "Terminal" + i, requestsPerTerminal));
		}
		pool.shutdown();
	}
	
	private static void getRequestsPaths(String[] requestsPath) {
		requestsPath[0] = "/home/tatiana/git/Iso8583/iso-8583-test/src/testFiles/req_test_1";
		requestsPath[1] = "/home/tatiana/git/Iso8583/iso-8583-test/src/testFiles/req_test_2";
		requestsPath[2] = "/home/tatiana/git/Iso8583/iso-8583-test/src/testFiles/req_test_3";
		requestsPath[3] = "/home/tatiana/git/Iso8583/iso-8583-test/src/testFiles/req_test_4";
	}
}

