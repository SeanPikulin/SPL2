package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;

import java.util.List;

/**
 * An event representing M's request to release agents from a mission
 */
public class ReleaseAgentsEvent implements Event {
    private List<String> SerialNumbers;

    public List<String> getSerialNumbers() {
        return SerialNumbers;
    }
    public ReleaseAgentsEvent(List<String> SerialNumbers){
        this.SerialNumbers=SerialNumbers;
    }
}