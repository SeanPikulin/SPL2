package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Pair;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

/**
 * An event representing the availability checking of an agents given as parameters
 */
public class AgentAvailableEvent implements Event<Pair<Boolean, Future<Boolean>>> {
    private List<String> serialNumbers;
    private Report report;
    private int duration;

    public AgentAvailableEvent(List<String> serialNumbers, Report report, int duration) {
        this.serialNumbers = serialNumbers;
        this.report=report;
        this.duration = duration;
    }

    public List<String> getSerialNumbers() {
        return this.serialNumbers;
    }

    public Report getReport() {
        return report;
    }

    public int getDuration() {
        return duration;
    }
}
