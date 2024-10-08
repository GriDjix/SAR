package task1;

public class RdV {
	Broker ba, bc;
	Channel ca, cc;
	
	private void _wait() {
		while (ca == null || cc == null) {
			try {
				wait();
			} catch (InterruptedException ex) {
				// nothing to do here
			}
		}
	}
	
	synchronized Channel connect(Broker b, int port) throws InterruptedException {
		this.bc = b;
		this.cc = new Channel(bc, port);
		if (ca != null) {
			ca.connect(cc, bc.name);
			notify();
		}
		else {
			wait();
		}
		return cc;
		
	}
	
	synchronized Channel accept(Broker b, int port) throws InterruptedException {
		this.ba = b;
		this.ca = new Channel(ba, port);
		if (cc != null) {
			cc.connect(ca, ba.name);
			notify();
		}
		else {
			wait();
		}
		return ca;
	}
	
}

/*
package info5.sar.channels;

class RendezVous {
	ChannelImpl ac;
	ChannelImpl cc;
	Broker ab; //accepting broker
	Broker cb; //connecting broker
	
	private void _wait() {
		while (ac == null || cc == null) {
			try {
				wait();
			} catch (InterruptedException ex) {
				// nothing to do here
			}
		}
	}
	
	synchronized Channel connect(Broker cb, int port) {
		this.cb = cb;
		cc = ChannelImpl(cb, port);
		if (ac != null) {
			ac.connect(cc, cb.getName());
			notify();
		} else
			wait();
		return cc;
	}
	
	synchronized Channel accept(Broker ab, int port) {
		this.ab = ab;
		ac = new ChannelImpl(ab, port);
		if (cc != null) {
			ac.connect(cc, ab.getName());
			notify();
		} else
			wait();
		return ac;
	}
}
*/