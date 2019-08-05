package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import parser.Parser;

public class ServerThread extends Thread {
	protected Socket socket;
	private ServerStatistics serverStatistics;
	private int transactions = 0;

	// Constructor
	public ServerThread(Socket clientSocket, ServerStatistics serverStatistics) {
		this.socket = clientSocket;
		this.serverStatistics = serverStatistics;

	}

	public void run() {
		long start = System.currentTimeMillis();

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
//		 String formattedMessage;
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
					serverStatistics.closingConnection(getName());

					socket.close();
					System.out.println("null");
					scanner.close();
					return;
				} else if (clientRequest.equals("close")) {
					System.out.println("Close message received");
					flag = false;

					serverStatistics.closingConnection(getName());

					serverStatistics.putTransactionsByThread(getName(), transactions);
					serverStatistics.setTimeByThread(getName(), start, System.currentTimeMillis());
//					serverStatistics.printStatistics();
					System.out.println("Total waiting time = " + totalWait);
					System.out.println("Total processing time = " + totalProcess);
				} else {
					transactions++;
					String responseCode = "00";

					// Unpacks the message received from the client
					long unpackStart = System.currentTimeMillis();
//					 formattedMessage = parser.unpackIsoMsg(clientRequest);
					parser.unpackIsoMsg(clientRequest);
					long unpackFinish = System.currentTimeMillis();

					serverStatistics.newUnpackTime(unpackStart, unpackFinish);

					// Prints the message in the console
					System.out.println("Request received");
					System.out.println("Client request:");
					System.out.println(clientRequest);
//					 System.out.println(formattedMessage);

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

					serverStatistics.newPackTime(packingStart, packingFinish);

					// Prints the response in the console
//					System.out.println("Server response:");
//					System.out.println(serverResponse);
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
				serverStatistics.closingConnection(getName());

				serverStatistics.putTransactionsByThread(getName(), transactions);
				serverStatistics.setTimeByThread(getName(), start, System.currentTimeMillis());
//				serverStatistics.printStatistics();
				
				System.out.println("At thread " + getName() + " " + e.getMessage());
//				e.printStackTrace();
				
				scanner.close();
				return;
			} catch (IOException e) {
				serverStatistics.closingConnection(getName());

				System.out.println("At thread " + getName());
//				e.printStackTrace();
				scanner.close();
				return;
			} catch (NumberFormatException e) {
				serverStatistics.closingConnection(getName());

				System.out.println("At thread " + getName());
//				e.printStackTrace();
				scanner.close();
				return;
			}
		}
		scanner.close();
		try {
			socket.close();
		} catch (IOException e) {
			serverStatistics.closingConnection(getName());

			System.out.println("Failed to close socket");
			return;
		}

	}
	
	
}