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
		
		String req = "0800A33800800840000600000000000063BA000001080910495600000001000458104956080904303030303030303030"
				+ "3639393030303030303030303036323930300036303030303030303034313030303030303031303030363239303030303030"
				+ "3030323935350012313030382F30382F313936350083303830454031353139322E3136382E3034312E313035303530323030"
				+ "3030313131353030302E3030302E3030302E30303031353030302E3030302E3030302E30303031353030302E3030302E303030"
				+ "2E3030300012313037392E3032302D31333000183139322E3136382E34312E39353A34383534018130343038303030363030"
				+ "303036333130303830383132333930373036303030343535303030323031303230303135303030303030303030303632393030"
				+ "7C3034303830303036303030303031313030383039303831313539303630303034353630303032303630303135303030303030"
				+ "3030303036323930307C3034303830303036303030303031313030383039303831333130303630303034353730303032303630"
				+ "303135303030303030303030303632393030000334353700163239353030333237333035363032303200203030303030303030"
				+ "3030303030303030303030300830362E30332E333200143230313930383039313033303536";
		
//		byte bytes[] = parser.textToBytes("00AE");
		byte bytes[] = parser.textToBytes(req);
		String a = parser.bytesToText(bytes);
		
		
		System.out.println("[MAIN] " + a);
		
//		Byte c = Byte.parseByte("-81",  16);
//		System.out.println("[MAIN]" + c);
//		System.out.println(Integer.toBinaryString(c));
		
		
//		String b = getString(bytes[2], bytes[3]);
//		System.out.println("[MAIN] b = " + b);
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
