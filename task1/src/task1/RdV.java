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
