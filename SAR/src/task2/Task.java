package task2;

public class Task {
	Task(Broker b, Runnable r);
	Task(QueueBroker b, Runnable r);
	Broker getBroker();
	QueueBroker getQueueBroker();
	static Task getTask();
}
