package server;

import java.util.Calendar;

public class StatsThread extends Thread {
	private ServerStatistics stats;
	
	public StatsThread(ServerStatistics stats) {
		this.stats = stats;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(5000);
				System.out.println("\n" + Calendar.getInstance().getTime());
				stats.printStatistics();
			} catch (InterruptedException e) {
				System.out.println("In " + getName());
				e.printStackTrace();
			}
			
		}
	}

}
