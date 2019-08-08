package parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;

// Essa classe armazena uma request ISO e sua response, além de informações sobre ambas
public class Context {
	
	// Mapas de (número do campo)-->(conteúdo do campo), para request e response respectivamente 
	private HashMap<Integer, String> isoRequestMap = new HashMap<Integer, String>();
	private HashMap<Integer, String> isoResponseMap = new HashMap<Integer, String>();
	// Array de bytes recebido do socket sem os dois bytes de tamanho
	private byte[] rawIsoRequest;
	// Array de bytes com a response a nível de socket
	private byte[] rawIsoResponse;
	// Iso request como uma string em hex
	private String isoRequestHex = new String("");
	// Iso response como uma string em hex
	private String isoResponsetHex = new String("");
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
	
	// (TODO) Imprime a request de forma mais legível
	public String printFormattedIsoRequest() {
		String requestFormatada = new String("");
		// TODO
		return requestFormatada;
	}
	
	public byte[] getRawIsoRequest() {
		return this.rawIsoRequest;
	}
	
	public HashMap<Integer, String> getIsoRequestMap() {
		return this.isoRequestMap;
	}
	
	public void setIsoRequestHex(String reqAsHex) {
		this.isoRequestHex = reqAsHex;
	}
	
	public String getIsoRequestHex() {
		return this.isoRequestHex;
	}
	
	public HashMap<Integer, String> getIsoResponseMap() {
		return this.isoResponseMap;
	}
	

}
