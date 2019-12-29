package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Pair;
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


	@Override
	protected void initialize() {
		getBroker().register(this);
		subscribeEvent(AgentAvailableEvent.class, new Callback<AgentAvailableEvent>() {
			@Override
			public void call(AgentAvailableEvent c) {
				/**
				 * this function gets an AgentAvailableEvent, adds the MoneyPenny that handles this event and the
				 * agents' names to the report. Then, checks if it can send the agents to the mission (according to
				 * M's answer) and sends them.Otherwise, it releases them.
				 *
				 */
				Report report = c.getReport();
				// update the report with its information
				report.setAgentsNames(squad.getAgentsNames(c.getSerialNumbers()));
				report.setMoneypenny(serialNumber);
				Future<Boolean> isToSendAgentsFuture = new Future<>();
				complete(c, new Pair<>(squad.getAgents(c.getSerialNumbers()), isToSendAgentsFuture));
				Boolean isToSendAgents = isToSendAgentsFuture.get();
				if (isToSendAgents) {
					squad.sendAgents(c.getSerialNumbers(), c.getDuration());
				}
				else {
					squad.releaseAgents(c.getSerialNumbers());
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			/**
			 * this function calls the terminate function when the TerminateBroadcast is received
			 */
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});

	}

}
