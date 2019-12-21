package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

public class AgentAvailableEvent implements Event<Boolean> {
    private List<String> serialNumbers;
    private Report report;

    public AgentAvailableEvent(List<String> serialNumbers, Report report) {
        this.serialNumbers = serialNumbers;
        this.report=report;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }

    public Report getReport() {
        return report;
    }
}
