package solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import problem.Action;
import problem.Cycle;
import problem.GridCell;
import problem.Player;
import problem.RaceSimTools;
import problem.RaceState;
import problem.Tour;
import problem.Track;

/**
 * Implement your solver here.
 * @author Joshua Song
 *
 */
public class Consultant {
	
	/**
	 * Solves a tour. Replace existing code with your code.
	 * @param tour
	 */
	public void solveTour(Tour tour) {
		
		// Set up variables and things we need here, 
		// for example the Scout and Monte class
		
		Scout s = new Scout();
		// Run tracks function inside Scout
		s.tracks(tour.getAvailableTracks(), tour.getPurchasableCycles());
		// Select the 3 highest net worths from s now
		Map<Track, Cycle> first = null;
		double firstValue = 0;
		Map<Track, Cycle> second = null;
		double secondValue = 0;
		Map<Track, Cycle> third = null;
		double thirdValue = 0;
		
		Iterator<Entry<Map<Track, Cycle>, Double>> iterator = s.cyclesForTracks.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Map<Track, Cycle>, Double> entry = iterator.next();
			Double value = entry.getValue();
			if(value>firstValue) {
				firstValue =  value;
				first = entry.getKey();
			} else if(value>secondValue && !first.keySet().toArray()[0].equals(entry.getKey().keySet().toArray()[0])) {
				secondValue = value;
				second = entry.getKey();
			} else if(value>thirdValue && !first.keySet().toArray()[0].equals(entry.getKey().keySet().toArray()[0]) && !second.keySet().toArray()[0].equals(entry.getKey().keySet().toArray()[0])) {
				thirdValue = value;
				third = entry.getKey();
			}
		}
		
		System.out.println(firstValue);
		System.out.println(first.get(first.keySet().toArray()[0]).getName());
		System.out.println(((Track) first.keySet().toArray()[0]).getFileNameNoPath());
		System.out.println(secondValue);
		System.out.println(second.get(second.keySet().toArray()[0]).getName());
		System.out.println(((Track) second.keySet().toArray()[0]).getFileNameNoPath());
		System.out.println(thirdValue);
		System.out.println(third.get(third.keySet().toArray()[0]).getName());
		System.out.println(((Track) third.keySet().toArray()[0]).getFileNameNoPath());
		
		
		Monte m = new Monte();
		
		// You should get information from the tour using the getters, and
		// make your plan.

		// Example: Buy the first cycle that is Wild
		List<Cycle> purchasableCycles = tour.getPurchasableCycles();
		List<Cycle> cycle = new ArrayList<Cycle>();
		cycle.add(first.get(first.keySet().toArray()[0]));
		cycle.add(second.get(second.keySet().toArray()[0]));
		cycle.add(third.get(third.keySet().toArray()[0]));
		
		tour.buyCycle(cycle.get(0));
		if(!cycle.get(0).equals(cycle.get(1))) {
			tour.buyCycle(cycle.get(1));
		} 
		if(!cycle.get(2).equals(cycle.get(0)) && !cycle.get(2).equals(cycle.get(1))) {
			tour.buyCycle(cycle.get(2));
		} 
		
		while (!tour.isFinished()) {
			System.out.println("unfinished loop");
			if (tour.isPreparing()) {
				System.out.println("preparing loop");
				// Race hasn't started. Choose a track, then prepare your
				// players by choosing their cycles and start positions
				
				// Example:
				Track track = tour.getAvailableTracks().get(0);
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
				players.add(new Player(id, cycle.get(2), startPosition));
				
				// Start race
				tour.startRace(track, players);
				
			}
			
			// Decide on your next action here. tour.getLatestRaceState() 
			// will probably be helpful.
			
			
			// Example: Output current position of player
			RaceState state = tour.getLatestRaceState();
			System.out.println("Player position: " + 
					state.getPlayers().get(0).getPosition());			
			
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(m.getNextAction(tour.getLatestRaceState()));
			tour.stepTurn(actions);
			
			
		}
	}
}

// altereed code that is unused
//// Example:
//Track track = null;
//ArrayList<Player> players = new ArrayList<Player>();
//for(int i = 0; i < 3; i++) {
//	track = tour.getAvailableTracks().get(i);
//	Map<String, GridCell> startingPositions = track.getStartingPositions();
//	String id = "";
//	GridCell startPosition = null;				
//	for (Map.Entry<String, GridCell> entry : startingPositions.entrySet()) {
//		id = entry.getKey()+i;
//		startPosition = entry.getValue();
//		break;
//	}
//	players.add(new Player(id, cycle.get(i), startPosition));
//}
//// Start race
//tour.startRace(track, players);		
