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
	private static int CAST_TO_MILLISECONDS=100;
	// a class for thread-safe singleton
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
	 * Releases agents.Synchronization on each agent separately
	 */
	public void releaseAgents(List<String> serials){

		 // to avoid a situation where one thread tries to acquire and another thread is trying to release
			for (String serialNumber:serials) {
				if (this.agents.get(serialNumber) != null)
					synchronized (this.agents.get(serialNumber)) {
						this.agents.get(serialNumber).release();
						this.agents.get(serialNumber).notifyAll();
					}
			}

		}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time * CAST_TO_MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("finished"+time);
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
		for (String serial:serials) {
			synchronized (this.agents.get(serial)) {
				//To enforce the pre-condition: agent is available
				while (!this.agents.get(serial).isAvailable()) {
					try {
						this.agents.get(serial).wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.agents.get(serial).acquire();
			}
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
