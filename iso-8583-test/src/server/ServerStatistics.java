package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ServerStatistics {
	private HashMap<String, Integer> transactionsByThread = new HashMap<String, Integer>();
	private int numberOfConections = 0;
	// TODO: add time lived (thread)

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

	public void printStatistics() {
		System.out.println("Número de conexões: " + numberOfConections);
		System.out.println("Média de transações por conexão: " + getMeanNumberOfTransactions());
		System.out.println("Total de transações: " + getTotalTransactions());
	}
}
