package parser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ParserISO {
	// HashMap with all of the possible fields in a Request/Response
	private HashMap<Integer, DataElement> dataElements;

	//
	ArrayList<Integer> keysList = new ArrayList<Integer>();

	// Construtor
	public ParserISO() {
		dataElements = DataElementMap.getInstance();
	}

	// Altera MTI para newMTI
	public void setMTI(HashMap<Integer, FieldWrapper> map) {
		byte[] mti = {0x30, 0x38, 0x31, 0x30};
		map.put(0, new FieldWrapper(4, mti));
	}

	// Altera código de resposta (bit 39)
	public void setBit39(HashMap<Integer, String> map) {
		status = new String(status, StandardCharsets.US_ASCII);
		map.put(39, new FieldWrapper(4, ));
	}

	// Altera bit 63 (TODO - colocar como hex)
	public void setBit63(String status, HashMap<Integer, String> map) {
		map.put(63, bytesToHex(status.getBytes()));
	}

	// Cria uma String em hex a partir do map da response
	public byte[] packIsoResponse(HashMap<Integer, FieldWrapper> responseMap) {
		// Array de bytes a ser retornado por esse método (Esse array foi criado com overhead, 
		// precisamos manter o número de bytes preenchidos para criar um array novo com o tamanho
		// correto a ser enviado)
		byte[] packedIsoResponse = new byte[5000];
		// Preenchendo MTI ([0][1][2][3])
		byte[] auxMTI = responseMap.get(0).getConteudo(); 
		for(int i = 0; i < 4; i ++) {
			packedIsoResponse[i] = auxMTI[i];
		}
		// Criando BitMap
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(responseMap.keySet());
		keys.sort(null);
		packedMsg += createBitmap(keys);
		// Adding data elements
		packedMsg += packDataElements(keys, responseMap);
		// Returning packed Iso
		return packedMsg;
	}

	// Creates one String with all of the fields values
	private String packDataElements(List<Integer> fieldsList, HashMap<Integer, String> responseMap) {
		String packedMsg = "";
		// TODO Checar campo a campo
		for (Integer field : fieldsList) {
			if (field == 0 || field == 1)
				continue;
			if (dataElements.get(field).getVariable()) {
				// Variable size fields
				String auxString = responseMap.get(field);
				int sizeOfField = auxString.length() / 2;
				if (field != 125) {
					packedMsg += String.format("%04d", sizeOfField);
				} else {
					packedMsg += String.format("%02d", sizeOfField);
				}
				packedMsg += auxString;
			} else {
				// Fixed size fields
				if (field == 42 || field == 37) {
					String aux = responseMap.get(field);
					packedMsg += aux;
				} else {
					packedMsg += responseMap.get(field);
				}
			}
		}
		return packedMsg;
	}

	// Cria o bitmap para a response
	private byte[] createBitmap(List<Integer> keysList) {
		Integer bitMaskSize = keysList.get(keysList.size() - 1);
		byte[] bitMask;
		int len;
		
		if (bitMaskSize > 64) {
			len = 128;
			bitMask = new byte[16];
			
		} else {
			len = 64;
		}

		char initialMask[] = new char[len];
		Arrays.fill(initialMask, '0');
		for (Integer pos : keysList) {
			if (pos > 0)
				initialMask[pos - 1] = '1';
		}
		if (len == 128) {
			initialMask[0] = '1';
		}
		String binary = new String(initialMask);

		for (int i = 16; i <= len; i += 16) {
			String sub = binary.substring(i - 16, i);
			String subBin = Integer.toHexString(Integer.parseInt(sub, 2)).toUpperCase();

			while (subBin.length() < 4) {
				subBin = "0" + subBin;
			}

			bitMask += subBin;
		}

		return bitMask;
	}

	// Cria um mapa (campo)-->(conteúdo[em bytes]) a partir de uma requestIso
	// que chega em bytes[]
	public void unpackIsoRequest(byte[] isoMsg, HashMap<Integer, FieldWrapper> map) {

		// Obtém código MTI
		map.put(0, new FieldWrapper(4, Arrays.copyOfRange(isoMsg, 0, 4)));

		// Obtém e interpreta o primeiro bitmap
		byte[] firstBitmap = Arrays.copyOfRange(isoMsg, 4, 12);
		List<Integer> elementList = decodeBitmap(firstBitmap, false);

		// Se o segundo bitmap existe obtém e interpreta
		int lastPosition = 12;
		if (elementList.get(0) == 1) {
			byte[] secondBitmap = Arrays.copyOfRange(isoMsg, 12, 20);
			elementList.addAll(decodeBitmap(secondBitmap, true));
			elementList.remove(0);
			lastPosition = 20;
		}

		System.out.println("[unpackIsoRequest]Campos ativos: " + elementList);

		// Obtém o conteúdo dos campos ativos
		byte[] conteudosISO = Arrays.copyOfRange(isoMsg, lastPosition, isoMsg.length);

		// Extrai o conteúdo dos campos ativos e coloca no map
		for (Integer element : elementList) {
			DataElement dataElement = dataElements.get(element);

			// Se o campo tiver tamanho variável
			if (dataElement.getVariable()) {
				// Número de bytes que significam tamanho
				int numberOfDigits = dataElement.getMaxNumberOfDigits();
				// Obtém numberOfDigits bytes que significam o tamanho desse campo
				byte[] leadingBytes = Arrays.copyOfRange(conteudosISO, 0, numberOfDigits);
				int len = 0;
				// Transforma os LLL bytes em ascii e depois em int
				String s = new String(leadingBytes, StandardCharsets.US_ASCII);
				Integer sizeOfField = Integer.parseInt(s);
				
				// Obtém o conteudo do campo com sizeOfField bytes
				byte[] conteudoCampo = Arrays.copyOfRange(conteudosISO, numberOfDigits, numberOfDigits + sizeOfField);
				// Insere esse campo no map
				map.put(element, new FieldWrapper(conteudoCampo.length, conteudoCampo));
				// Corta esse campo do array de bytes original conteudosISO
				conteudosISO = Arrays.copyOfRange(conteudosISO, numberOfDigits + sizeOfField, conteudosISO.length);

			// Se o campo tiver tamanho fixo
			} else {
				// Descobre tamanho fixo desse campo
				int size = dataElement.getSize();
				// Insere conteudo do campo no map
				map.put(element, new FieldWrapper(size, Arrays.copyOfRange(conteudosISO, 0, size)));
				// Retira esse campo do array conteudosISO
				conteudosISO = Arrays.copyOfRange(conteudosISO, size, conteudosISO.length);

			}
		}
	}

	// Interpreta o bitmap e gera uma lista com o numero dos campos ativos
	private List<Integer> decodeBitmap(byte[] bitmap, boolean isSecondBitmap) {
		List<Integer> elementList = new ArrayList<Integer>();
		int aux = 0;
		int aux2 = 0;

		for (int i = 0; i < 4; i ++) {
			aux = aux << 8;
			aux = aux | bitmap[i];
			aux2 = aux2 << 8;
			aux2 = aux2 | bitmap[i+4];
		}

		String binaryString = Integer.toBinaryString(aux);
		binaryString += Integer.toBinaryString(aux2);
		char[] binary = binaryString.toCharArray();
		
		int activeField = 1;
		if (isSecondBitmap) {
			activeField = 65;
		} 
		
		for (char c: binary) {
			if(c == '1') {
				elementList.add(activeField);
			}
			activeField++;
		}
		
		return elementList;
	}

	// Converte um array de bytes em uma string de hexadecimais
	public String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	// Converte uma string de hex em um array de bytes
	public void hexToBytes(String hex, byte[] bytes) {
		int tamEmBytes = (hex.length() / 2);

		bytes[0] = (byte) (((byte) (tamEmBytes >> 8)) & 0x000000ff);
		bytes[1] = (byte) ((byte) (tamEmBytes) & 0x000000ff);

		char a;
		byte aux = (byte) 0x00;
		byte aux1 = (byte) 0x00;
		for (int i = 0; i < tamEmBytes; i += 2) {
			aux = (byte) 0x00;

			a = hex.charAt(i);
			aux = charToByte(a);
			a = hex.charAt(i + 1);
			aux1 = charToByte(a);

			aux = (byte) (aux << 4);
			aux = (byte) (aux & ((byte) 0xf0));

			aux1 = (byte) (aux1 & ((byte) 0x0f));

			bytes[i + 2] = (byte) (aux | aux1);
		}
	}

	// Recebe um char e retorna um byte de acordo com seu valor hex
	private byte charToByte(char a) {
		switch (a) {
		case '0':
			return (byte) 0x00;
		case '1':
			return (byte) 0x01;
		case '2':
			return (byte) 0x02;
		case '3':
			return (byte) 0x03;
		case '4':
			return (byte) 0x04;
		case '5':
			return (byte) 0x05;
		case '6':
			return (byte) 0x06;
		case '7':
			return (byte) 0x07;
		case '8':
			return (byte) 0x08;
		case '9':
			return (byte) 0x09;
		case 'A':
			return (byte) 0x0a;
		case 'B':
			return (byte) 0x0b;
		case 'C':
			return (byte) 0x0c;
		case 'D':
			return (byte) 0x0d;
		case 'E':
			return (byte) 0x0e;
		case 'F':
			return (byte) 0x0f;
		default:
			return (byte) 0xff;
		}
	}

	// Interpreta o bit 63 e retorna uma lista com seus conteudos antes concatenados
	public List<String> parse63(HashMap<Integer, FieldWrapper> map) {
		List<String> valores = new ArrayList<String>();
		FieldWrapper campo63 = map.get(63);
		campo63 = hexToASCII(campo63);
		if (campo63 == null)
			return null;

		int tam;
		int inicio = 0;
		int fim = 2;
		while (fim < campo63.length()) {
			tam = Integer.parseInt(campo63.substring(inicio, fim));
			fim += tam;
			inicio += 2;
			valores.add(campo63.substring(inicio, fim));
			fim += 2;
			inicio += tam;
		}
		return valores;
	}

	// Recebe os dois primeiros bytes de uma request e retorna um inteiro que
	// representa o tamanho da mensagem em bytes
	public int getReqLen(byte a, byte b) {
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

	// Recebe dois maps, um da request e outro da response e preenche o da response
	// com os campos adequados (OBS: não adiciona campos como o bit 39 e nem altera
	// o conteúdo dos campos)
	public void makeResponseMap(HashMap<Integer, FieldWrapper> requestMap, HashMap<Integer, FieldWrapper> responseMap) {
		responseMap.putAll(requestMap);
		responseMap.remove(62);
		responseMap.remove(114);
		responseMap.remove(115);
		responseMap.remove(119);
		responseMap.remove(120);
		responseMap.remove(121);
	}

	// Converts a hexadecimal String to an ASCII String
	public String hexToASCII(String text) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length() - 1; i += 2) {
			// grab the hex in pairs
			String output = text.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);
		}
		return sb.toString();
	}
}
