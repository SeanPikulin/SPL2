package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

	private List<Report> reports;
	private AtomicInteger total;
	private static class InstanceHolder {
		private static Diary instance=new Diary();
	}

	private Diary() {
		reports = new Vector<>();
		total = new AtomicInteger(0);
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return InstanceHolder.instance;
	}

	public List<Report> getReports() {
		return this.reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		this.reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		Gson g=new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter writer=new FileWriter(filename);
			g.toJson(this,writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return this.total.get();
	}

	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal(){
		int val;
		do {
			val = getTotal();
		} while (!total.compareAndSet(val, val + 1));
	}
}
