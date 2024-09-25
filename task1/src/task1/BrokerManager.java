package task1;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {
	
        // map giving the Broker from its name, static ? pas indispensable si un seul brkmngr
        private static Map<String, Broker> brokers = new HashMap<>();

        
        //  synchronized static
        public static void addBroker(Broker broker) throws IllegalArgumentException {
            if (isNameUsed(broker.name))
                throw new IllegalArgumentException("Broker name already used (" + broker.name + ")");
            brokers.put(broker.name, broker);
        }


        public static boolean isNameUsed(String name) {
            return brokers.containsKey(name);
        }

        
        public static Broker getBroker(String name) {
            return brokers.get(name);
        }

}


/* code correction
 package info5.sar.channels;
 
 import java.util.HashMap;
 
 public class BrokerManager {
 
 	private static BrokerManager self; (singleton)
 	
 	static BrokerManager getSelf() { (pas besoin de synchronized car une lecture de référence est atomique)
 		return self;
 	}
 	static {
 		self = new BrokerManager();
 	}
 	
 	public synchronized void add(Broker borker) {
 		String name = broker.getName();
 		Broker b = brokers.get(name);
 		if (b != null)
 			throws new IllegalStateException("Broker " + name + " already exists!");
 		brokers.put(name, broker);
 	}
 	
 	public synchronized void remove(Broker broker) {
 		String name = broker.getName();
 		brokers.remove(name);
 	}
 	
 	public synchronized Broker get(String name) {
 		return brokers.get(name);
 	}
 }
  		
*/