package solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import problem.Action;
import problem.Cycle;
import problem.GridCell;
import problem.Player;
import problem.RaceState;
import problem.Tour;
import problem.Track;

/**
 * Consultant solver for which bike/track combinations to use for three races in the given tour
 * @author Jonathan
 */
public class Consultant {
	
	/**
	 * Solves a tour
	 */
	public void solveTour(Tour tour) {
		
		// Enable the scout class
		Scout s = new Scout();
		
		// Run the "best" function inside Scout to find the best track/cycle combinations
		s.selection(tour);
		Cycle best = s.getCycle();
		
		// Initiate the MCTS method for movement within a race
		Monte m = new Monte();
		
		// Buy the 3 cycles listed in the three best matches
		List<Cycle> cycle = new ArrayList<Cycle>();
		cycle.add(best);
//		cycle.add(secondCycle);
//		cycle.add(thirdCycle);
		
		// Make sure we don't buy another bike if the same one is in another best match
		// i.e. with another track
		tour.buyCycle(cycle.get(0));
//		if(!cycle.get(0).equals(cycle.get(1))) {
//			tour.buyCycle(cycle.get(1));
//		} 
//		if(!cycle.get(2).equals(cycle.get(0)) && !cycle.get(2).equals(cycle.get(1))) {
//			tour.buyCycle(cycle.get(2));
//		} 
		
		// before running the tour, register the three tracks
		for(Track t : s.getTracks()){
			tour.registerTrack(t, 1);
		}
		
		// for the tour, run through the three tracks
		while (!tour.isFinished()) {
			System.out.println("\nThe Race/Tour Continues");
			if (tour.isPreparing()) {
				System.out.println("Preparation Time");
				// Race hasn't started. Choose a track, then prepare your
				// players by choosing their cycles and start positions
				
				// Pick a track from the already registered 3 that is left to race
				Track track = tour.getUnracedTracks().get(0);
				
				ArrayList<Player> players = new ArrayList<Player>();
				Map<String, GridCell> startingPositions = 
						track.getStartingPositions();
				String id = "";
				GridCell startPosition = null;				
				for (Map.Entry<String, GridCell> entry1 : startingPositions.entrySet()) {
					id = entry1.getKey();
					startPosition = entry1.getValue();
					break;
				}
				Cycle raceCycle = cycle.get(0);;
				// Here we want to add the associated cycle for this track
				// therefore use the track to index it in our map of map>doubles
//				if (firstTrack.equals(track)) {
//					raceCycle = cycle.get(0);
//				} else if (secondTrack.equals(track)) {
//					raceCycle = cycle.get(0);
//				} else {
//					raceCycle = cycle.get(0);
//				}
				
				players.add(new Player(id, raceCycle, startPosition));
				
				// Start race
				tour.startRace(track, players);
				
			}
			
			// Output current position of player
			RaceState state = tour.getLatestRaceState();
			System.out.println("Player position: " + 
					state.getPlayers().get(0).getPosition());			
			
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(m.getNextAction(tour));
			tour.stepTurn(actions);
			
		}
	}
}