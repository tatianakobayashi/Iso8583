package server;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerStatistics {
	private HashMap<String, Integer> transactionsByThread = new HashMap<String, Integer>();
	
	public void putTransactionsByThread(String threadName, Integer connections) {
		transactionsByThread.put(threadName, connections);
	}
	
	private float getMeanNumberOfTransactions() {
		ArrayList<Integer> numberOfTransactions = (ArrayList<Integer>) transactionsByThread.values();
		
		int sum = 0;
		for (Integer transactions : numberOfTransactions) {
			sum += transactions;
		}
		
		return sum/numberOfTransactions.size();
	}
	
	public void printStatistics() {
		System.out.println("Média de transações por thread: " + getMeanNumberOfTransactions());
	}
}
