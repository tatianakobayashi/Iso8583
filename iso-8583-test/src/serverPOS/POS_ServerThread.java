package serverPOS;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import parser.Parser;

public class POS_ServerThread extends Thread {
	protected Socket socket;

	// Constructor
	public POS_ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {
		InputStream is = null;
		BufferedInputStream input = null;
		DataOutputStream output = null;

		System.out.println(getName() + " running...");

		// Tries to get input/output references
		try {
			is = socket.getInputStream();
			input = new BufferedInputStream(is);
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error when creating server I/O channels");
			return;
		}

		Parser parser = new Parser();

		String clientRequest;
		String serverResponse;

		boolean flag = true;
		byte[] b = new byte[2];
		int reqLen;
		
		try {
			input.read(b, 0, 2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Concatena e converte os dois primeiros bytes para descobrir tamanho da requisição
		reqLen = getReqLen(b[0], b[1]);
		//System.out.println("b[0] e b[1] " + String.format("%02x", b[0]) + " " + String.format("%02x", b[1]));
		//System.out.println("reqLen " + String.format("%08x", reqLen));
		
		// Lê reqLen bytes restantes do buffer de entrada
		byte[] req = new byte[reqLen];
		System.out.println("reqLen " + reqLen);
		try {
			int bytesRead = input.read(req, 0, reqLen);
			System.out.println("Bytes read: " + bytesRead);
			System.out.println("Req size " + req.length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String stringReq;
		try {
			stringReq = new String(req, "ASCII");
			System.out.println(stringReq);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
			
			/*
			 * String responseCode = "00";
			 * 
			 * // Unpacks the message received from the client
			 * parser.unpackIsoMsg(clientRequest);
			 * 
			 * // Prints the message in the console System.out.println("Client request:");
			 * System.out.println(clientRequest);
			 * 
			 * // Gets the STAN number lastAuditNumber = auditNumber; auditNumber =
			 * Integer.parseInt(parser.getIsoRequestMap().get(11));
			 * 
			 * // Checks if the STAN number is correct if (auditNumber != 0 &&
			 * lastAuditNumber != 0 && lastAuditNumber != auditNumber - 1) { responseCode =
			 * "12"; }
			 * 
			 * // Sets response fields parser.setResponseCode(responseCode);
			 * 
			 * // Packs the response serverResponse = parser.repackIsoMsg();
			 * 
			 * System.out.println("Sending response...");
			 * 
			 * // Adds line break to the end of the message serverResponse += '\n';
			 * 
			 * // Sends response to the client output.writeBytes(serverResponse);
			 * output.flush();
			 */

		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Failed to close socket");
			return;
		}
	}
	
	private int getReqLen(byte a, byte b) {
		int len;
		int intA = (int) a;
		int intB = (int) b;
		//System.out.println("intA " + String.format("%08x", intA));
		intA = intA & 0x000000ff;
		//System.out.println("intA mask " + String.format("%08x", intA));
		//System.out.println("intB " + String.format("%08x", intB));
		intB = intB & 0x000000ff;
		//System.out.println("intB mask " + String.format("%08x", intB));
		
		len = a;
		//System.out.println("len = a " + String.format("%08x", len));
		len <<= 8;
		//System.out.println("len << 8 " + String.format("%08x", len));
		len = (int) (len | intB);
		//System.out.println("len | b " + String.format("%08x", len));
			
		return len;
	}
}