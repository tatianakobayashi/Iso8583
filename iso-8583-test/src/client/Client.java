package client;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;

import parser.Parser;

public class Client {

	private static Socket socket;

	public static void main(String args[]) {
		try {
			//String testFile = args[0];
			String testFile = "teste1";
			
			int port = 25000;
			String tatiAddress = "192.168.41.105";
			String local = "localhost";
			InetAddress address = InetAddress.getByName(local);
			socket = new Socket(address, port);
			Scanner keyboard = new Scanner(System.in);
			boolean flag = true;
			char reSend = 'n';
			
			Calendar c = Calendar.getInstance();
			String date = String.format("%02d%02d%02d%02d%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
			
			String localFileAddress = "/home/tatiana/eclipse-workspace/iso-8583-test/src/testFiles";

			// Creating output buffer
			DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
			// Creating input buffer
			Scanner inputFromServer = new Scanner(socket.getInputStream());
			// Instantiating parser
			Parser isoParser = new Parser();
			// Instantiating file reader
			File file = new File(localFileAddress + testFile);
			Scanner scanner = new Scanner(file);
			// Reading from test file
			String message = "";
			while (scanner.hasNextLine()) {
				message += scanner.nextLine();
			}
			// Parsing message
			String packedMessage = isoParser.packIsoMsg(message);
			packedMessage += '\n';
			String response = null;
			String parsedResponse = null;
			while (flag) {
				// Sending message
				outputToServer.writeBytes(packedMessage);
				outputToServer.flush();
				System.out.println("Request sent: " + packedMessage);
				// Receiving response from server
				response = inputFromServer.nextLine();
				System.out.println("Server response: " + response);
				// Parsing server response
				parsedResponse = isoParser.unpackIsoMsg(response);
				System.out.println("Parsed server response:\n" + parsedResponse);
				// Re send or close connection?
				System.out.println("Re-send request? [y/n]?");
				reSend = keyboard.next().charAt(0);
				if(reSend == 'y') {
					flag = true;
				}
				else {
					outputToServer.writeBytes("close");
					outputToServer.flush();
					flag = false;
				}
			}
			keyboard.close();
			outputToServer.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}