package task1;

public class RdV {
	Broker ba, bc;
	Channel ca, cb;
	
	public Channel connect(Broker b) {
		this.bc = b;
		if (ba == null) {
			cb = new Channel();
			return cb;
		}
		else {
			Channel cb = new Channel(ca.out, ca.in);
			return cb;
		}
	}
	
	public Channel accept(Broker b) {
		this.ba = b;
		if (bc == null) {
			ca = new Channel();
			return ca;
		}
		else {
			Channel ca = new Channel(cb.out, cb.in);
			return cb;
		}
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