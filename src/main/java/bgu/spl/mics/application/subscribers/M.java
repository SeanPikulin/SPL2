package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private int currentTick;
	private Diary diary;

	public M() {
		super("M");
		diary = Diary.getInstance();
	}

	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(MissionReceivedEvent.class, new Callback<MissionReceivedEvent>() {
			@Override
			public void call(MissionReceivedEvent c) {
				diary.incrementTotal();
				Future<Boolean> agentsFuture = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getSerialNumbers()));
				boolean isAgentsExists = agentsFuture.get();
				if (isAgentsExists) {
					Future<Boolean> gadgetFuture = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getGadget()));
					boolean isGadgetExists = gadgetFuture.get();
				}
			}
		});
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				currentTick = c.getTick();
			}
		});
	}

}
