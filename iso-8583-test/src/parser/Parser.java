package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Parser {
	// HashMap with all of the possible fields in a Request/Response
	private static final HashMap<Integer, DataElement> dataElements = new HashMap<Integer, DataElement>();
	// The existing fields and values in the request
	private HashMap<Integer, String> isoRequest;
	//
	ArrayList<Integer> keysList = new ArrayList<Integer>();

	// Class builder
	public Parser() {
		buildDataElementMap();
	}

	// If the dataElements Hash Map is empty, fill it with the fields
	private void buildDataElementMap() {
		if (dataElements.isEmpty()) {
			dataElements.put(1, new DataElement(1, 64, false, 0, "b"));
			dataElements.put(2, new DataElement(2, 19, true, 2, "n"));
			dataElements.put(3, new DataElement(3, 6, false, 0, "n"));
			dataElements.put(4, new DataElement(4, 12, false, 0, "n"));
			dataElements.put(5, new DataElement(5, 12, false, 0, "n"));
			dataElements.put(6, new DataElement(6, 12, false, 0, "n"));
			dataElements.put(7, new DataElement(7, 10, false, 0, "n"));
			dataElements.put(8, new DataElement(8, 8, false, 0, "n"));
			dataElements.put(9, new DataElement(9, 8, false, 0, "n"));
			dataElements.put(10, new DataElement(10, 8, false, 0, "n"));
			dataElements.put(11, new DataElement(11, 6, false, 0, "n"));
			dataElements.put(12, new DataElement(12, 6, false, 0, "n"));
			dataElements.put(13, new DataElement(13, 4, false, 0, "n"));
			dataElements.put(14, new DataElement(14, 4, false, 0, "n"));
			dataElements.put(15, new DataElement(15, 4, false, 0, "n"));
			dataElements.put(16, new DataElement(16, 4, false, 0, "n"));
			dataElements.put(17, new DataElement(17, 4, false, 0, "n"));
			dataElements.put(18, new DataElement(18, 4, false, 0, "n"));
			dataElements.put(19, new DataElement(19, 3, false, 0, "n"));
			dataElements.put(20, new DataElement(20, 3, false, 0, "n"));
			dataElements.put(21, new DataElement(21, 3, false, 0, "n"));
			dataElements.put(22, new DataElement(22, 3, false, 0, "n"));
			dataElements.put(23, new DataElement(23, 3, false, 0, "n"));
			dataElements.put(24, new DataElement(24, 3, false, 0, "n"));
			dataElements.put(25, new DataElement(25, 2, false, 0, "n"));
			dataElements.put(26, new DataElement(26, 2, false, 0, "n"));
			dataElements.put(27, new DataElement(27, 1, false, 0, "n"));
			dataElements.put(28, new DataElement(28, 8, false, 0, "x+n"));
			dataElements.put(29, new DataElement(29, 8, false, 0, "x+n"));
			dataElements.put(30, new DataElement(30, 8, false, 0, "x+n"));
			dataElements.put(31, new DataElement(31, 8, false, 0, "x+n"));
			dataElements.put(32, new DataElement(32, 11, true, 2, "n"));
			dataElements.put(33, new DataElement(33, 11, true, 2, "n"));
			dataElements.put(34, new DataElement(34, 28, true, 2, "ns"));
			dataElements.put(35, new DataElement(35, 37, true, 2, "z"));
			dataElements.put(36, new DataElement(36, 104, true, 3, "n"));
			dataElements.put(37, new DataElement(37, 12, false, 0, "an"));
			dataElements.put(38, new DataElement(38, 6, false, 0, "an"));
			dataElements.put(39, new DataElement(39, 2, false, 0, "an"));
			dataElements.put(40, new DataElement(40, 3, false, 0, "an"));
			dataElements.put(41, new DataElement(41, 8, false, 0, "ans"));
			dataElements.put(42, new DataElement(42, 15, false, 0, "ans"));
			dataElements.put(43, new DataElement(43, 40, false, 0, "ans"));
			dataElements.put(44, new DataElement(44, 25, true, 2, "an"));
			dataElements.put(45, new DataElement(45, 76, true, 2, "an"));
			dataElements.put(46, new DataElement(46, 999, true, 3, "an"));
			dataElements.put(47, new DataElement(47, 999, true, 3, "an"));
			dataElements.put(48, new DataElement(48, 999, true, 3, "an"));
			dataElements.put(49, new DataElement(49, 3, false, 0, "a or n"));
			dataElements.put(50, new DataElement(50, 3, false, 0, "a or n"));
			dataElements.put(51, new DataElement(51, 3, false, 0, "a or n"));
			dataElements.put(52, new DataElement(52, 64, false, 0, "b"));
			dataElements.put(53, new DataElement(53, 16, false, 0, "n"));
			dataElements.put(54, new DataElement(54, 120, true, 3, "an"));
			dataElements.put(55, new DataElement(55, 999, true, 3, "ans"));
			dataElements.put(56, new DataElement(56, 999, true, 3, "ans"));
			dataElements.put(57, new DataElement(57, 999, true, 3, "ans"));
			dataElements.put(58, new DataElement(58, 999, true, 3, "ans"));
			dataElements.put(59, new DataElement(59, 999, true, 3, "ans"));
			dataElements.put(60, new DataElement(60, 999, true, 3, "ans"));
			dataElements.put(61, new DataElement(61, 999, true, 3, "ans"));
			dataElements.put(62, new DataElement(62, 999, true, 3, "ans"));
			dataElements.put(63, new DataElement(63, 999, true, 3, "ans"));
			dataElements.put(64, new DataElement(64, 64, false, 0, "b"));
			dataElements.put(65, new DataElement(65, 1, false, 0, "b"));
			dataElements.put(66, new DataElement(66, 1, false, 0, "n"));
			dataElements.put(67, new DataElement(67, 2, false, 0, "n"));
			dataElements.put(68, new DataElement(68, 3, false, 0, "n"));
			dataElements.put(69, new DataElement(69, 3, false, 0, "n"));
			dataElements.put(70, new DataElement(70, 3, false, 0, "n"));
			dataElements.put(71, new DataElement(71, 4, false, 0, "n"));
			dataElements.put(72, new DataElement(72, 4, false, 0, "n"));
			dataElements.put(73, new DataElement(73, 6, false, 0, "n"));
			dataElements.put(74, new DataElement(74, 10, false, 0, "n"));
			dataElements.put(75, new DataElement(75, 10, false, 0, "n"));
			dataElements.put(76, new DataElement(76, 10, false, 0, "n"));
			dataElements.put(77, new DataElement(77, 10, false, 0, "n"));
			dataElements.put(78, new DataElement(78, 10, false, 0, "n"));
			dataElements.put(79, new DataElement(79, 10, false, 0, "n"));
			dataElements.put(80, new DataElement(80, 10, false, 0, "n"));
			dataElements.put(81, new DataElement(81, 10, false, 0, "n"));
			dataElements.put(82, new DataElement(82, 12, false, 0, "n"));
			dataElements.put(83, new DataElement(83, 12, false, 0, "n"));
			dataElements.put(84, new DataElement(84, 12, false, 0, "n"));
			dataElements.put(85, new DataElement(85, 12, false, 0, "n"));
			dataElements.put(86, new DataElement(86, 16, false, 0, "n"));
			dataElements.put(87, new DataElement(87, 16, false, 0, "n"));
			dataElements.put(88, new DataElement(88, 16, false, 0, "n"));
			dataElements.put(89, new DataElement(89, 16, false, 0, "n"));
			dataElements.put(90, new DataElement(90, 42, false, 0, "n"));
			dataElements.put(91, new DataElement(91, 1, false, 0, "an"));
			dataElements.put(92, new DataElement(92, 2, false, 0, "an"));
			dataElements.put(93, new DataElement(93, 5, false, 0, "an"));
			dataElements.put(94, new DataElement(94, 7, false, 0, "an"));
			dataElements.put(95, new DataElement(95, 42, false, 0, "an"));
			dataElements.put(96, new DataElement(96, 64, false, 0, "b"));
			dataElements.put(97, new DataElement(97, 16, false, 0, "x + n"));
			dataElements.put(98, new DataElement(98, 25, false, 0, "ans"));
			dataElements.put(99, new DataElement(99, 11, true, 2, "n"));
			dataElements.put(100, new DataElement(100, 11, true, 2, "n"));
			dataElements.put(101, new DataElement(101, 17, true, 2, "ans"));
			dataElements.put(102, new DataElement(102, 28, true, 2, "ans"));
			dataElements.put(103, new DataElement(103, 28, true, 2, "ans"));
			dataElements.put(104, new DataElement(104, 100, true, 3, "ans"));
			dataElements.put(105, new DataElement(105, 999, true, 3, "ans"));
			dataElements.put(106, new DataElement(106, 999, true, 3, "ans"));
			dataElements.put(107, new DataElement(107, 999, true, 3, "ans"));
			dataElements.put(108, new DataElement(108, 999, true, 3, "ans"));
			dataElements.put(109, new DataElement(109, 999, true, 3, "ans"));
			dataElements.put(110, new DataElement(110, 999, true, 3, "ans"));
			dataElements.put(111, new DataElement(111, 999, true, 3, "ans"));
			dataElements.put(112, new DataElement(112, 999, true, 3, "ans"));
			dataElements.put(113, new DataElement(113, 999, true, 3, "ans"));
			dataElements.put(114, new DataElement(114, 999, true, 3, "ans"));
			dataElements.put(115, new DataElement(115, 999, true, 3, "ans"));
			dataElements.put(116, new DataElement(116, 999, true, 3, "ans"));
			dataElements.put(117, new DataElement(117, 999, true, 3, "ans"));
			dataElements.put(118, new DataElement(118, 999, true, 3, "ans"));
			dataElements.put(119, new DataElement(119, 999, true, 3, "ans"));
			dataElements.put(120, new DataElement(120, 999, true, 3, "ans"));
			dataElements.put(121, new DataElement(121, 999, true, 3, "ans"));
			dataElements.put(122, new DataElement(122, 999, true, 3, "ans"));
			dataElements.put(123, new DataElement(123, 999, true, 3, "ans"));
			dataElements.put(124, new DataElement(124, 999, true, 3, "ans"));
			dataElements.put(125, new DataElement(125, 999, true, 3, "ans"));
			dataElements.put(126, new DataElement(126, 999, true, 3, "ans"));
			dataElements.put(127, new DataElement(127, 999, true, 3, "ans"));
			dataElements.put(128, new DataElement(128, 64, false, 0, "b"));
		}
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

	// Returns a HashMap with all the dataFields from the request
	public HashMap<Integer, String> getIsoRequestMap() {
		return isoRequest;
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
				
				if(field != 125) {
					packedMsg += String.format("%04d", sizeOfField);
				}
				else {
					packedMsg += String.format("%02d", sizeOfField);
				}
//				packedMsg += String.format("%0" + dataElements.get(field).getMaxNumberOfDigits() + "d", sizeOfField);
				packedMsg += auxString;
			} else {
				// Fixed size fields
				if (field == 42 || field == 37) {
					String aux= isoRequest.get(field);
					aux = asciiToHex(aux);
					packedMsg += aux;
				}
				else {
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

//		System.out.println(keysList);
//		System.out.println("Length: " + len + " bitMaskSize: " + bitMaskSize);

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

//		System.out.println("isoMsg: " + isoMsg);

		String mtiCode = isoMsg.substring(0, 4);
		System.out.println("MTI: " + mtiCode);

		formattedMsg += quote + "000" + quote + ":" + quote + mtiCode + quote;

		String firstBitmap = isoMsg.substring(4, 20);
		List<Integer> elementList = decodeBitmap(firstBitmap, 0);

		int lastPosition = 20;
		if (elementList.get(0) == 1) {
			String secondBitmap = isoMsg.substring(20, 36);
			elementList.addAll(decodeBitmap(secondBitmap, 64));
			elementList.remove(0);
			lastPosition = 36;
		}

		System.out.println("Element List: " + elementList);

		String hasAnotherLine = ",\n";

		String elements = isoMsg.substring(lastPosition);

//		System.out.println("Elements: " + elements);

		getDataElements(elementList, elements);

		for (Integer element : elementList) {
			formattedMsg += hasAnotherLine + quote + String.format("%03d", element) + quote + ":" + quote
					+ isoRequest.get(element) + quote;
		}

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
//			System.out.println(dataElement);
			if (dataElement.getVariable()) {
				// Variable size

//				System.out.println("maxNumber: " +  dataElement.getMaxNumberOfDigits());
//				System.out.println(dataElement);
//				System.out.println(unformattedMsg);

				// If the field size number doesn't have a fixed length
//				len = Integer.parseInt(unformattedMsg.substring(0, dataElement.getMaxNumberOfDigits()))
//						+ dataElement.getMaxNumberOfDigits();

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

				if (dataElement.getCode() != 63 || dataElement.getCode() != 62) {
					// Converts from hex to ASCII text
					auxValue = hexToASCII(auxValue);
				}

//				System.out.println(len + " " + auxValue);
			} else {
				// Fixed size
//				System.out.println(dataElement);
				// (Size * 2) when the element is a hexadecimal value
				if (dataElement.getCode() == 42 || dataElement.getCode() == 37) {
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
//			 System.out.println("auxValue: " + auxValue);
			isoRequest.put(element, auxValue);
		}
	}

	// Decodes the bitmap into the existing fields codes
	private List<Integer> decodeBitmap(String bitmap, Integer firstPosition) {
		List<Integer> elementList = new ArrayList<Integer>();

		String binary = "";

		for (int i = 4; i <= bitmap.length(); i += 4) {
			String sub = bitmap.substring(i - 4, i);
//			System.out.println(sub);
			Integer decimal = Integer.parseInt(sub, 16);

			String aux = Integer.toBinaryString(decimal);

			while (aux.length() < 16) {
				aux = "0" + aux;
			}

			binary += aux;
		}

//		System.out.println(binary);

		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '1') {
				elementList.add(i + 1 + firstPosition);
			}
		}

		return elementList;
	}

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

	public String bytesToText(Byte bytes[]) {
		StringBuilder sb = new StringBuilder();
		for (Byte b : bytes) {
			if(b != null)
				sb.append(String.format("%02X", b));
		}
//		System.out.println(sb.toString());
		return sb.toString();
	}

	// New
	public Byte[] textToBytes(String hex) {
		int len = hex.length();
		Byte[] bytes = new Byte[(len / 2) + 2];
		
		bytes[0] = (byte) ((byte) ((len/2)  >> 8)& 0x000000ff);
		bytes[1] = (byte) ((byte) (len/2) & 0x000000ff);		
		
		for (int i = 2; i < len; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
		}

		return bytes;
	}

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

	// New
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
	
	public List parse63() {
		String campo63 = new String(isoRequest.get(63));
		List<String> valores = new ArrayList<String>();
		int i = 0;
		int tam;
		int inicio = 0;
		int fim = 2;
		String aux = new String("");
		while(fim < campo63.length()) {
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
