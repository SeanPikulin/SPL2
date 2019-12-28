package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Pair;

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
	private Map<Class<? extends Event>, AtomicInteger> eventIndexMap;
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
		if (broadcastSubscriberMap.get(b.getClass()) != null&& broadcastSubscriberMap.get(b.getClass()).size() > 0) {
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
		synchronized (eventSubscriberMap) {
			eventIndexMap.get(e.getClass()).incrementAndGet();
			if (eventSubscriberMap.get(e.getClass()) == null) {
				System.out.println("event type" + e.getClass() + " didnt have subcribers at all");
				return null;
			}
			if (eventSubscriberMap.get(e.getClass()).size() == 0) {
				System.out.println("event type " + e.getClass() + " doesnt have subscribers");
				return null;
			}
			if (queues.get(eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size())) == null) {
				System.out.println("subscriber " + eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size()).getName() + " doenst have a queue");
				return null;
			}
			try {
				System.out.println("subscriber = " + eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size() + " for event " + e.getClass());
				System.out.println("subscriber " + eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size()).getName() + " queue: " + queues.get(eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size())));
				queues.get(eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size())).put(e);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void register(Subscriber m) {
		queues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		synchronized (eventSubscriberMap) {
		eventSubscriberMap.forEach((k, v) -> v.remove(m));

		for (Class<? extends Event> type:eventSubscriberMap.keySet()) {
			if(eventSubscriberMap.get(type).size()==0)
				for(Event e:eventFutureMap.keySet()){
					if(type.equals(e.getClass()))
						eventFutureMap.get(e).resolve(null);
				}
		}

			broadcastSubscriberMap.forEach((k, v) -> v.remove(m));

			queues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		if (queues.get(m) == null)
			return null;
		Message message = queues.get(m).take();
		System.out.println(message.getClass().toString() + " has taken by " + m.getName());
		return message;
	}

	

}
