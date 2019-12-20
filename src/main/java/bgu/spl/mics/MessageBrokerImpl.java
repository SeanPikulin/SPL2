package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	private Map<Subscriber, BlockingQueue<Message>> queues;
	private Map<Event, Future> eventFutureMap;
	private Map<Class<? extends Event>, List<Subscriber>> eventSubscriberMap;
	private Map<Class<? extends Broadcast>, List<Subscriber>> broadcastSubscriberMap;
	private AtomicInteger index;
	private static class InstanceHolder {
		private static MessageBroker instance = new MessageBrokerImpl();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return InstanceHolder.instance;
	}

	private MessageBrokerImpl() {
		queues = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		eventSubscriberMap = new ConcurrentHashMap<>();
		broadcastSubscriberMap = new ConcurrentHashMap<>();
		index = new AtomicInteger(-1);
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		if (eventSubscriberMap.get(type) == null) {
			List<Subscriber> lst = new Vector<>();
			lst.add(m);
			eventSubscriberMap.put(type, lst);
		}
		else
			eventSubscriberMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if (broadcastSubscriberMap.get(type) == null) {
			List<Subscriber> lst = new Vector<>();
			lst.add(m);
			broadcastSubscriberMap.put(type, lst);
		}
		else
			broadcastSubscriberMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastSubscriberMap.get(b) != null)
			broadcastSubscriberMap.get(b).forEach(subscriber -> queues.get(subscriber).add(b));
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future result = new Future<T>();
		eventFutureMap.put(e, result);
		increaseIndex();
		if (eventSubscriberMap.get(e) == null)
			return null;
		try {
			queues.get(eventSubscriberMap.get(e).get(index.get() % eventSubscriberMap.get(e).size())).put(e);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private void increaseIndex() {
		int val;
		do {
			val = index.get();
		} while (!index.compareAndSet(val, val + 1));
	}

	@Override
	public void register(Subscriber m) {
		queues.put(m, new PriorityBlockingQueue<Message>());
	}

	@Override
	public void unregister(Subscriber m) {
		queues.remove(m);
		eventSubscriberMap.forEach((k, v) -> v.remove(m));
		broadcastSubscriberMap.forEach((k, v) -> v.remove(m));
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		return queues.get(m).take();
	}

	

}
