package test;

import java.util.Calendar;

public class Request {
	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		System.out.println("Data e hora atual: " + c.getTime());
		System.out.println("Ano: " + c.get(Calendar.YEAR));
		System.out.println("Mes: " + c.get(Calendar.MONTH));
		System.out.println("Dia: " + c.get(Calendar.DAY_OF_MONTH));
		System.out.println("Horas: "  +  c.get(Calendar.HOUR_OF_DAY));
		System.out.println("Minutos: "  + c.get(Calendar.MINUTE) );
		System.out.println("Segundos" + c.get(Calendar.SECOND));
	}
}
