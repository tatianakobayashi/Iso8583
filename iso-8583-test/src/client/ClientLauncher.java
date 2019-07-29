package client;

import java.util.Random;

public class ClientLauncher {
	public static void main(String args[]) {
		int numberOfTerminals = 3000;
		int requestsPerTerminal = 1000;
		String serverIP = "";
		final int PORT = 0;
		String[] requestsPath = new String[4];
		Random random = new Random();
		
		getRequestsPaths(requestsPath);
		
		for(int i = 0; i < numberOfTerminals; i ++) {
			new ClientTerminal(serverIP, PORT, requestsPath[random.nextInt(3)], "Terminal " + i, requestsPerTerminal).start();
		}
	}
	
	private static void getRequestsPaths(String[] requestsPath) {
		requestsPath[0] = "/home/cassio/git/Iso8583/iso-8583-test/src/testFiles/req_test_1";
		requestsPath[1] = "/home/cassio/git/Iso8583/iso-8583-test/src/testFiles/req_test_2";
		requestsPath[2] = "/home/cassio/git/Iso8583/iso-8583-test/src/testFiles/req_test_3";
		requestsPath[3] = "/home/cassio/git/Iso8583/iso-8583-test/src/testFiles/req_test_4";
	}
}
