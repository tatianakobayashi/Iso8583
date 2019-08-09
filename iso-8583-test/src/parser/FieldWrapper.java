package parser;

public class FieldWrapper {
	// Tamanho desse campo
	private int tam;
	// Array de bytes referentes ao conteudo desse campo
	private byte[] conteudo;
	
	public FieldWrapper(int len) {
		this.tam = len;
		conteudo = new byte[this.tam];
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
