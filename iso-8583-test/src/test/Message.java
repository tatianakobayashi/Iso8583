package test;

public class Message {
	
	char mti;
	Byte bitmaps[] = new Byte[8];

	// MTI
	int getISOVersion() {
		return mti / 1000;
	}
	
	int getMessageClass() {
		return (mti % 1000)/100;
	}
	
	int getMessageFunction() {
		return (mti % 100)/10;
	}
	
	int getMessageOrigin() {
		return mti % 10;
	}
	
	void printISOVersion() {
		int n = getISOVersion();
		switch(n) {
		case 0: 
			System.out.println("ISO8583:1987");
			break;
		case 1:
			System.out.println("ISO8583:1993");
			break;
		case 2: 
			System.out.println("ISO8583:2003");
			break;
		case 8:
			System.out.println("National use");
			break;
		case 9:
			System.out.println("Private use");
			break;
		default:
			if(n >= 3 && n <= 7) {
				System.out.println("Reserved by ISO");
			}
			else {
				System.out.println("Invalid version");
			}
		}
	}
	
	void printMessageClass() {
		int n = getMessageClass();
		switch(n) {
		case 1:
			System.out.println("Authorization");
			break;
		case 2: 
			System.out.println("Financial");
			break;
		case 3:
			System.out.println("File actions");
			break;
		case 4:
			System.out.println("Reversal and chargeback");
			break;
		case 5:
			System.out.println("Reconciliation");
			break;
		case 6:
			System.out.println("Administrative");
			break;
		case 7:
			System.out.println("Fee collection");
			break;
		case 8:
			System.out.println("Network management");
			break;
		default:
			if(n == 0 || n == 9) {
				System.out.println("Reserved by ISO");
			}
			else {
				System.out.println("Invalid class");
			}
		}
	}
	
	void printMessageFunction() {
		int n = getMessageFunction();
		switch(n) {
		case 0:
			System.out.println("Request");
			break;
		case 1: 
			System.out.println("Request response");
			break;
		case 2:
			System.out.println("Advice");
			break;
		case 3:
			System.out.println("Advice response");
			break;
		case 4:
			System.out.println("Notification");
			break;
		case 5:
			System.out.println("Notification acknowledgement");
			break;
		case 6:
			System.out.println("Instruction");
			break;
		case 7:
			System.out.println("Instruction acknowledgement");
			break;
		default:
			if(n == 8 || n == 9) {
				System.out.println("Reserved by ISO");
			}
			else {
				System.out.println("Invalid function");
			}
		}
	}
	
	void printMessageOrigin() {
		int n = getMessageOrigin();
		switch(n) {
		case 0:
			System.out.println("Aquirer");
			break;
		case 1: 
			System.out.println("Aquirer repeat");
			break;
		case 2:
			System.out.println("Issuer");
			break;
		case 3:
			System.out.println("Issuer repeat");
			break;
		case 4:
			System.out.println("Other");
			break;
		case 5:
			System.out.println("Other repeat");
			break;
		default:
			if(n >= 6 && n <= 9) {
				System.out.println("Reserved by ISO");
			}
			else {
				System.out.println("Invalid origin");
			}
		}
	}
	
	
}
