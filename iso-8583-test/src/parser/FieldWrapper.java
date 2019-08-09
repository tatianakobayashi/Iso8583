package parser;

import java.util.Arrays;

public class FieldWrapper {
	// Tamanho desse campo
	private int tam;
	// Array de bytes referentes ao conteudo desse campo
	private byte[] conteudo;
	
	public FieldWrapper(int len, byte[] conteudo) {
		this.tam = len;
		this.conteudo = new byte[this.tam];
		this.conteudo = Arrays.copyOf(conteudo, len);
	}

	public int getTam() {
		return tam;
	}

	public void setTam(int tam) {
		this.tam = tam;
	}

	public byte[] getConteudo() {
		return conteudo;
	}
}
