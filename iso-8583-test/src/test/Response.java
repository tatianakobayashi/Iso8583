package test;
public class Response {
	public static void main(String[] args) {
//		int x = 16*16;
//		String hex = Integer.toHexString(x);
//		System.out.println("Hex = " + hex + " " + hex.length());
		
		String binary = "01000010"; 
		int bin = Integer.parseInt(binary, 2);
		 
		System.out.println("Decimal = " + bin + " binary = "  + binary + " hexadecimal = " + Integer.toHexString(bin));
	}
}
