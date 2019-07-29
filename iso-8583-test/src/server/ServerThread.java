package server;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

import parser.Parser;

public class ServerThread extends Thread {
	protected Socket socket;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

	public void run() {
        InputStream is = null;
        Scanner scanner = null;
        DataOutputStream output = null;
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
        while(flag) {
            try {
                clientRequest = scanner.nextLine();
                if (clientRequest == null) {
                    socket.close();
                    System.out.println("null");
                    scanner.close();
                    return;
                }
                else if(clientRequest.equals("close")) {
                	System.out.println("Close message received");
                	flag = false;
                }
                else {
                	formattedMessage = parser.unpackIsoMsg(clientRequest);
                	System.out.println("Client request:");
                	System.out.println(clientRequest);
                	System.out.println("Server response:");
                	parser.setResponseCode();
                	serverResponse = parser.repackIsoMsg();
                	System.out.println(serverResponse);
                	serverResponse += '\n';
                	output.writeBytes(serverResponse);
                    output.flush();
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
        }
        catch (IOException e) {
        	System.out.println("Failed to close socket");
        	return;
        }
    }
}