package solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
		// rawr
		// You should get information from the tour using the getters, and
		// make your plan.

		// Example: Buy the first cycle that is Wild
		List<Cycle> purchasableCycles = tour.getPurchasableCycles();
		Cycle cycle = null;
		for (int i = 0; i < purchasableCycles.size(); i++) {
			cycle = purchasableCycles.get(i);
			if (cycle.isWild()) {
				tour.buyCycle(cycle);
				break;
			}
		}
		
		while (!tour.isFinished()) {
			
			if (tour.isPreparing()) {
				
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
				players.add(new Player(id, cycle, startPosition));
				
				// Start race
				tour.startRace(track, players);		
			}
			
			// Decide on your next action here. tour.getLatestRaceState() 
			// will probably be helpful.
			
			// Example: Output current position of player
			RaceState state = tour.getLatestRaceState();
			System.out.println("Player position: " + 
					state.getPlayers().get(0).getPosition());
			
			// Example: Keep moving forward slowly
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(Action.FS);
			tour.stepTurn(actions);
			
			
		}
	}
}
