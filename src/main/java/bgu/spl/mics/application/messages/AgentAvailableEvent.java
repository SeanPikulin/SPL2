package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Agent;

import java.util.List;

public class AgentAvailableEvent implements Event<Boolean> {
    private List<String> serialNumbers;

    public AgentAvailableEvent(List<String> serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }


}
