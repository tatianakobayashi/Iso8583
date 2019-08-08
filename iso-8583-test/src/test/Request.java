package test;

import parser.Parser;

public class Request {
	public static void main(String[] args) {
//		Calendar c = Calendar.getInstance();
//		System.out.println("Data e hora atual: " + c.getTime());
//		System.out.println("Ano: " + c.get(Calendar.YEAR));
//		System.out.println("Mes: " + c.get(Calendar.MONTH));
//		System.out.println("Dia: " + c.get(Calendar.DAY_OF_MONTH));
//		System.out.println("Horas: "  +  c.get(Calendar.HOUR_OF_DAY));
//		System.out.println("Minutos: "  + c.get(Calendar.MINUTE) );
//		System.out.println("Segundos: " + c.get(Calendar.SECOND));
//		System.out.println(String.format("%02d%02d%02d%02d%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),c.get(Calendar.SECOND)));

		Parser parser = new Parser();
		
		byte bytes[] = parser.textToBytes("00AE");
		String a = parser.bytesToText(bytes);
		
		System.out.println("[MAIN] " + a);
		
		String b = getString(bytes[2], bytes[3]);
		System.out.println("[MAIN] b = " + b);
//		System.out.println("[MAIN] " + parser.bytesToText());
		
		
	
	}
	
	private int getReqLen(byte a, byte b) {
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
	
	private static String getString(byte a, byte b) {
		StringBuilder sb = new StringBuilder();
		Parser parser = new Parser();
		
		sb.append(String.format("%02X", a));
		sb.append(String.format("%02X", b));
		
		String hex = sb.toString();
		
		int c = Integer.parseInt(hex, 16);
		
		System.out.println("[getString] " + hex);
		
		return parser.hexToASCII(hex);
	}
}
