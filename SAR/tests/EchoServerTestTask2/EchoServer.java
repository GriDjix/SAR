package EchoServerTestTask2;


import task2.QueueBroker;
import task1.Broker;
import task2.MessageQueue;

public class EchoServer extends Thread {
    private QueueBroker queueBroker;
    private int port;

    public EchoServer(String brokerName, int port) {
        Broker broker = new Broker(brokerName);
        this.queueBroker = new QueueBroker(broker);
        this.port = port;
    }

    @Override
    public void run() {
        try {
        	System.out.println("EchoServer starting accepting connection !");
            // Le serveur accepte des connexions en boucle
            while (true) {
                // Accepter une connexion client via le port spécifié
                MessageQueue messageQueue = queueBroker.accept(port);
                System.out.println("New connection established !");
                
                // Démarrer un nouveau thread pour traiter la connexion du client
                new Thread(new EchoTask(messageQueue)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // La tâche qui gère les interactions avec un client
    class EchoTask implements Runnable {
        private MessageQueue messageQueue;

        public EchoTask(MessageQueue msgQueue) {
            this.messageQueue = msgQueue;
        }

        @Override
        public void run() {
            try {
                // Tant que la connexion est active, lire et renvoyer les données
                while (!messageQueue.closed()) {
                	System.out.println("EchoServer is reading data on MQ : "  + messageQueue);
                	byte[] bytesReceived = messageQueue.receive();
                	System.out.println((bytesReceived != null) + " -> EchoServer is reading all data on MQ : "  + messageQueue);
                	if (bytesReceived != null) {
                		System.out.println("EchoServer received " + bytesReceived.length + " bytes, now sending back the data");
                		messageQueue.send(bytesReceived, 0, bytesReceived.length);
                		System.out.println("EchoServer wrote back " + bytesReceived.length + " bytes");
                	}
                }

                // Déconnexion
        		System.out.println("EchoServer Disconnect...");
                messageQueue.close();
        		System.out.println("EchoServer Disconnected...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
