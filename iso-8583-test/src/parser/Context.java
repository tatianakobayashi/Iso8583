package parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

// Essa classe armazena uma request ISO e sua response, além de informações sobre ambas
public class Context {

	// Mapas de (número do campo)-->(conteúdo do campo), para request e response
	// respectivamente
	private HashMap<Integer, FieldWrapper> isoRequestMap = new HashMap<Integer, FieldWrapper>();
	private HashMap<Integer, FieldWrapper> isoResponseMap = new HashMap<Integer, FieldWrapper>();
	// Array de bytes recebido do socket sem os dois bytes de tamanho
	private byte[] rawIsoRequest;
	// Array de bytes com a response a nível de socket
	private byte[] rawIsoResponse;
	// Iso request como uma string em hex
	private String isoRequestHex = new String("");
	// Iso response como uma string em hex
	private String isoResponseHex = new String("");
	// Tamanho de cada mensagem a nível de socket (em bytes)
	private int isoRequestLen;
	private int isoResponseLen;

	public void setIsoRequestLen(int len) {
		this.isoRequestLen = len;
	}

	public void setRawIsoRequest(BufferedInputStream input) {
		this.rawIsoRequest = new byte[this.isoRequestLen];

		try {
			input.read(this.rawIsoRequest, 0, this.isoRequestLen);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public byte[] getRawIsoRequest() {
		return this.rawIsoRequest;
	}

	public byte[] getRawIsoResponse() {
		return this.rawIsoResponse;
	}

	public HashMap<Integer, FieldWrapper> getIsoRequestMap() {
		return this.isoRequestMap;
	}

	public void setIsoRequestHex(String reqAsHex) {
		this.isoRequestHex = reqAsHex;
	}

	public void setIsoResponseHex(String respAsHex) {
		this.isoResponseHex = respAsHex;
	}
	
	public String getIsoResponseHex() {
		return this.isoResponseHex;
	}

	public String getIsoRequestHex() {
		return this.isoRequestHex;
	}

	public HashMap<Integer, FieldWrapper> getIsoResponseMap() {
		return this.isoResponseMap;
	}
	
	public void allocRawIsoResponse(int len) {
		this.rawIsoResponse = new byte[len];
	}
	
	// (TODO) Imprime a request de forma mais legível
	public String printFormattedIsoRequest() {
		String requestFormatada = new String("TODO");
		// TODO
		return requestFormatada;
	}

	// (TODO) Imprime a response de forma mais legível
	public String printFormattedIsoResponse() {
		String responseFormatada = new String("TODO");
		// TODO
		return responseFormatada;
	}

	public void setRawIsoResponse(byte[] packIsoResponse) {
		this.rawIsoResponse = Arrays.copyOf(packIsoResponse, packIsoResponse.length);		
	}

}
