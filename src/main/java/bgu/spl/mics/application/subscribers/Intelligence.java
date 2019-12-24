package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.Comparator;
import java.util.List;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private List<MissionInfo> missions;

	public Intelligence(List<MissionInfo> missionInfos) {
		super("Intelligence");
		this.missions = missionInfos;
		missions.sort(new Comparator<MissionInfo>() {
			@Override
			public int compare(MissionInfo missionInfo, MissionInfo t1) {
				return missionInfo.getTimeIssued() - t1.getTimeIssued();
			}
		});
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				// while there are available missions and the closest mission's time has exceeded
					while (missions.size() != 0 && missions.get(0).getTimeIssued() <= c.getTick()) {
						MissionInfo mission = missions.get(0);
						Report report = new Report();
						// update the report with his information
						report.setAgentsSerialNumbers(mission.getSerialAgentsNumbers());
						report.setMissionName(mission.getMissionName());
						report.setGadgetName(mission.getGadget());
						report.setTimeIssued(mission.getTimeIssued());
						Event event = new MissionReceivedEvent(mission.getMissionName(), mission.getSerialAgentsNumbers(), mission.getGadget(), mission.getTimeExpired(), mission.getDuration(), report);
						getSimplePublisher().sendEvent(event);
						missions.remove(0); // the mission sent, therefore we remove it
					}
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
