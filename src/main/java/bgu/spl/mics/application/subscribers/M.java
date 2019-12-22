package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
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
	private int serialNumber;
	private int currentTick;
	private Diary diary;

	public M(int serialNumber,int timeToTerminate) {
		super("M",timeToTerminate);
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
			public void call(MissionReceivedEvent c) {
				diary.incrementTotal();
				c.getReport().setM(serialNumber);
				Future<Boolean> agentsFuture = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getSerialNumbers(),c.getReport()));
				boolean isAgentsExists = agentsFuture.get();
				if (isAgentsExists) {
					Future<Boolean> gadgetFuture = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getGadget(),c.getReport()));
					boolean isGadgetExists = gadgetFuture.get();
					if(isGadgetExists){
						if(c.getTimeExpired()>currentTick){
							c.getReport().setTimeCreated(currentTick);
							diary.addReport(c.getReport());
							getSimplePublisher().sendEvent(new SendAgentsEvent(c.getSerialNumbers(),c.getDuration()));

						}
						else{
							getSimplePublisher().sendEvent(new ReleaseAgentsEvent(c.getSerialNumbers()));
						}

					}
					else{
						getSimplePublisher().sendEvent(new ReleaseAgentsEvent(c.getSerialNumbers()));
					}
				}
			}
		});
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				currentTick = c.getTick();
				if(c.getTick()==getTimeToTerminate())
					terminate();
			}
		});
	}

}
