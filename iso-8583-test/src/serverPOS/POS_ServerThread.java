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

import parser.Context;
import parser.ParserISO;

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

		System.out.println("[MAIN] " + getName() + " running...");

		// Tenta referenciar canais de entrada e saída para esse socket
		try {
			is = socket.getInputStream();
			input = new BufferedInputStream(is);
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("[MAIN] Error when creating server I/O channels");
			return;
		}
		
		// Instâncias de parser e contexto
		ParserISO parser = new ParserISO();
		Context context = new Context();
		
		// Obtém os dois primeiros bytes da request e descobre o tamanho
		byte[] headerTamanho = new byte[2];
		try {
			input.read(headerTamanho, 0, 2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		context.setIsoRequestLen(parser.getReqLen(headerTamanho[0], headerTamanho[1]));
		
		// Lê e armazena o resto da request
		context.setRawIsoRequest(input);
		// Obtém request como hex
		context.setIsoRequestHex(parser.bytesToHex(context.getRawIsoRequest()));
		// Processa request e gera mapa de campo->conteudo
		parser.unpackIsoRequest(context.getIsoRequestHex(), context.getIsoRequestMap());
		// Gera o map da response a partir do map da request, mas sem os campos
		// "desnecessários"
		parser.makeResponseMap(context.getIsoRequestMap(), context.getIsoResponseMap());
		
		List<String> conteudo63 = parser.parse63();
		String idade = conteudo63.get(0);

		if (validarIdade(idade)) {
			parser.setResponseCode("00");
			parser.setBit63("Ok!");
		} else {
			parser.setResponseCode("01");
			parser.setBit63("Falha");
		}

		// Packs the response
		parser.repackIsoMsg();
		// Sends response to the client
		
		byte bytes[] = parser.textToBytes(serverResponse);
		
		String teste = parser.bytesToText(bytes);
		
		System.out.println("[MAIN] " + teste);

		System.out.println("[MAIN] " + parser.getIsoRequestMap().toString());
		System.out.println("[MAIN] " + serverResponse);
		
		
		try {
			output.write(serverResponse.getBytes());
			output.flush();
		} catch (IOException e1) {
			System.out.println("[MAIN] Failed to send response;");
		}

		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("[MAIN] Failed to close socket");
			return;
		}
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
			} else
				return false;
		} catch (DateTimeException e) {
			return false;
		}

	}
}