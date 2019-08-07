package serverPOS;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
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
		// Concatena e converte os dois primeiros bytes para descobrir tamanho da
		// requisição
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
		 		
		// Packs the response
		serverResponse = parser.repackIsoMsg();
		// Sends response to the client

		System.out.println(parser.getIsoRequestMap().toString());
		System.out.println(serverResponse);
		try {
			output.write(serverResponse.getBytes());
			output.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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
		int dia = Integer.parseInt(idade.substring(0, 2));
		int mes = Integer.parseInt(idade.substring(3, 5));
		int ano = Integer.parseInt(idade.substring(6, 10));
		
		LocalDate atual = LocalDate.now();
		
		try {
			LocalDate nascimento = LocalDate.of(ano, Month.of(mes), dia);
			if (Period.between(nascimento, atual).getYears() >= 18) {
				return true;
			}
			else
				return false;
		} catch (DateTimeException e) {
			return false;
		}

	}
}