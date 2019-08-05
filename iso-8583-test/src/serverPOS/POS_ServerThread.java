package serverPOS;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import parser.Parser;

public class POS_ServerThread extends Thread {
	protected Socket socket;
	private int transactions = 0;

	// Constructor
	public POS_ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {
		InputStream is = null;
		Scanner scanner = null;
		DataOutputStream output = null;
		
		System.out.println(getName() + " running...");

		// Tries to get input/output references
		try {
			is = socket.getInputStream();
			scanner = new Scanner(is);
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error when creating server I/O channels");
			return;
		}

		Parser parser = new Parser();

		String clientRequest;
		String serverResponse;

		boolean flag = true;

		int auditNumber = 0;
		int lastAuditNumber;

		long waitingStart, processStart, waitingEnd, processEnd;
		long totalWait = 0, totalProcess = 0;
		while (flag) {
			try {
				waitingStart = System.currentTimeMillis();
				clientRequest = scanner.nextLine();
				waitingEnd = System.currentTimeMillis();
				totalWait += waitingEnd - waitingStart;
				processStart = System.currentTimeMillis();
				if (clientRequest == null) {
					socket.close();
					System.out.println("null");
					scanner.close();
					return;
					
				} else if (clientRequest.equals("close")) {
					System.out.println("Close message received");
					flag = false;
					System.out.println("Total waiting time = " + totalWait);
					System.out.println("Total processing time = " + totalProcess);
					
				} else {
					transactions++;
					String responseCode = "00";

					// Unpacks the message received from the client
					long unpackStart = System.currentTimeMillis();
					parser.unpackIsoMsg(clientRequest);
					long unpackFinish = System.currentTimeMillis();


					// Prints the message in the console
					System.out.println("Request received");
					System.out.println("Client request:");
					System.out.println(clientRequest);

					// Gets the STAN number
					lastAuditNumber = auditNumber;
					auditNumber = Integer.parseInt(parser.getIsoRequestMap().get(11));

					// Checks if the STAN number is correct
					if (auditNumber != 0 && lastAuditNumber != 0 && lastAuditNumber != auditNumber - 1) {
						responseCode = "12";
					}

					// Sets response fields
					parser.setResponseCode(responseCode);

					// Packs the response
					long packingStart = System.currentTimeMillis();
					serverResponse = parser.repackIsoMsg();
					long packingFinish = System.currentTimeMillis();

					processEnd = System.currentTimeMillis();
					totalProcess += processEnd - processStart;
					waitingStart = System.currentTimeMillis();
					System.out.println("Sending response...");

					// Adds line break to the end of the message
					serverResponse += '\n';

					// Sends response to the client
					output.writeBytes(serverResponse);
					waitingEnd = System.currentTimeMillis();
					totalWait += waitingEnd - waitingStart;
					output.flush();

				}
			} catch (NoSuchElementException e) {
				System.out.println("At thread " + getName() + " " + e.getMessage());
				// e.printStackTrace();
				scanner.close();
				return;
				
			} catch (IOException e) {
				System.out.println("At thread " + getName());
				e.printStackTrace();
				scanner.close();
				return;
				
			} catch (NumberFormatException e) {
				System.out.println("At thread " + getName());
				e.printStackTrace();
				scanner.close();
				return;
			}
		}
		scanner.close();
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Failed to close socket");
			return;
		}

	}
	
	
}