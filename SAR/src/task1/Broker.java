package task1;

import java.util.HashMap;

public class Broker {
	
	String name;	
	HashMap<Integer, RdV> connections;
	BrokerManager brokermanager;
	
	public Broker(String name) {
		this.name = name;
		brokermanager = BrokerManager.getSelf();
		connections = new HashMap<Integer, RdV>();
		brokermanager.addBroker(this);
	}
	
	public Channel connect(String name, int port) throws InterruptedException {
		Broker broker = (Broker) brokermanager.getBroker(name);
		if (broker == null)
			return null;
		return broker._connect(this, port); //Find the accepting broker and go there for the rendez-vous
	}
	
	public Channel accept(int port) throws IllegalStateException, InterruptedException{
		RdV rdv = null;
		synchronized (connections) {
			rdv = connections.get(port);
			if (rdv != null)
				throw new IllegalStateException("Port " + port + " already acepting...");
			rdv = new RdV();
			connections.put(port, rdv); //add the rendez-vous object for the connect to find and go within that rendez-vous to wait
			connections.notifyAll();
		}
		Channel ch;
		ch = rdv.accept(this, port); //add the rendez-vous object for the connect to find and go within that rendez-vous to wait
		return ch;
	}
	private Channel _connect(Broker bc, int port) throws InterruptedException {
		RdV rdv = null;
		synchronized (connections) {
			rdv = connections.get(port);
			while (rdv == null) {
				try {
					connections.wait();
				} catch (InterruptedException ex) {
					//Nothing to do here.
				}
				rdv = connections.get(port);
			}
			connections.remove(port); //Very important to remove it here, on the connect side and of course within the synchronized block
		}
		return rdv.connect(bc, port);
	}
	
}


/* Code exemple

public class BrokerImpl extends Broker {
	BrokerManager bm;
	HashMap<integer, RendezVous> accepts;
	
	public BrokerImpl(String name) {
		super(name);
		accepts = new HashMap<Integer, RendezVous>();
		bm = BrokerManager.getSelf(); //Get the broker manager and add myself as a new broker
		bm.add(this);
	}
	
	@Override
	public Channel connect(String name, int port) {
		BrokerImpl b = (BrokerImpl) bm.get(name);
		if (b == null)
			return null;
		return b._connect(this, port); //Find the accepting broker and go there for the rendez-vous
	}
	
	@Override
	pubic Chanel accept(int port) {
		RendezVous rdv = null;
		synchronized (accepts) {
			rdv = accepts.get(port);
			if (rdv != null)
				throw new IllegalStateException("Port " + port + " already acepting...");
			rdv = new RendezVous();
			accepts.put(port, rdv); //add the rendez-vous object for the connect to find and go within that rendez-vous to wait
			accepts.notifyAll();
		}
		Channel ch;
		ch = rdv.accept(this, port); //add the rendez-vous object for the connect to find and go within that rendez-vous to wait
		return ch;
	}
	
	private Channel _connect(BrokerImpl b, int port) {
		RendezVous rdv = null;
		synchronized (accepts) {
			rdv = accepts.get(port);
			while (rdv == null) {
				try {
					accepts.wait();
				} catch (InterruptedException ex) {
					//Nothing to do here.
				}
				rdv = accepts.get(port);
			}
			accepts.remove(port); //Very important to remove it here, on the connect side and of course within the synchronized block
		}
		return rdv.connect(b, port);
	}
	*/