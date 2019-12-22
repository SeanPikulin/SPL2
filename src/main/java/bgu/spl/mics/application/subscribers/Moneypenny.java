package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentAvailableEvent;
import bgu.spl.mics.application.messages.ReleaseAgentsEvent;
import bgu.spl.mics.application.messages.SendAgentsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
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

	public Moneypenny(int serialNumber,int timeToTerminate) {
		super("Moneypenny",timeToTerminate);
		squad = Squad.getInstance();
		this.serialNumber=serialNumber;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(AgentAvailableEvent.class, new Callback<AgentAvailableEvent>() {
			@Override
			public void call(AgentAvailableEvent c) {
				c.getReport().setAgentsNames(squad.getAgentsNames(c.getSerialNumbers()));
				complete(c, squad.getAgents(c.getSerialNumbers()));
			}
		});
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
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				if(c.getTick()==getTimeToTerminate())
					terminate();
			}
		});
		
	}

}
