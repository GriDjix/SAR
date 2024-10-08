package task1;

import java.util.HashMap;
import java.util.Map;

public class BrokerManager {
    	
		private static Map<String, Broker> brokers = new HashMap<>();
	
		private static BrokerManager self;
		
		static BrokerManager getSelf() {
			return self;
		}
		static {
			self = new BrokerManager();
		}
        
        public synchronized void addBroker(Broker broker) throws IllegalArgumentException {
        	String name = broker.name;
        	Broker b = brokers.get(name);
            if (b != null) {
                throw new IllegalArgumentException("Broker name already used (" + name + ")");
            }
            brokers.put(name, broker);
        }


        public synchronized boolean removeBroker(String name) throws IllegalArgumentException {
            Broker b = brokers.get(name);
            if (b == null) {
            	throw new IllegalArgumentException("Broker " + name + " doesn't exists");
            }
            return brokers.remove(name) != null;            
        }

        
        public synchronized Broker getBroker(String name) {
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