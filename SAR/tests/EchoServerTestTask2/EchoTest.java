package EchoServerTestTask2;


public class EchoTest {
    public static void main(String[] args) {
    	
    	System.out.println("Test start...");
    	
        int serverPort = 8080;  // Choisissez un port libre
    	
        // Démarrer le serveur d'écho
    	System.out.println("Initialization EchoServer...");
        EchoServer server = new EchoServer("EchoServer", serverPort);
        
    	System.out.println("ServerEcho Starting...");
        server.start();

        // Démarrer plusieurs clients pour tester l'écho
        for (int i = 0; i < 5; i++) {
        	System.out.println("Initialization EchoClient " + i);
            EchoClient client = new EchoClient("Client" + i, "EchoServer", "EchoClient " + i, serverPort);
        	System.out.println("EchoClient " + i + " Starting...");
            client.start();
        }
    }
}

