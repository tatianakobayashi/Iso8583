package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
	// HashMap with all of the possible fields in a Request/Response
	private HashMap<Integer, DataElement> dataElements;
	// The existing fields and values in the request
	private HashMap<Integer, String> isoRequest;
	//
	ArrayList<Integer> keysList = new ArrayList<Integer>();

//	private DataElementMap dataElements;

	// Class builder
	public Parser() {
//		buildDataElementMap();
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

		isoRequest.put(0, mti);

		isoRequest.put(39, responseCode);
	}

	// Sets the 63th bit's value
	public void setBit63(String status) {
		isoRequest.put(63, status);
	}

	// Sets the date field (7) with a string with month, day, hour, minutes and
	// seconds, 10 characters total. The String must already be formatted
	public void setDate(String date) {
		isoRequest.put(7, date);
	}

	// Sets the STAN field(11)
	public void setAuditNumber(int auditNumber) {
		isoRequest.put(123, String.format("%06d", auditNumber));
	}

	// Sets the 123rd field with a threadName
	public void setThreadName(String threadName) {
		isoRequest.put(123, threadName);
	}

	// Removes bits from the HashMap
	public void unsetBitsForResponse() {
		isoRequest.remove(62);
		isoRequest.remove(120);
		isoRequest.remove(114);
		isoRequest.remove(115);
		isoRequest.remove(119);
		isoRequest.remove(121);
	}

	// Returns a HashMap with all the dataFields from the request
	public HashMap<Integer, String> getIsoRequestMap() {
		return isoRequest;
	}

	// Creates a String from a formatted Iso message
	public void packIsoMsg(String isoMsg) {
		// Splitting formatted Iso message
		isoMsg = isoMsg.replace('"', ' ').replace('{', ' ').replace('}', ' ').trim();

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
		unsetBitsForResponse();

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

				if ((field != 63 || field != 62) && dataElements.get(field).getType() != "n") {
					auxString = asciiToHex(auxString);
				}

				if (field != 125) {
					packedMsg += String.format("%04d", sizeOfField);
				} else {
					packedMsg += String.format("%02d", sizeOfField);
				}
				packedMsg += auxString;
			} else {
				// Fixed size fields
				if (dataElements.get(field).getType() != "n") {
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

	// Creates the bitmap
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

	// Creates a formatted String from an unformatted message String
	public String unpackIsoMsg(String isoMsg) {
		String formattedMsg = "{\n";
		String quote = "\"";

//		System.out.println("[unpackIsoMsg] isoMsg: " + isoMsg);

		// Extract MTI Code
		String mtiCode = isoMsg.substring(0, 4);
		System.out.println("[unpackIsoMsg] MTI: " + mtiCode);

		// Initialize final String
		formattedMsg += quote + "000" + quote + ":" + quote + mtiCode + quote;

		// Extract the first bitmap
		String firstBitmap = isoMsg.substring(4, 20);
		List<Integer> elementList = decodeBitmap(firstBitmap, 0);

		// If it exists, extract the second bitmap
		int lastPosition = 20;
		if (elementList.get(0) == 1) {
			String secondBitmap = isoMsg.substring(20, 36);
			elementList.addAll(decodeBitmap(secondBitmap, 64));
			elementList.remove(0);
			lastPosition = 36;
		}

		System.out.println("[unpackIsoMsg] Element List: " + elementList);

		String hasAnotherLine = ",\n";

		// Extracts the fields' values
		String elements = isoMsg.substring(lastPosition);

		// Save values in HashMap
		getDataElements(elementList, elements);

		// Build final String
		for (Integer element : elementList) {
			formattedMsg += hasAnotherLine + quote + String.format("%03d", element) + quote + ":" + quote
					+ isoRequest.get(element) + quote;
		}

		// Save MTI Code
		isoRequest.put(0, mtiCode);

		return formattedMsg + "\n}";
	}

	// Extracts the fields values from the message String
	private void getDataElements(List<Integer> elementList, String unformattedMsg) {
		isoRequest = new HashMap<Integer, String>();

		String auxValue;
		Integer len, lenSize;

		for (Integer element : elementList) {
			DataElement dataElement = dataElements.get(element);
			if (dataElement.getVariable()) {
				// Variable size
				if (dataElement.getCode() != 125) {
					len = Integer.parseInt(unformattedMsg.substring(0, 4));
					lenSize = 4;
				} else {
					// Field 125 has only 2 digits of field size
					len = Integer.parseInt(unformattedMsg.substring(0, 2));
					lenSize = 2;
				}
				//
				len *= 2;
				// Cuts out the length field
				unformattedMsg = unformattedMsg.substring(lenSize);
				// Gets the field value
				auxValue = unformattedMsg.substring(0, len);
				// Cuts out the field value from the original string
				unformattedMsg = unformattedMsg.substring(len);

				if ((dataElement.getCode() != 63 || dataElement.getCode() != 62) && dataElement.getType() != "n") {
					// Converts from hex to ASCII text
					auxValue = hexToASCII(auxValue);
				}
			} else {
				// Fixed size
				// (Size * 2) when the element is a hexadecimal value
				if (dataElement.getType() != "n") {
					// gets the field value
					auxValue = unformattedMsg.substring(0, dataElement.getSize() * 2);
					// cuts the field value from the original string
					unformattedMsg = unformattedMsg.substring(dataElement.getSize() * 2);
					// converts the value from hex to ascii
					auxValue = hexToASCII(auxValue);
				} else {
					// gets the field value
					auxValue = unformattedMsg.substring(0, dataElement.getSize());
					// cuts the field value from the original string
					unformattedMsg = unformattedMsg.substring(dataElement.getSize());
				}
			}
			isoRequest.put(element, auxValue);
		}
	}

	// Decodes the bitmap into the existing fields codes
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

//		System.out.println("[decodeBitmap] " + binary);

		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '1') {
				elementList.add(i + 1 + firstPosition);
			}
		}

		return elementList;
	}

	//
	public String concatMsg() {
		String packedMsg = "";
		// Appending MTI to our final string
		packedMsg += isoRequest.get(keysList.get(0));
		// Creating bitMap
		packedMsg += createBitmap(keysList);
		// Adding data elements
		packedMsg += packDataElements(keysList);
		// Returning packed Iso
		return packedMsg;
	}

	// Converts a byte array into a Hexadecimal String
	public String bytesToText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	// Converts a Hexadecimal String into a byte array
	public byte[] textToBytes(String hex) {
		int len = hex.length();

		byte teste[] = new byte[len / 2 + 2];

		teste[0] = (byte) ((byte) (len / 2 >> 8) & 0x000000ff);
		teste[1] = (byte) (byte) (len / 2 & 0x000000ff);

		int pos = 2;

		for (int i = 0; i < len; i += 2) {

			int end = i + 2;
			if (end > len) {
				end = len;
			}

			String substring = hex.substring(i, end);
			if (substring != null || substring != "" || substring != "\n") {
				int c = Integer.parseInt(substring, 16);
				teste[pos] = (byte) c;
			}
			pos++;
		}
		return teste;
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

	// Converts a ASCII String to an hexadecimal String
	public String asciiToHex(String text) {
		char[] charText = text.toCharArray();

		// Iterate over char array and cast each element to Integer.
		StringBuilder builder = new StringBuilder();

		for (char c : charText) {
			int i = (int) c;
			// Convert integer value to hex
			builder.append(Integer.toHexString(i).toUpperCase());
		}
		return builder.toString();
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
}
