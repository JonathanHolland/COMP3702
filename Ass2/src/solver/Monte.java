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
	
	MonteNode mNode;
	
	/**
	 * Constructor
	 */
	public Monte() {
		
	}
	
	public Action getNextAction(Tour tour) {
		mNode = new MonteNode(tour);
		System.out.print("MCTS: "); 
		startTimer();
		int iters = 0;
		while(!outOfTime(200)) {
			mNode.exploitExpand();
			iters++;
		}
		System.out.print("iters_" + iters + " - ");
		Action bestAction = mNode.getMove().action;
		System.out.print("MCTS_" + bestAction + " - ");
		System.out.println("DPOL_" + mNode.defaultPolicy(tour.getLatestRaceState()));
		return bestAction; // Return best action
//		return mNode.defaultPolicy(tour.getLatestRaceState());
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
