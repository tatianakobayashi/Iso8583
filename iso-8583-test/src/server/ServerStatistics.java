package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ServerStatistics {
	private HashMap<String, Integer> transactionsByThread = new HashMap<String, Integer>();
	private HashMap<String, Float> timeByThread = new HashMap<String, Float>();
	private int numberOfConections = 0;
	private ArrayList<Float> unpackTime = new ArrayList<Float>();
	private ArrayList<Float> packTime = new ArrayList<Float>();

	private Semaphore s = new Semaphore(1);

	public void putTransactionsByThread(String threadName, Integer connections) {
		try {
			s.acquire();
			transactionsByThread.put(threadName, connections);
			s.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void newConnection() {
		numberOfConections++;
	}

	private int getTotalTransactions() {

		if (!transactionsByThread.isEmpty()) {
			Collection<Integer> numberOfTransactions = transactionsByThread.values();

			int sum = 0;
			for (Integer transactions : numberOfTransactions) {
				sum += transactions;
			}

			return sum;
		}

		return 0;
	}

	private float getMeanNumberOfTransactions() {

		if (!transactionsByThread.isEmpty()) {
			Collection<Integer> numberOfTransactions = transactionsByThread.values();

			return getTotalTransactions() / numberOfTransactions.size();
		}
		return 0;

	}

	private float getTotalTime() {

		if (timeByThread.isEmpty()) {

			return 0;
		} else {
			float total = 0;
			for (Float time : timeByThread.values()) {
				total += time;
			}

			return total;
		}

	}

	private float getMeanTime() {
		if (timeByThread.isEmpty()) {
			return 0;
		}

		else {

			int size = timeByThread.values().size();
			return getTotalTime() / size;
		}

	}

	public void newPackTime(float start, float end) {
		packTime.add((end - start));
	}

	public void newUnpackTime(float start, float end) {
		unpackTime.add((end - start));
	}

//	private float totalPackTime() {
//		float total = 0;
//		
//		for (Float time : packTime) {
//			total += time;
//		}
//		
//		return total;
//	}
//	
//	private float totalUnpackTime() {
//		float total = 0;
//		
//		for (Float time : unpackTime) {
//			total += time;
//		}
//		
//		return total;
//	}
//
//	private float meanPackTime() {
//		if(packTime.isEmpty()) {
//			return 0;
//		}
//		else {
//			return totalPackTime()/packTime.size();
//		}
//	}
//	
//	private float meanUnpackTime() {
//		if(unpackTime.isEmpty()) {
//			return 0;
//		}
//		else {
//			return totalUnpackTime()/unpackTime.size();
//		}
//	}

	public void printStatistics() {
		String msg = "                    ---~~~---\n";
		msg += "Número de conexões: " + numberOfConections;
		msg += "\n                    ---###---";

		try {
			s.acquire();
			msg += "\nMédia de transações por conexão: " + getMeanNumberOfTransactions();
			msg += "\nTotal de transações: " + getTotalTransactions();
			msg += "\n                    ---###---";
			msg += "\nMédia de tempo por conexão: " + getMeanTime();
			msg += "\nTempo total: " + getTotalTime();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		s.release();
//		System.out.println("                    ---###---");
//		System.out.println("Média de tempo de empacotamento: " + meanPackTime());
//		System.out.println("Total de tempo de empacotamento: " + totalPackTime());
//		System.out.println("                    ---###---");
//		System.out.println("Média de tempo de desempacotamento: " + meanUnpackTime());
//		System.out.println("Total de tempo de desempacotamento: " + totalUnpackTime());

		System.out.println(msg);
	}

	public void setTimeByThread(String threadName, float start, float end) {
		// TODO Auto-generated method stub
		try {
			s.acquire();
			timeByThread.put(threadName, (end - start) );
			s.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
