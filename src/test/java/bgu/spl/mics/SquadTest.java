package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    private Squad squad;
    @BeforeEach
    public void setUp(){
        squad=Squad.getInstance();
    }

    @Test
    public void testGetInstance(){
        Squad squad2=Squad.getInstance();
        assertTrue(squad2==squad);
    }

    @Test
    public void testLoad(){
        Agent[] agents={new Agent(), new Agent(), new Agent()};
        agents[0].setSerialNumber("007");
        agents[1].setSerialNumber("006");
        agents[2].setSerialNumber("0012");
        squad.load(agents);
        List<String> serialNumbers= new LinkedList<>();
        serialNumbers.add("007");
        serialNumbers.add("006");
        serialNumbers.add("0012");
        serialNumbers.add("004");
        assertFalse(squad.getAgents(serialNumbers));
        serialNumbers.remove("004");
        assertTrue(squad.getAgents(serialNumbers));
    }

    @Test
    public void testGetAgentsNames(){
        Agent[] agents={new Agent(), new Agent(), new Agent()};
        agents[0].setSerialNumber("007");
        agents[1].setSerialNumber("006");
        agents[2].setSerialNumber("0012");
        agents[0].setName("James Bond");
        agents[1].setName("Inon Katz");
        agents[2].setName("Sean Pikulin");
        List<String> serialNumbers= new LinkedList<>();
        serialNumbers.add("007");
        serialNumbers.add("006");
        serialNumbers.add("0012");
        squad.load(agents);
        List<String> names=squad.getAgentsNames(serialNumbers);
        assertTrue(names.contains("James Bond"));
        assertTrue(names.contains("Sean Pikulin"));
        assertTrue(names.contains("Inon Katz"));
    }

}
