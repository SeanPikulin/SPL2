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

	/**
	 * This function adds the subscriber to the type of event in the eventSubscriberMap.
	 * @param type The type to subscribe to,
	 * @param m
	 * @param <T>
	 */
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

	/**
	 * This function adds the subscriber to the type of broadcasr in the broadcastSubscriberMap.
	 * @param type 	The type to subscribe to.
	 * @param m
	 */
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

	/**
	 * this function gets the Future of the completed event and resolves it with the result.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);

	}

	/**
	 * This function adds the broadcast to each subscriber's queue that is in the broadcastSubscriberMap the
	 * broadcast param.
	 * @param b 	The message to added to the queues.
	 */
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
	/**
	 * This function adds an event to a subscriber's queue using the round-robin manner. If there is no such
	 * subscriber or the subscriber unregistered itself, it returns null.
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		Future result = new Future<T>();
		eventFutureMap.put(e, result);
		if (eventIndexMap.get(e.getClass()) == null)
			eventIndexMap.put(e.getClass(), new AtomicInteger(0));
		synchronized (eventSubscriberMap) {
			eventIndexMap.get(e.getClass()).incrementAndGet();
			if (eventSubscriberMap.get(e.getClass()) == null) {
				return null;
			}
			if (eventSubscriberMap.get(e.getClass()).size() == 0) {
				return null;
			}
			if (queues.get(eventSubscriberMap.get(e.getClass()).get(eventIndexMap.get(e.getClass()).get() % eventSubscriberMap.get(e.getClass()).size())) == null) {
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

	@Override
	/**
	 * This function creates a queue for a subscriber
	 */
	public void register(Subscriber m) {
		queues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	/**
	 * this function removes the subscriber from the eventsSubscriberMap and broadcastSubscriberMap,
	 * checks if there are events which there are no more suitable subscribers to handle them and resolves
	 * them with null and removes the queue of this subscriber.
	 */
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
	/**
	 * this function awaits for a message to get into the queue of m ,using blocking queue
	 * and returns the message when it gets it.
	 */
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		if (queues.get(m) == null)
			return null;
		Message message = queues.get(m).take();
		return message;
	}

	

}
