package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private int duration;
	private boolean terminated;
	private int counter;
	private Timer timer;
	private static final int TIMER_PERIOD  = 100;
	private static final int TIMER_DELAY=0;

	public TimeService(int duration) {
		super("Time_Service");
		this.duration = duration;
		counter = 1;
		timer=new Timer();
	}


	@Override
	protected void initialize() {
		this.terminated = false;
	}

	/**
	 * The function sends a time tick to all subscribers each 100 milliseconds
	 * for the appropriate duration. In the end, it sends the terminate broadcast.
	 * The function uses Timer for correct time management
	 *
	 */
	@Override
	public void run() { // check Timer
		initialize();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
						Broadcast broadcast = new TickBroadcast(counter);
						getSimplePublisher().sendBroadcast(broadcast);
						System.out.println(counter);
						counter++;
						if (counter == duration + 1) {
							terminated = true;
							getSimplePublisher().sendBroadcast(new TerminateBroadcast());
							timer.cancel();
						}
				}
			}, TIMER_DELAY, TIMER_PERIOD);
	}

}
