package bgu.spl.mics.application.passiveObjects;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.simple.*;
/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets=new Vector<>();
	private static class InstanceHolder {
		private static Inventory instance=new Inventory();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InstanceHolder.instance;
	}

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
		for(int i=0;i<inventory.length;i++){
			this.gadgets.add(inventory[i]);
		}
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget){
		synchronized (this) {
			if (!gadgets.contains(gadget))
				return false;
			else {
				gadgets.remove(gadget);
				return true;
			}
		}
	}


	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgets.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		JSONObject obj=new JSONObject();
		JSONArray gadgets=new JSONArray();
		for (String gadget:this.gadgets) {
			gadgets.add(gadget);
		}
		obj.put("List of Gadgets",gadgets);
		try {
			FileWriter file=new FileWriter(filename);
			file.write(obj.toJSONString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
