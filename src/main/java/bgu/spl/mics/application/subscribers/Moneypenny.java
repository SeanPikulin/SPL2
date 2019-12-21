package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentAvailableEvent;
import bgu.spl.mics.application.passiveObjects.Squad;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private Squad squad;

	public Moneypenny() {
		super("Moneypenny");
		squad = Squad.getInstance();
	}

	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(AgentAvailableEvent.class, new Callback<AgentAvailableEvent>() {
			@Override
			public void call(AgentAvailableEvent c) {
				complete(c, squad.getAgents(c.getSerialNumbers()));
			}
		});
		
	}

}
