package task1;

public class Task extends Thread {
	
	Broker broker;
	
	Task(Broker b, Runnable r) {
		broker = b;
	}
	
	public Broker getBroker() {
		return broker;
	}

}
