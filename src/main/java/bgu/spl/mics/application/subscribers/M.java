package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;

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
			public void call(MissionReceivedEvent c) {
				diary.incrementTotal();
				Report report = c.getReport();
				report.setM(serialNumber);
				System.out.println("M = " + serialNumber);

				Future<Boolean> agentsFuture = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getSerialNumbers(),c.getReport()));
				if(agentsFuture!=null) {
					boolean isAgentsExists = agentsFuture.get();
					if (isAgentsExists) {
						Future<Boolean> gadgetFuture = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getGadget(), c.getReport()));
						if (gadgetFuture != null) {
							boolean isGadgetExists = gadgetFuture.get();
							if (isGadgetExists) {
								if (c.getTimeExpired() > c.getReport().getQTime()) {
									c.getReport().setTimeCreated(currentTick);
									diary.addReport(c.getReport());
									System.out.println(c.getMissionName() + " sent");
									getSimplePublisher().sendEvent(new SendAgentsEvent(c.getSerialNumbers(), c.getDuration()));

								} else {
									System.out.println(c.getMissionName() + " wasnt sent");
									getSimplePublisher().sendEvent(new ReleaseAgentsEvent(c.getSerialNumbers()));
								}

							} else {
								System.out.println(c.getMissionName() + " wasnt sent");
								getSimplePublisher().sendEvent(new ReleaseAgentsEvent(c.getSerialNumbers()));
							}
						}
					}
				}
			}
		});
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				currentTick = c.getTick();
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
	}

}
