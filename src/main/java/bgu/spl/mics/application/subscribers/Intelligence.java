package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
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

	public Intelligence(List<MissionInfo> missionInfos,int timeToTerminate) {
		super("Intelligence",timeToTerminate);
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
		getBroker().register(this);
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				if (c.getTick() == getTimeToTerminate()) {
					terminate();
				} else {

					while (missions.size() != 0 && missions.get(0).getTimeIssued() == c.getTick()) {
						MissionInfo mission = missions.get(0);
						Report report = new Report();
						report.setAgentsSerialNumbers(mission.getSerialAgentsNumbers());
						report.setMissionName(mission.getMissionName());
						report.setGadgetName(mission.getGadget());
						report.setTimeIssued(mission.getTimeIssued());
						Event event = new MissionReceivedEvent(mission.getMissionName(), mission.getSerialAgentsNumbers(), mission.getGadget(), mission.getTimeExpired(), mission.getDuration(), report);
						getSimplePublisher().sendEvent(event);
						missions.remove(0);
					}
				}
			}
		});
	}
}
