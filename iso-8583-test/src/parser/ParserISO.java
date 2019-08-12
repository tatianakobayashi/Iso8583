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
		byte[] mti = { 0x30, 0x38, 0x31, 0x30 };
		map.put(0, new FieldWrapper(4, mti));
	}

	// Altera código de resposta (bit 39)
	public void setBit39(Boolean success, HashMap<Integer, FieldWrapper> map) {
		if (success) {
			byte[] response = { 0x30, 0x30 };
			map.put(39, new FieldWrapper(4, response));
		} else {
			byte[] response = { 0x30, 0x31 };
			map.put(39, new FieldWrapper(4, response));
		}

//		status = new String(status, StandardCharsets.US_ASCII);

	}

	// Altera bit 63 (TODO - colocar como hex)
	public void setBit63(String status, HashMap<Integer, FieldWrapper> map) {

		byte[] statusBytes = status.getBytes(StandardCharsets.US_ASCII);
		map.put(63, new FieldWrapper(statusBytes.length, statusBytes));
	}

	// Cria uma String em hex a partir do map da response
	public byte[] packIsoResponse(HashMap<Integer, FieldWrapper> responseMap) {
		// Array de bytes a ser retornado por esse método (Esse array foi criado com
		// overhead,
		// precisamos manter o número de bytes preenchidos para criar um array novo com
		// o tamanho
		// correto a ser enviado)
//		byte[] packedIsoResponse = new byte[5000];
		// Preenchendo MTI ([0][1][2][3])
		byte[] auxMTI = responseMap.get(0).getConteudo();
//		for (int i = 0; i < 4; i++) {
//			packedIsoResponse[i] = auxMTI[i];
//		}
		// Criando BitMap
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(responseMap.keySet());
		keys.sort(null);

		byte[] bitmap = createBitmap(keys);

//		for (int i = 0; i < bitmap.length; i++) {
//			packedIsoResponse[i + 4] = bitmap[i];
//		}

		// Adding data elements
		byte[] packedDataElements = packDataElements(keys, responseMap);
		
//		int pos = packedIsoResponse.length;
//		for (byte b : packedDataElements) {
//			packedIsoResponse[pos] = b;
//			pos++;
//		}
		
		byte[] packedIsoResponse = new byte[4 + bitmap.length + packedDataElements.length];
		// Copiando MTI
		for (int i = 0; i < 4; i++) {
			packedIsoResponse[i] = auxMTI[i];
		}
		// Copiando bitmap
		for (int i = 0; i < bitmap.length; i++) {
			packedIsoResponse[i + 4] = bitmap[i];
		}
		// Copiando dataElements
		int pos = 4 + bitmap.length;
		for (byte b : packedDataElements) {
			packedIsoResponse[pos] = b;
			pos++;
		}
		
		// Returning packed Iso
		return packedIsoResponse;
	}

	// Creates one String with all of the fields values
	private byte[] packDataElements(List<Integer> fieldsList, HashMap<Integer, FieldWrapper> responseMap) {
		byte[] packedMsg = new byte[5000];
		int packedMsgPosition = 0;
		// TODO Checar campo a campo
		for (Integer field : fieldsList) {
			if (field == 0 || field == 1)
				continue;
			FieldWrapper fieldWrapper = responseMap.get(field);
			if (dataElements.get(field).getVariable()) {
				// Variable size fields
				int sizeOfField = fieldWrapper.getTam() / 2;

				// Format size into String
				String fieldSizeString;
				if (field != 125) {
					fieldSizeString = String.format("%04d", sizeOfField);
				} else {
					fieldSizeString = String.format("%02d", sizeOfField);
				}

				// Copy field size into packedMessage
				byte[] fieldSizeBytes = fieldSizeString.getBytes(StandardCharsets.US_ASCII);
				for (byte b : fieldSizeBytes) {
					packedMsg[packedMsgPosition] = b;
					packedMsgPosition++;
				}
			}
			// Copy field value into packed message

			for (byte b : fieldWrapper.getConteudo()) {
				packedMsg[packedMsgPosition] = b;
				packedMsgPosition++;
			}
		}
		return packedMsg;
	}

	// Cria o bitmap para a response
	private byte[] createBitmap(List<Integer> keysList) {
		Integer bitMaskSize = keysList.get(keysList.size() - 1);
		byte[] bitMask;
		int len;

		// Set bitmask size
		if (bitMaskSize > 64) {
			len = 128;
			bitMask = new byte[16];

		} else {
			len = 64;
			bitMask = new byte[8];
		}

		// Create Binary String
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

		// Convert binary into hexadecimal
		int bitMaskPosition = 0;
		for (int i = 16; i <= len; i += 16) {
			String substring = binary.substring(i - 16, i);
			String substringHex = Integer.toHexString(Integer.parseInt(substring, 2)).toUpperCase();

			while (substringHex.length() < 4) {
				substringHex = "0" + substringHex;
			}

			for (char c : substringHex.toCharArray()) {
				bitMask[bitMaskPosition] = charToByte(c);

				bitMaskPosition++;
			}
		}

		return bitMask;
	}

	// Cria um mapa (campo)-->(conteúdo[em bytes]) a partir de uma requestIso
	// que chega em bytes[]
	public void unpackIsoRequest(byte[] isoMsg, HashMap<Integer, FieldWrapper> map) {
		System.out.println("Full request: " + bytesToHex(isoMsg));
		// Obtém código MTI
		map.put(0, new FieldWrapper(2, Arrays.copyOfRange(isoMsg, 0, 2)));
		
		// Obtém e interpreta o primeiro bitmap
		byte[] firstBitmap = Arrays.copyOfRange(isoMsg, 2, 10);
		List<Integer> elementList = decodeBitmap(firstBitmap, false);
		// Se o segundo bitmap existe obtém e interpreta
		int lastPosition = 10;
		if (elementList.get(0) == 1) {
			byte[] secondBitmap = Arrays.copyOfRange(isoMsg, 10, 18);
			elementList.addAll(decodeBitmap(secondBitmap, true));
			elementList.remove(0);
			lastPosition = 18;
		}

		System.out.println("[unpackIsoRequest]Campos ativos: " + elementList);

		// Obtém o conteúdo dos campos ativos
		byte[] conteudosISO = Arrays.copyOfRange(isoMsg, lastPosition, isoMsg.length);

		// Extrai o conteúdo dos campos ativos e coloca no map
		for (Integer element : elementList) {
			DataElement dataElement = dataElements.get(element);

			// Se o campo tiver tamanho variável
			if (dataElement.getVariable()) {
				// Obtém dois bytes que significam tamanho (1 byte no caso do campo 125 (?)) e retira 
				// esses bytes do buffer
				byte[] leadingBytes;
				if(dataElement.getCode() == 125) {
					 leadingBytes = Arrays.copyOfRange(conteudosISO, 0, 1);
					 conteudosISO = Arrays.copyOfRange(conteudosISO, 1, conteudosISO.length);
				}
				else {
					leadingBytes = Arrays.copyOfRange(conteudosISO, 0, 2);
					conteudosISO = Arrays.copyOfRange(conteudosISO, 2, conteudosISO.length);
				}
				// Transforma os bytes referentes a tamanho para int
				String s = new String(bytesToHex(leadingBytes));
				Integer sizeOfField = Integer.parseInt(s);
				//if(dataElement.getType() == "b")
					//sizeOfField = sizeOfField * 2;

				// Obtém o conteudo do campo com sizeOfField bytes
				byte[] conteudoCampo = Arrays.copyOfRange(conteudosISO, 0, sizeOfField);
				// Insere esse campo no map
				System.out.println("colocando campo " + element + ": " + bytesToHex(conteudoCampo));
				map.put(element, new FieldWrapper(conteudoCampo.length, conteudoCampo));
				// Corta esse campo do array de bytes original conteudosISO
				conteudosISO = Arrays.copyOfRange(conteudosISO, sizeOfField, conteudosISO.length);

				// Se o campo tiver tamanho fixo
			} else {
				// Descobre tamanho fixo desse campo
				int size = 0;
				if(dataElement.getType() == "n") {
					size = dataElement.getSize() / 2;
				}
				else 
					size = dataElement.getSize();
				
				// Insere conteudo do campo no map
				map.put(element, new FieldWrapper(size, Arrays.copyOfRange(conteudosISO, 0, size)));
				System.out.println("colocando campo " + element + ": " + bytesToHex(Arrays.copyOfRange(conteudosISO, 0, size)));
				// Retira esse campo do array conteudosISO
				conteudosISO = Arrays.copyOfRange(conteudosISO, size, conteudosISO.length);

			}
		}
	}

	// Interpreta o bitmap e gera uma lista com o numero dos campos ativos
	private List<Integer> decodeBitmap(byte[] bitmap, boolean isSecondBitmap) {
		List<Integer> elementList = new ArrayList<Integer>();
		String binaryString = "";
		
		for (int i = 0; i < 8; i++) {
			if((bitmap[i] & (byte)0x80) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x40) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x20) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x10) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x08) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x04) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x02) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
			if((bitmap[i] & (byte)0x01) != (byte)0x00) binaryString+= "1"; else binaryString+= "0";
		}
		
		char[] binary = binaryString.toCharArray();
		int activeField = 1;
		if (isSecondBitmap) {
			activeField = 65;
		}

		for (char c : binary) {
			if (c == '1') {
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

		FieldWrapper campo63Bytes = map.get(63);

		String campo63 = bytesToHex(campo63Bytes.getConteudo());

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
