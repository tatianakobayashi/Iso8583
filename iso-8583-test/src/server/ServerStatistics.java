package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ServerStatistics {
	private HashMap<String, Integer> transactionsByThread = new HashMap<String, Integer>();
	private HashMap<String, Float> timeByThread = new HashMap<String, Float>();
	private int numberOfConections = 0;
	private ArrayList<Float> unpackTime = new ArrayList<Float>();
	private ArrayList<Float> packTime = new ArrayList<Float>();

	public void putTransactionsByThread(String threadName, Integer connections) {
		transactionsByThread.put(threadName, connections);
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
		if(timeByThread.isEmpty())
			return 0;
		else{
			float total = 0;
			for (Float time : timeByThread.values()) {
				total += time;
			}
			return total;
		}
	}
	

	private float getMeanTime() {
		if(timeByThread.isEmpty())
			return 0;
		else{
			return getTotalTime()/timeByThread.values().size();
		}
	}
	
	public void newPackTime(float start, float end) {
		packTime.add((end - start)/1000F);
	}
	
	public void newUnpackTime(float start, float end) {
		unpackTime.add((end - start)/1000F);
	}
	
	private float totalPackTime() {
		float total = 0;
		
		for (Float time : packTime) {
			total += time;
		}
		
		return total;
	}
	
	private float totalUnpackTime() {
		float total = 0;
		
		for (Float time : unpackTime) {
			total += time;
		}
		
		return total;
	}

	private float meanPackTime() {
		if(packTime.isEmpty()) {
			return 0;
		}
		else {
			return totalPackTime()/packTime.size();
		}
	}
	
	private float meanUnpackTime() {
		if(unpackTime.isEmpty()) {
			return 0;
		}
		else {
			return totalUnpackTime()/unpackTime.size();
		}
	}

	public void printStatistics() {
		System.out.println("                    ---~~~---");
		System.out.println("Número de conexões: " + numberOfConections);
		System.out.println("                    ---###---");
		System.out.println("Média de transações por conexão: " + getMeanNumberOfTransactions());
		System.out.println("Total de transações: " + getTotalTransactions());
		System.out.println("                    ---###---");
		System.out.println("Média de tempo por conexão: " + getMeanTime());
		System.out.println("Tempo total: " + getTotalTime());
//		System.out.println("                    ---###---");
//		System.out.println("Média de tempo de empacotamento: " + meanPackTime());
//		System.out.println("Total de tempo de empacotamento: " + totalPackTime());
//		System.out.println("                    ---###---");
//		System.out.println("Média de tempo de desempacotamento: " + meanUnpackTime());
//		System.out.println("Total de tempo de desempacotamento: " + totalUnpackTime());
	}

	public void setTimeByThread(String threadName, float start, float end) {
		// TODO Auto-generated method stub
		timeByThread.put(threadName, (end - start)/1000F);
	}
}
