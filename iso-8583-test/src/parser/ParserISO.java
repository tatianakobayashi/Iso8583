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

	// Sets the response code(39) and changes the MTI function to a Response
	public void setResponseCode(String responseCode) {
		String mti = isoRequest.get(0);
		int mti_int = Integer.parseInt(mti);

		int function = (mti_int % 100) / 10;

		if (function % 2 == 0) {
			mti_int += 10;
		}
		mti = String.format("%04d", mti_int);

//		System.out.println("New MTI: " + mti);
		isoRequest.put(0, mti);

		isoRequest.put(39, responseCode);
	}

	// Sets the 63rd bit's value
	public void setBit63(String status) {
		isoRequest.put(63, status);
	}

	// Creates a String from a formatted Iso message
	public void packIsoMsg(String isoMsg) {
		// Splitting formatted Iso message
		isoMsg = isoMsg.replace('"', ' ').replace('{', ' ').replace('}', ' ').trim();

		// System.out.println("isoMsg pack: " + isoMsg);

		String splittedMsg[] = isoMsg.split(",");
		List<String> splittedMsgList = Arrays.asList(splittedMsg);
		// Creating hashmap from splitted isoMsg
		isoRequest = new HashMap<Integer, String>(splittedMsgList.size());
		for (String string : splittedMsgList) {
			String splittedLine[] = string.split(":");
			splittedLine[0] = splittedLine[0].trim();
			splittedLine[1] = splittedLine[1].trim();
			Integer auxKey = Integer.parseInt(splittedLine[0]);
			isoRequest.put(auxKey, splittedLine[1]);
			keysList.add(auxKey);
		}
	}

	// Recreates an unformatted String from the isoRequest HashMap
	public String repackIsoMsg() {
		

		// This is the String to be returned
		String packedMsg = "";

		// Appending MTI to our final string
		packedMsg += isoRequest.get(0);
		// Creating bitMap
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(isoRequest.keySet());
		keys.sort(null);
		packedMsg += createBitmap(keys);
		// Adding data elements
		packedMsg += packDataElements(keys);
		// Returning packed Iso
		return packedMsg;
	}

	// Creates one String with all of the fields values
	private String packDataElements(List<Integer> fieldsList) {
		String packedMsg = "";

		for (Integer field : fieldsList) {
			if (field == 0 || field == 1)
				continue;
			if (dataElements.get(field).getVariable()) {
				// Variable size fields
				String auxString = isoRequest.get(field);
				int sizeOfField = auxString.length();

				if (field != 63 || field != 62) {
					auxString = asciiToHex(auxString);
				}

//				System.out.println("Element: " + field);
//				System.out.println("packDataElements maxNumberOfDigits: "  +dataElements.get(field).getMaxNumberOfDigits() + " Length: " + sizeOfField);

				if (field != 125) {
					packedMsg += String.format("%04d", sizeOfField);
				} else {
					packedMsg += String.format("%02d", sizeOfField);
				}
//				packedMsg += String.format("%0" + dataElements.get(field).getMaxNumberOfDigits() + "d", sizeOfField);
				packedMsg += auxString;
			} else {
				// Fixed size fields
				if (field == 42 || field == 37) {
					String aux = isoRequest.get(field);
					aux = asciiToHex(aux);
					packedMsg += aux;
				} else {
					packedMsg += isoRequest.get(field);
				}

			}
		}
		return packedMsg;
	}

	// Cria o bitmap para a response 
	private String createBitmap(List<Integer> keysList) {
		String bitMask = "";
		Integer bitMaskSize = keysList.get(keysList.size() - 1);

		int len;
		if (bitMaskSize > 64) {
			len = 128;
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

	// Cria um mapa (campo)-->(conteúdo) a partir de uma requestIso representada
	// como uma String de hexadecimais
	public void unpackIsoRequest(String isoMsgHex, HashMap<Integer, String> map) {

		// Obtém código MTI
		map.put(0, isoMsgHex.substring(0, 4));

		// Obtém e interpreta o primeiro bitmap
		String firstBitmap = isoMsgHex.substring(4, 20);
		List<Integer> elementList = decodeBitmap(firstBitmap, 0);

		// Se o segundo bitmap existe obtém e interpreta
		int lastPosition = 20;
		if (elementList.get(0) == 1) {
			String secondBitmap = isoMsgHex.substring(20, 36);
			elementList.addAll(decodeBitmap(secondBitmap, 64));
			elementList.remove(0);
			lastPosition = 36;
		}

		System.out.println("[unpackIsoRequest]Campos ativos: " + elementList);

		// Obtém o conteúdo dos campos ativos
		String elements = isoMsgHex.substring(lastPosition);

		// Extrai o conteúdo dos campos ativos e coloca no map
		String auxValue;
		Integer len, lenSize;
		for (Integer element : elementList) {
			DataElement dataElement = dataElements.get(element);
			
			// Se o campo tiver tamanho variável
			if (dataElement.getVariable()) {

				if (dataElement.getCode() != 125) {
					len = Integer.parseInt(elements.substring(0, 4));
					lenSize = 4;
				} else {
					// Field 125 has only 2 digits of field size
					len = Integer.parseInt(elements.substring(0, 2));
					lenSize = 2;
				}
				len *= 2;
				// Cuts out the length field
				elements = elements.substring(lenSize);
				// Gets the field value
				auxValue = elements.substring(0, len);
				// Cuts out the field value from the original string
				elements = elements.substring(len);
				
			// Se o campo tiver tamanho fixo
			} else {
			// (Size * 2) when the element is a hexadecimal value
				if (dataElement.getCode() == 42 || dataElement.getCode() == 37) {
					// gets the field value
					auxValue = elements.substring(0, dataElement.getSize() * 2);
					// cuts the field value from the original string
					elements = elements.substring(dataElement.getSize() * 2);
				} else {
					// gets the field value
					auxValue = elements.substring(0, dataElement.getSize());
					// cuts the field value from the original string
					elements = elements.substring(dataElement.getSize());
				}
			}
			map.put(element, auxValue);
		}
	}

	// Interpreta o bitmap e gera uma lista com o numero dos campos ativos
	private List<Integer> decodeBitmap(String bitmap, Integer firstPosition) {
		List<Integer> elementList = new ArrayList<Integer>();
		String binary = "";

		for (int i = 4; i <= bitmap.length(); i += 4) {
			String sub = bitmap.substring(i - 4, i);
			Integer decimal = Integer.parseInt(sub, 16);
			String aux = Integer.toBinaryString(decimal);

			while (aux.length() < 16) {
				aux = "0" + aux;
			}
			binary += aux;
		}

		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '1') {
				elementList.add(i + 1 + firstPosition);
			}
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
	
	// Separates the values in the 63th bit
	public List<String> parse63() {
		String campo63 = new String(isoRequest.get(63));
		List<String> valores = new ArrayList<String>();
		int i = 0;
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
	public void makeResponseMap(HashMap<Integer, String> requestMap, HashMap<Integer, String> responseMap) {
		responseMap.putAll(requestMap);
		responseMap.remove(62);
		responseMap.remove(114);
		responseMap.remove(115);
		responseMap.remove(119);
		responseMap.remove(120);
		responseMap.remove(121);
	}
}
