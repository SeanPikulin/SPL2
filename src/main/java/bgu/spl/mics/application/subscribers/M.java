package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Pair;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int serialNumber;
	private int currentTick;
	private Diary diary;

	public M(int serialNumber) {
		super("M");
		diary = Diary.getInstance();
		this.serialNumber=serialNumber;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public Diary getDiary() {
		return diary;
	}

	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(MissionReceivedEvent.class, new Callback<MissionReceivedEvent>() {
			@Override
			/**
			 * When MissionReceivedEvent is received, the function increments the diary's total and adds M to the report.
			 * Then it sorts the agents' serial numbers to enable resource ordering when acquiring agents.
			 * After it, sends the AgentAvailableEvent to check and get the agents for the mission if there is a
			 * suitable subscriber.
			 * Then, checks if the gadget is still in the inventory, and after that checks if the tick didn't reach
			 * the expired time of the mission.
			 * If one of the conditions isn't met, the functions releases the agents of the mission or terminate.
			 */
			public void call(MissionReceivedEvent c) {
				diary.incrementTotal();
				c.getReport().setM(serialNumber);
				c.getSerialNumbers().sort(new Comparator<String>() {
					@Override
					public int compare(String s, String t1) {
						return s.compareTo(t1);
					}
				});
				Future<Pair<Boolean, Future<Boolean>>> agentsFuture = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getSerialNumbers(),c.getReport(), c.getDuration()));
				if(agentsFuture!=null && agentsFuture.get()!=null) {
					Boolean isAgentsExists = agentsFuture.get().getFirst();
					if (isAgentsExists != null & isAgentsExists.booleanValue() == true) { // if all the agents are in the squad
						Future<Boolean> gadgetFuture = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getGadget(), c.getReport()));
						if (gadgetFuture != null) { // if there is a suitable subscriber
							Boolean isGadgetExists = gadgetFuture.get();
							if (isGadgetExists != null && isGadgetExists.booleanValue() == true) { // if the gadget is still in the inventory
								if (c.getTimeExpired() > c.getReport().getQTime()) { //  QTime is the most updated tick
									c.getReport().setTimeCreated(currentTick);
									diary.addReport(c.getReport());
									agentsFuture.get().getSecond().resolve(true);

								} else  // due to time expiration, M forces to release the appropriate agents of the mission
									agentsFuture.get().getSecond().resolve(false);

							} else // due to lack of gadget, M forces to release the appropriate agents of the mission
								agentsFuture.get().getSecond().resolve(false);
						} else
							agentsFuture.get().getSecond().resolve(false);
					} else
						agentsFuture.get().getSecond().resolve(false);
				}else
					terminate();
			}
		});
		/**
		 * this function gets a TimeBroadcast and updates the current tick
		 */
		subscribeBroadcast(TickBroadcast.class, c -> currentTick = c.getTick());
		/**
		 * this function gets a TerminateBroadcast and calls the terminate function
		 */
		subscribeBroadcast(TerminateBroadcast.class, c -> terminate());
	}

}
