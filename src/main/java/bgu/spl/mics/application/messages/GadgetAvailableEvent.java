package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Report;

/**
 * An event representing the availability checking of a gadget given as parameter
 */
public class GadgetAvailableEvent implements Event<Boolean> {
    private String gadget;
    private Report report;

    public GadgetAvailableEvent(String gadget,Report report) {
        this.gadget = gadget;
        this.report=report;
    }

    public String getGadget() {
        return gadget;
    }

    public Report getReport() {
        return report;
    }
}
