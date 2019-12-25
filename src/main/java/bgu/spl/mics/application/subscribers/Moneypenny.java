package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.passiveObjects.Squad;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private int serialNumber;
	private Squad squad;

	public Moneypenny(int serialNumber) {
		super("Moneypenny");
		squad = Squad.getInstance();
		this.serialNumber=serialNumber;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	@Override
	protected void initialize() {
		// in order to assign half of the moneypennies for AgentAvailableEvent and half for Send and Release
		if (serialNumber % 2 == 0) { // subscribes for AgentAvailableEvent
			subscribeEvent(AgentAvailableEvent.class, new Callback<AgentAvailableEvent>() {
				@Override
				public void call(AgentAvailableEvent c) {
					Report report = c.getReport();
					// update the report with its information
					report.setAgentsNames(squad.getAgentsNames(c.getSerialNumbers()));
					report.setMoneypenny(serialNumber);
					complete(c, squad.getAgents(c.getSerialNumbers()));
				}
			});
		}
		else { // subscribes for SendAgentsEvent and ReleaseAgentsEvent
			subscribeEvent(SendAgentsEvent.class, new Callback<SendAgentsEvent>() {
				@Override
				public void call(SendAgentsEvent c) {
					squad.sendAgents(c.getSerialNumbers(),c.getDuration());
				}
			});
			subscribeEvent(ReleaseAgentsEvent.class, new Callback<ReleaseAgentsEvent>() {
				@Override
				public void call(ReleaseAgentsEvent c) {
					squad.releaseAgents(c.getSerialNumbers());
				}
			});
		}
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
		
	}

}
