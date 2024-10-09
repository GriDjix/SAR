package task2;

import task1.Broker;
import task1.Channel;

public class QueueBroker {
	
	Broker broker;
	
	public QueueBroker(Broker b) {
		broker = b;
	}
	
	String name() {
		return broker.getName();
	}
		
	public MessageQueue accept(int port) throws IllegalStateException, InterruptedException {
		Channel channel = broker.accept(port);
		return new MessageQueue(channel, broker); 
	}
	public MessageQueue connect(String name, int port) throws InterruptedException {
		Channel channel = broker.connect(name, port);
		return new MessageQueue(channel, broker);
	}
	
}
