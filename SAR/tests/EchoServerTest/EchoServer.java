package EchoServerTest;


import task1.Broker;
import task1.BrokerManager;
import task1.Channel;
import task1.CircularBuffer;
import task1.RdV;

public class EchoServer extends Thread {
    private Broker broker;
    private int port;

    public EchoServer(String brokerName, int port) {
        this.broker = new Broker(brokerName);
        this.port = port;
    }

    @Override
    public void run() {
        try {
        	System.out.println("EchoServer starting accepting connection !");
            // Le serveur accepte des connexions en boucle
            while (true) {
                // Accepter une connexion client via le port spécifié
                Channel channel = broker.accept(port);
                System.out.println("New connection established !");
                
                // Démarrer un nouveau thread pour traiter la connexion du client
                new Thread(new EchoTask(channel)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // La tâche qui gère les interactions avec un client
    class EchoTask implements Runnable {
        private Channel channel;

        public EchoTask(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[256];

                // Tant que la connexion est active, lire et renvoyer les données
                while (!channel.disconnected()) {
                	System.out.println("EchoServer is reading some data from channel : "  + channel);
                		int bytesRead = channel.read(buffer, 0, buffer.length);
                    System.out.println("EchoServer read " + bytesRead + " bytes, now sending back the data");
                    if (bytesRead > 0) {
                    	int offset = 0;
                        while (bytesRead - offset > 0) {
                        	offset += channel.write(buffer, offset, bytesRead - offset);// Echo des données reçues
                        }
                    }
            		System.out.println("EchoServer wrote back " + bytesRead + " bytes");
                    bytesRead = 0;
                }

                // Déconnexion
        		System.out.println("EchoServer Disconnect...");
                channel.disconnect();
        		System.out.println("EchoServer Disconnected...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
