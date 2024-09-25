package task1;

public class Broker {
	
	String name;	
	RdV rdv[];
	BrokerManager brokermanager;
	
	Broker(String name) {
		this.name = name;
	}
	Channel accept(int port);
	Channel connect(String name, int port);
}
