package task2;

import java.nio.channels.ClosedChannelException;

import task1.Broker;
import task1.Channel;

public class MessageQueue {
	
	Channel channel;
	Broker broker;
	
	MessageQueue (Channel channel, Broker broker) {
		this.channel = channel;
		this.broker = broker;
	}
	
	public void send(byte[] bytes, int offset, int length) throws ClosedChannelException {
		
		int sendBytes = 0;	//Total byte sent
		
		byte[] lenghtMessage =  new byte[] { (byte) length };
		
		System.out.println("On envoie la valeur " + (lenghtMessage[0] & 0xFF) + " comme étant le longueur du message à suivre");
		channel.write(lenghtMessage, 0, 1);
		
		while (sendBytes < length) {
			sendBytes += channel.write(bytes, offset + sendBytes, length - sendBytes);
			System.out.println(broker.getName() + " à envoyer " + sendBytes + " bytes");
		}
	}
	
	public byte[] receive() {
		
		int readByte = 0;
		byte[] lenghtMessage = new byte[1];
		channel.read(lenghtMessage, 0, 1);
		int lenght = (lenghtMessage[0] & 0xFF);
		System.out.println("Longueur du message : " + lenght);
		byte[] bytes = null;
		if (lenght > 0) {
		
			bytes = new byte[lenght];
			while (readByte < lenght) {
				readByte += channel.read(bytes, readByte, lenght-readByte);	//On boucle pour lire autant de byte que la longueur initiale du message
			}
			System.out.println("On sort de la boucle de read en ayant lu " + readByte + " bytes");
		}
		return bytes;
	}
	
	public void close() {
		channel.disconnect();
	}
	
	public boolean closed() {
		return channel.disconnected();
	}
}
