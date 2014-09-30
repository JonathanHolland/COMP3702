package solver;

import problem.*;

/**
 * Decides upon the actions need to be taken each 'step' of a race 
 * by using the Monte Carlo tree method.
 * @author Michael
 *
 */
public class Monte {

	private long startTime;
	
	private Tour tour;
	
	/**
	 * Constructor
	 */
	public Monte() {

	}
	
	public Action getNextAction(RaceState state) {
		startTimer();
		int iters = 0;
		while(!outOfTime(1000)) {
			iters++;
		}
		
		System.out.println("Completed iterations: " + iters);
		return Action.FS; // Return best action
	}
	
	/**
	 * Starts the timer for outOfTime()
	 */
	public void startTimer(){
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * returns false when wantedTime has elapsed since startTime
	 * @param wantedTime
	 * @return
	 */
	public boolean outOfTime(long wantedTime) {
		if(System.currentTimeMillis() - startTime >= wantedTime) {
			return true;
		} else {
			return false;
		}
	}
		
}
