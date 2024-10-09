package EchoServerTestTask2;

import task1.Broker;
import task2.MessageQueue;
import task2.QueueBroker;

public class EchoClient extends Thread {
    private QueueBroker queueBroker;
    private String serverName;
    private String clientName;
    private int port;

    public EchoClient(String brokerName, String serverName, String clientName, int port) {
        this.queueBroker = new QueueBroker(new Broker(brokerName));  // Création du client Broker
        this.serverName = serverName;
        this.clientName = clientName;
        this.port = port;
    }

    @Override
    public void run() {
        try {
        	System.out.println(clientName + " connection...");
            // Connexion au serveur d'écho
            MessageQueue msgQueue = queueBroker.connect(serverName, port);
            if (msgQueue == null) {
                System.err.println("Failed to connect to the server");
                return;
            }
        	System.out.println(clientName + " connected !");
        	
            // Boucle de test : envoi de données et vérification de l'écho
            byte[] dataToSend = new byte[255];
            for (int i = 0; i < 255; i++) {
                dataToSend[i] = (byte) (i + 1);  // Remplissage avec les valeurs 1 à 255
            }
            System.out.println(clientName + " à commencer à envoyer ses data (" + dataToSend.length + ")...");
            msgQueue.send(dataToSend, 0, dataToSend.length);
            System.out.println(clientName + " à envoyer un total de " + dataToSend.length + "bytes");
            
            
            byte[] receivedData = msgQueue.receive();
    		System.out.println(clientName + " à reçu un total de " + receivedData.length + "bytes");
    		
            
            // Vérification des données reçues
            if (receivedData.length == dataToSend.length) {
                for (int i = 0; i < receivedData.length; i++) {
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
            msgQueue.close();
    		System.out.println(clientName + " Disconnected");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

