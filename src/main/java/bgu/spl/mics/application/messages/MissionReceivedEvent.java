package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class MissionReceivedEvent implements Event {
    private String missionName;
    private List<String> serialNumbers;
    private String gadget;
    private int timeExpired;
    private int duration;

    public MissionReceivedEvent(String missionName, List<String> serialNumbers, String gadget, int timeExpired, int duration) {
        this.missionName = missionName;
        this.serialNumbers = serialNumbers;
        this.gadget = gadget;
        this.timeExpired = timeExpired;
        this.duration = duration;
    }

    public int getTimeExpired() {
        return timeExpired;
    }

    public int getDuration() {
        return duration;
    }

    public String getMissionName() {
        return missionName;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public String getGadget() {
        return gadget;
    }
}
