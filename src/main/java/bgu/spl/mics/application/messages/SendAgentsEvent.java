package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.List;

public class SendAgentsEvent implements Event {
    private List<String> SerialNumbers;
    int duration;

    public List<String> getSerialNumbers() {
        return SerialNumbers;
    }

    public int getDuration() {
        return duration;
    }

    public SendAgentsEvent(List<String> SerialNumbers, int duration){
        this.SerialNumbers=SerialNumbers;
        this.duration=duration;
    }
}
