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
	private final Map<Event, Future> eventFutureMap;
	private final Map<Class<? extends Event>, List<Subscriber>> eventSubscriberMap;
	private final Map<Class<? extends Broadcast>, List<Subscriber>> broadcastSubscriberMap;
	private final Map<Class<? extends Event>, AtomicInteger> eventIndexMap;
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
		eventIndexMap = new ConcurrentHashMap<>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (eventSubscriberMap) {
			if (eventSubscriberMap.get(type) == null) {
				List<Subscriber> lst = new Vector<>();
				lst.add(m);
				eventSubscriberMap.put(type, lst);
			}
			else
				eventSubscriberMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (broadcastSubscriberMap) {
			if (broadcastSubscriberMap.get(type) == null) {
				List<Subscriber> lst = new Vector<>();
				lst.add(m);
				broadcastSubscriberMap.put(type, lst);
			}
			else {
				broadcastSubscriberMap.get(type).add(m);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastSubscriberMap.get(b.getClass()) != null) {
			broadcastSubscriberMap.get(b.getClass()).forEach(subscriber -> {
				try {
					queues.get(subscriber).put(b);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future result = new Future<T>();
		eventFutureMap.put(e, result);
		if (eventIndexMap.get(e.getClass()) == null)
			eventIndexMap.put(e.getClass(), new AtomicInteger(0));
		synchronized (eventIndexMap) {
			increaseIndex(eventIndexMap.get(e.getClass()));
			if (eventSubscriberMap.get(e.getClass()) == null || eventSubscriberMap.get(e.getClass()).size() == 0) {
				return null;
			}
			try {
				queues.get(eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size())).put(e);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	private void increaseIndex(AtomicInteger index) {
		int val;
		do {
			val = index.get();
		} while (!index.compareAndSet(val, val + 1));
	}

	@Override
	public void register(Subscriber m) {
		queues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		queues.remove(m);
		eventSubscriberMap.forEach((k, v) -> v.remove(m));
		broadcastSubscriberMap.forEach((k, v) -> v.remove(m));
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		checkRegistration(m);
		return queues.get(m).take();
	}

	private void checkRegistration(Subscriber m){
		if (queues.get(m) == null)
			register(m);
	}

	

}
