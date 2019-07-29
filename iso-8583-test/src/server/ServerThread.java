package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;

import parser.Parser;

public class ServerThread extends Thread {
	protected Socket socket;
	private Calendar c;
	private ServerStatistics serverStatistics;
	private int transactions = 0;

	// Class builder
	public ServerThread(Socket clientSocket, ServerStatistics serverStatistics) {
		this.socket = clientSocket;
		this.serverStatistics = serverStatistics;
		c = Calendar.getInstance();
	}

	// Returns the formatted date and time
	private String getDateAndTime() {
		return String.format("%02d%02d%02d%02d%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	public void run() {
		InputStream is = null;
		Scanner scanner = null;
		DataOutputStream output = null;

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
		String formattedMessage;
		String serverResponse;

		boolean flag = true;

		int auditNumber = 0;
		int lastAuditNumber;

		while (flag) {
			try {
				clientRequest = scanner.nextLine();
				if (clientRequest == null) {
					socket.close();
					System.out.println("null");
					scanner.close();
					return;
				} else if (clientRequest.equals("close")) {
					System.out.println("Close message received");
					flag = false;
				} else {
					transactions++;
					String responseCode = "00";

					// Unpacks the message received from the client
					formattedMessage = parser.unpackIsoMsg(clientRequest);

					// Prints the message in the console
					System.out.println("Request received");
//					System.out.println("Client request:");
//					System.out.println(clientRequest);
//					System.out.println(formattedMessage);

					// Gets the STAN number
					lastAuditNumber = auditNumber;
					auditNumber = Integer.parseInt(parser.getIsoRequestMap().get(11));

					// Checks if the STAN number is correct
					if (auditNumber != 0 && lastAuditNumber != 0 && lastAuditNumber != auditNumber - 1) {
						responseCode = "12";
					}

					// Sets response fields
					parser.setResponseCode(responseCode);
					parser.setDate(getDateAndTime());
					parser.setThreadName(this.getName());
					parser.setAuditNumber(auditNumber + 1);

					// Packs the response
					serverResponse = parser.repackIsoMsg();

					// Prints the response in the console
//					System.out.println("Server response:");
//					System.out.println(serverResponse);
					System.out.println("Sending response...");

					// Adds line break to the end of the message
					serverResponse += '\n';

					// Sends response to the client
					output.writeBytes(serverResponse);
					output.flush();

					serverStatistics.putTransactionsByThread(getName(), transactions);
				}
			} catch (IOException e) {
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