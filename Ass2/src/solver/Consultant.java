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
		s.best(tour.getTracks(), tour.getPurchasableCycles());
		
		// Select the 3 highest net worths from s now
		double firstValue = 0.0;
		double secondValue = 0.0;
		double thirdValue = 0.0;
		Track firstTrack = null;
		Track secondTrack = null;
		Track thirdTrack = null;
		Cycle firstCycle = null;
		Cycle secondCycle = null;
		Cycle thirdCycle = null;
		
		
		Iterator<Entry<Map<Track, Cycle>, Double>> iterator = s.cyclesForTracks.entrySet().iterator();
		
		Track t = null;
		Cycle c = null;
		while(iterator.hasNext()) {
			Entry<Map<Track, Cycle>, Double> entry = iterator.next();
			Double value = entry.getValue();
			Iterator<Entry<Track,Cycle>> it = entry.getKey().entrySet().iterator();
			while(it.hasNext()) {
				Entry<Track,Cycle> entry2 = it.next();
				t = entry2.getKey();
				c = entry2.getValue();
			}
			if(value>firstValue) {
				firstValue =  value;
				firstTrack = t;
				firstCycle = c;
			} else if(value>secondValue && !firstTrack.equals(t)) {
				secondValue = value;
				secondTrack = t;
				secondCycle = c;
			} else if(value>thirdValue && !firstTrack.equals(t) && !secondTrack.equals(t)) {
				thirdValue = value;
				thirdTrack = t;
				thirdCycle = c;
			}
		}
		
		System.out.println(firstValue);
		System.out.println(firstCycle.getName());
		System.out.println(firstTrack.getFileNameNoPath());
		System.out.println(secondValue);
		System.out.println(secondCycle.getName());
		System.out.println(secondTrack.getFileNameNoPath());
		System.out.println(thirdValue);
		System.out.println(thirdCycle.getName());
		System.out.println(thirdTrack.getFileNameNoPath());
		
		
		// Initiate the MCTS method for movement within a race
		Monte m = new Monte();
		
		// Buy the 3 cycles listed in the three best matches
		List<Cycle> cycle = new ArrayList<Cycle>();
		cycle.add(firstCycle);
		cycle.add(secondCycle);
		cycle.add(thirdCycle);
		
		// Make sure we don't buy another bike if the same one is in another best match
		// i.e. with another track
		tour.buyCycle(cycle.get(0));
		if(!cycle.get(0).equals(cycle.get(1))) {
			tour.buyCycle(cycle.get(1));
		} 
		if(!cycle.get(2).equals(cycle.get(0)) && !cycle.get(2).equals(cycle.get(1))) {
			tour.buyCycle(cycle.get(2));
		} 
		
		// before running the tour, register the three tracks
		tour.registerTrack(firstTrack, 1);
		tour.registerTrack(secondTrack, 1);
		tour.registerTrack(thirdTrack, 1);
		
		// for the tour, run through the three tracks
		while (!tour.isFinished()) {
			System.out.println("unfinished loop");
			if (tour.isPreparing()) {
				System.out.println("preparing loop");
				// Race hasn't started. Choose a track, then prepare your
				// players by choosing their cycles and start positions
				
				// Pick a track from the already registered 3 that is left to race
				Track track = tour.getUnracedTracks().get(0);
				
				ArrayList<Player> players = new ArrayList<Player>();
				Map<String, GridCell> startingPositions = 
						track.getStartingPositions();
				String id = "";
				GridCell startPosition = null;				
				for (Map.Entry<String, GridCell> entry : startingPositions.entrySet()) {
					id = entry.getKey();
					startPosition = entry.getValue();
					break;
				}
				Cycle raceCycle;
				// Here we want to add the associated cycle for this track
				// therefore use the track to index it in our map of map>doubles
				if (firstTrack.equals(track)) {
					raceCycle = cycle.get(0);
				} else if (secondTrack.equals(track)) {
					raceCycle = cycle.get(1);
				} else {
					raceCycle = cycle.get(2);
				}
				
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