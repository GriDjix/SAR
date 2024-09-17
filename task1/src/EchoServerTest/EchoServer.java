package EchoServerTest;

public class EchoServer {

	public static void main(String[] args) {

		Broker serverBroker = new Broker("EchoServer");
		int serverPort = 1000;

		new Thread(() -> {
			while (true) {
				Channel clientChannel = serverBroker.accept(serverPort);

				byte[] buffer = new byte[256];
				int bytesRead;
				while (!clientChannel.disconnected() && (bytesRead = clientChannel.read(buffer, 0, buffer.length)) != -1) {
					clientChannel.write(buffer, 0, bytesRead);
				}
				clientChannel.disconnect();
			}

		}).start();

		int numberOfClients = 3;
		for (int i = 0; i < numberOfClients; i++) {
			runClientTest("localhost", serverPort, i + 1);
		}

	}

	private static void runClientTest(String serverHost, int serverPort, int clientNumber) {

		Broker clientBroker = new Broker("ClientBroker" + clientNumber);
		Channel serverChannel = clientBroker.connect(serverHost, serverPort);

		byte[] dataToSend = new byte[255];
		for (int i = 0; i < 255; i++) {
			dataToSend[i] = (byte) (i + 1);
		}

		serverChannel.write(dataToSend, 0, dataToSend.length);

		byte[] dataReceived = new byte[255];
		int bytesRead = serverChannel.read(dataReceived, 0, dataReceived.length);

		if (bytesRead == 255) {
			boolean success = true;
			for (int i = 0; i < 255; i++) {
				if (dataToSend[i] != dataReceived[i]) {
					success = false;
					System.out.println("Client " + clientNumber + " Test Failed: Data mismatch at byte " + i);
					break;
				}
			}
			if (success) {
				System.out.println("Client " + clientNumber + " Test Passed: Data echoed correctly.");
			}
		} else {
			System.out.println("Client " + clientNumber + " Test Failed: Expected 255 bytes but received " + bytesRead);
		}

		serverChannel.disconnect();

	}
}

