package EchoServerTest;

import task1.Broker;
import task1.Channel;

public class EchoClient extends Thread {
    private Broker broker;
    private String serverName;
    private String clientName;
    private int port;

    public EchoClient(String brokerName, String serverName, String clientName, int port) {
        this.broker = new Broker(brokerName);  // Création du client Broker
        this.serverName = serverName;
        this.clientName = clientName;
        this.port = port;
    }

    @Override
    public void run() {
        try {
        	System.out.println(clientName + " connection...");
            // Connexion au serveur d'écho
            Channel channel = broker.connect(serverName, port);
            if (channel == null) {
                System.err.println("Failed to connect to the server");
                return;
            }
        	System.out.println(clientName + " connected !");
        	
            // Boucle de test : envoi de données et vérification de l'écho
            byte[] dataToSend = new byte[255];
            for (int i = 0; i < 255; i++) {
                dataToSend[i] = (byte) (i + 1);  // Remplissage avec les valeurs 1 à 255
            }
            
            // Réception de l'écho
            byte[] receivedData = new byte[255];
            
            int bytesRead = 0;
            int offset = 0;
            
            while (dataToSend.length - offset > 0 || bytesRead < receivedData.length) {
            	if (dataToSend.length - offset > 0) {
            		offset += channel.write(dataToSend, offset, dataToSend.length - offset);
            		System.out.println(clientName + " à envoyer un total de " + offset + "bytes");
            	}
            	else { System.out.println(clientName + " sent data !"); }
            	if (bytesRead < receivedData.length) {
            		bytesRead += channel.read(receivedData, bytesRead, receivedData.length);
            		System.out.println(clientName + " à reçu un total de " + bytesRead + "bytes");
            	}
            	else { System.out.println("Data received !"); }
            }
            
            // Vérification des données reçues
            if (bytesRead == dataToSend.length) {
                for (int i = 0; i < bytesRead; i++) {
                    if (dataToSend[i] != receivedData[i]) {
                        System.err.println("Echo failed at byte " + i);
                        return;
                    }
                }
                System.out.println("Echo success!");
            } else {
                System.err.println("Echo length mismatch");
            }

            // Déconnexion
    		System.out.println(clientName + " Disconnect...");
            channel.disconnect();
    		System.out.println(clientName + " Disconnected");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

