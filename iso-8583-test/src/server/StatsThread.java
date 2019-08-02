package server;

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
				stats.printStatistics();
			} catch (InterruptedException e) {
				System.out.println("In " + getName());
				e.printStackTrace();
			}
			
		}
	}

}
