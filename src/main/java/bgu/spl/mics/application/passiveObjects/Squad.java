package bgu.spl.mics.application.passiveObjects;
import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;
	private static class InstanceHolder {
		private static Squad instance=new Squad();
	}

	private Squad() {
		agents = new HashMap<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return  InstanceHolder.instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		for(int i=0;i<agents.length;i++){
			this.agents.put(agents[i].getSerialNumber(),agents[i]);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		for (String serialNumber:serials) {
			this.agents.get(serialNumber).release();
		}
		notifyAll();
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		releaseAgents(serials);
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		for(int i=0;i<serials.size();i++){
			if(!this.agents.containsKey(serials.get(i)))
				return false;
		}
		synchronized (this) {
			while (!allNotAcquired(serials)) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for(int i=0;i<serials.size();i++){
				this.agents.get(serials.get(i)).acquire();
			}
		}
		return true;
	}

	private boolean allNotAcquired(List<String> serials){
		for(int i=0;i<serials.size();i++){
			if(!this.agents.get(serials.get(i)).isAvailable())
				return false;
		}
		return true;
	}
    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
        List<String> result=new Vector<>();
        for(int i=0;i<serials.size();i++){
        	if(agents.containsKey(serials.get(i)))
        		result.add(agents.get(serials.get(i)).getName());
		}
        return result;
    }

}
