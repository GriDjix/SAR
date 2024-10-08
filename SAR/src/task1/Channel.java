package task1;

import java.nio.channels.ClosedChannelException;

public class Channel {
	
	Broker broker;
	CircularBuffer in, out;
	int port;
	Channel rch; //remote channel
	boolean disconnected;
	boolean dangling;
	String rname;
	
	protected Channel(Broker b, int port) {
		this.broker = b;
		this.port = port;
		this.in = new CircularBuffer(64);
	}
	
	void connect (Channel rch, String name) {
		this.rch = rch;
		rch.rch = this;
		this.out = rch.in;
		rch.out = this.in;
		rname = name;
	}
	
	public int read(byte[] bytes, int offset, int length) {
		int nbytes = 0;
		try {
			if (disconnected) {
				throw new ClosedChannelException(); //Do not throw an exception if dangling, it is legal to read when dangling
			}		
			while (nbytes == 0) { //Block if no bytes can be pulled through unless disconnected or dangling
				if (in.empty()) {
					synchronized (in) {
						//System.out.println("Entering synch loop of read");
						//System.out.println(in.empty());
						while (in.empty()) {
							if (disconnected || dangling)
								throw new ClosedChannelException(); //This is where we need to test these conditions, when there is nothing to read
																	// and we are supposed to block
							try {
								in.wait();
							} catch (InterruptedException ex) {
								//Nothing to do here.
							}
						}
					}
				}
				while (nbytes < length && !in.empty()) { // write bytes that can be pushed in the out buffer
					//System.out.println("buffer not empty");
					byte val = in.pull();
					bytes[offset + nbytes] = val;
					nbytes++;
				}
				//System.out.println(nbytes + " bytes read");
				if (nbytes != 0)
					synchronized (in) {
						in.notify(); //if some bytes where pushed, notify the other side
					}
			}
		} catch (ClosedChannelException ex) {
			if (!disconnected) {
				disconnected = true;
				synchronized (out) {
					out.notifyAll();
				}
			}
			//throw ex;
		}
		return nbytes;
	}
	
	public int write(byte[] bytes, int offset, int length) throws ClosedChannelException {
		if (disconnected)
			throw new ClosedChannelException(); //Do not throw an exception if dangling, do not bother making it a case to drop bytes, let the buffer fill up
											// when dangling... the other side is not reading...
		int nbytes = 0;
		while (nbytes == 0) { //Block if no bytes can be pushed through unless disconnected or dangling
			if (out.full()) {
				synchronized (out) {
					while (out.full()) {
						if (disconnected)
							throw new ClosedChannelException();
						if (dangling)
							return length;
						try {
							out.wait();
						} catch (InterruptedException ex) {
							//Nothing to do here.
						}
					}
				}
			}
			while (nbytes < length && !out.full()) { // write bytes that can be pushed in the out buffer
				byte val = bytes[offset + nbytes];
				out.push(val);
				nbytes++;
			}
			//System.out.println(nbytes + " bytes sent !");
			if (nbytes != 0)
				synchronized (out) {
					out.notify(); //if some bytes where pushed, notify the other side
				}
		}
		return nbytes;
	}
	
	public void disconnect() {
		//only synch locally to test-and-test the field "disconnected" and do not synch on the remote end to set the field "dangling".
		synchronized (this) {
			if (disconnected)
				return;
			disconnected = true;
			// this is safe even without synchronization because the field dangling only goes from false to true and never back to false
			rch.dangling = true;
		}
		// wake up locally and remotely blocked threads, on either a read or write operation, so that they can assess the new disconnected situation
		//Nota Bene: use notifyAll if you are not 100% sure there is only one thread waiting and it is correct to do so.
		synchronized (out) {
			out.notifyAll();
		}
		synchronized (in) {
			in.notifyAll();
		}
	}
	

	public boolean disconnected() {
		return disconnected;
	}
}

/* Code exemple

package info5.sar.channels;
import info5.sar.utils.CircularBuffer;

public class ChannelImpl extends Channnel {

	int port;
	CircularBufer in, out;
	ChannelImpl rch; //remote channel
	boolean disconnected;
	boolean dangling;
	String rname;
	
	protected ChannelImpl(Broker broker, int port) {
		super(broker);
		this.port = port;
		this.in = new CircularBuffer(64);
	}
	
	void connect (ChannelImpl rch, String name) {
		this.rch = rch;
		rch.rch = this;
		this.out = rch.in;
		rch.out = this.in;
		rname = name;
	}
		
	
	public int read(bytes[] bytes, int offset, int length) throws DisconnectedException {
		if (diconnected)
			throw DisconnectedException(); //Do not throw an exception if dangling, it is legal to read when dangling
		int nbytes = 0;
		try {
			while (nbytes == 0) { //Block if no bytes can be pulled through unless disconnected or dangling
				if (in.empty()) {
					synchronized (in) {
						while (in.empty()) {
							if (disconnected || dangling)
								throw new DisconnectedException(); //This is where we need to test these conditions, when there is nothing to read
																	// and we are supposed to block
							try {
								in.wait();
							} catch (InterruptedException ex) {
								//Nothing to do here.
							}
						}
					}
				}
				while (nbytes < length && !in.empty()) { // write bytes that can be pushed in the out buffer
					byte val = in.pull();
					bytes[offset + nbytes] = val;
					nbytes++;
				}
				if (nbytes != 0)
					synchronized (in) {
						in.notify(); //if some bytes where pushed, notify the other side
					}
			}
		} catch (DisconnectedException ex) {
			if (!disconnected) {
				disconnected = true;
				synchronized (out) {
					out.notifyAll();
				}
			}
			throw ex;
		}
	}
	
	public int write(bytes[] bytes, int offset, int length) throws DisconnectedException {
		if (diconnected)
			throw DisconnectedException(); //Do not throw an exception if dangling, do not bother making it a case to drop bytes, let the buffer fill up
											// when dangling... the other side is not reading...
		int nbytes = 0;
		while (nbytes == 0) { //Block if no bytes can be pushed through unless disconnected or dangling
			if (out.full()) {
				synchronized (out) {
					while (out.full()) {
						if (disconnected)
							throw new DisconnectedException();
						if (dangling)
							return length;
						try {
							out.wait();
						} catch (InterruptedException ex) {
							//Nothing to do here.
						}
					}
				}
			}
			while (nbytes < length && !out.full()) { // write bytes that can be pushed in the out buffer
				byte val = bytes[offset + nbytes];
				out.push(val);
				nbytes++;
			}
			if (nbytes != 0)
				synchronized (out) {
					out.notify(); //if some bytes where pushed, notify the other side
				}
		}
		return nbytes;
	}
	

	/*
	 * Warning: do not synch the entire method to test-and-test the field "disconnected" and then synch on the other end of the channel to set the field
	 * "dangling". This would lead to deadlocks when both sides would disconnect concurrently.
	 *
	@Override
	public void disconnet() {
		//only synch locally to test-and-test the field "disconnected" and do not synch on the remote end to set the field "dangling".
		synchronized (this) {
			if (disconnected)
				return;
			disconnected = true;
			// this is safe even without synchronization because the field dangling only goes from false to true and never back to false
			rch.dangling = true;
		}
		// wake up locally and remotely blocked threads, on either a read or write operation, so that they can assess the new disconnected situation
		//Nota Bene: use notifyAll if you are not 100% sure there is only one thread waiting and it is correct to do so.
		synchronized (out) {
			out.notifyAll();
		}
		synchronized (in) {
			in.notifyAll();
		}
	}
	
	@Override
	public boolean disconnected() {
		return disconnected;
	}
}
*/