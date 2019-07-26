package test;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class PrintThread implements Runnable{
	private Scanner input;
	private Boolean keepOpen;
	private PrintWriter output;
	
	public PrintThread(Scanner input, OutputStream outputStream) {
		this.input = input;
		this.keepOpen = true;
		output = new PrintWriter(outputStream);
	}
	
	public void run() {
		System.out.println("Start run");
		while (/* input.hasNextLine() && */keepOpen) {
			String msg = input.nextLine();
			output.println(msg);
//			System.out.println(msg);
			keepOpen = !msg.equalsIgnoreCase("sair");
			
			output.flush();
		}
		output.close();
		input.close();
	}
}
