package client;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import parser.Parser;

public class ClientTerminal extends Thread {
	private static Socket socket;
	private String terminalName;
	private int requestNum = 1;
	private int maxRequests;
	private Parser parser = null;
	private DataOutputStream outputToServer = null;
	private Scanner inputFromServer = null;
	private String request = "", packedRequest = "", response = "", unpackedResponse = "";
	private Random random = new Random();
	private Calendar c;

	public ClientTerminal(String serverIP, int port, String requestFile, String terminalName, int maxRequests) {
		this.terminalName = terminalName;
		this.maxRequests = maxRequests;
		c = Calendar.getInstance();
		try {
			InetAddress address = InetAddress.getByName(serverIP);
			socket = new Socket(address, port);
			outputToServer = new DataOutputStream(socket.getOutputStream());
			inputFromServer = new Scanner(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Terminal " + terminalName + ": " + "failed to build");
			return;
		}
		parser = new Parser();
		buildRequest(requestFile);

	}

	public void run() {
		for (int i = 0; i < maxRequests; i++) {
			long start = System.currentTimeMillis();
			// Parsing and packing iso request
			packRequest(i);
			// Sending packed request to server
			try {
				this.outputToServer.writeBytes(packedRequest);
			} catch(IOException e) {
				System.out.println("Terminal " + terminalName + ": " + "Error when sending request num(" + requestNum + ')');
				return;
			}
			// Receiving response from server
			response = inputFromServer.nextLine();
			// Parsing server response
			long end = System.currentTimeMillis();
			System.out.println("It took " + (end - start) / 1000 + " seconds for this transaction - " + terminalName);
			unpackedResponse = parser.unpackIsoMsg(response);
			try {
				// Thread sleeps 0 ~ 3 sec to simulate time between transactions 
				Thread.sleep(random.nextInt(3000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			outputToServer.writeBytes("close");
			outputToServer.flush();
			System.out.println(terminalName + " closing");
		} catch (IOException e) {
			System.out.println("Não enviou close");
			e.printStackTrace();
			return;
		}
	}

	private void buildRequest(String requestFile) {
		try (FileInputStream file = new FileInputStream(requestFile)) {
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine()) {
				this.request += fileScanner.nextLine();
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Thread " + terminalName + ": could not open file");
			return;
		} catch (IOException e) {
			System.out.println("Thread " + terminalName + ": error reading file");
			return;
		}
	}

	private void packRequest(int i) {
		parser.packIsoMsg(this.request);
		// Sets response fields
		parser.setDate(getDateAndTime());
		parser.setThreadName(this.getName());
		parser.setAuditNumber(i);
		this.packedRequest = parser.concatMsg();
		packedRequest += '\n';
	}
	
	// Returns the formatted date and time
		private String getDateAndTime() {
			return String.format("%02d%02d%02d%02d%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
					c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		}

}
