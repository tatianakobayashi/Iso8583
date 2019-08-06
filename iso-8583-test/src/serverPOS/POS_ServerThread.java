package serverPOS;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

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
		byte[] lenInBytes = new byte[2];
		int reqLen;
		try {
			input.read(lenInBytes, 0, 2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Concatena e converte os dois primeiros bytes para descobrir tamanho da requisição
		reqLen = getReqLen(lenInBytes[0], lenInBytes[1]);
		 
		// Lê reqLen bytes restantes do buffer de entrada
		byte[] iso_request = new byte[reqLen];
		try {
			int bytesRead = input.read(iso_request, 0, reqLen);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	    StringBuilder sb = new StringBuilder();
	    for (byte b : iso_request) {
	        sb.append(String.format("%02X", b));
	    }
	    String stringReq = sb.toString();
	    
	    System.out.println("StringReq: " + stringReq);
		
		String formmated = parser.unpackIsoMsg(stringReq);
		System.out.println(formmated);
		List<String> conteudo63 = parser.parse63();
		String idade = conteudo63.get(0);
		// TO-DO (Adicionar codigo de resposta [bit 39 = 00 sucesso ou 01 falha])
		// TO-DO (Escrever mensagem no bit 63)
		if(validarIdade(idade)) {
			parser.setResponseCode("00");
			parser.setBit63("Ok!");
		}
		else {
			parser.setResponseCode("01");
			parser.setBit63("Falha");
		}
 		/*
 		// TO-DO (MTI para 0810)
 	
 		// TO DO (Adicionar 2 bytes com tamanho da mensagem no inicio)
		// Packs the response
		serverResponse = parser.repackIsoMsg();
		// Sends response to the client 	
		output.writeBytes(serverResponse);
		output.flush();
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

		intA = intA & 0x000000ff;
		intB = intB & 0x000000ff;

		len = a;
		len <<= 8;
		len = (int) (len | intB);

		return len;
	}
	
	private boolean validarIdade(String idade) {
		return true;
		return false;
	}
}