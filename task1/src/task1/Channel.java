package task1;

public class Channel {
	CircularBuffer in, out;
	
	Channel() {
		in = new CircularBuffer(1000);
		out = new CircularBuffer(1000);
	}
	
	Channel(CircularBuffer in, CircularBuffer out) {
		this.in = in;
		this.out = out;
	}
	
	public synchronized int read(byte[] bytes, int offset, int length) throws InterruptedException {
		if (this.disconnected()) {
			throw DisconnectedException;
		}
        int readbytes = 0;
        while (length - readbytes > 0) {
            while (in.empty()) {
                // Si buffer vide, on attend qu'il y ait des données
                wait();  // Attente
            }
            // Lire un octet du buffer
            bytes[offset + readbytes] = in.pull();
            readbytes += 1;
        }
        
        return readbytes;  // Retourner le nombre d'octets effectivement lus
    }
	
	public synchronized int write(byte[] bytes, int offset, int length) throws InterruptedException {
		if (this.disconnected()) {
			throw DisconnectedException;
		}
        int writebytes = 0;
        while (length - writebytes > 0) {
            while (in.full()) {
                // Si buffer plein, on attend qu'il y ait de la place
                wait();  // Attente
            }
            // écrire un octet dans le channel
            out.push(bytes[offset + writebytes]);
            writebytes += 1;
        }
        
        return writebytes;  // Retourner le nombre d'octets effectivement écris
    }
	
	void disconnect() {}
	boolean disconnected() {}
}
