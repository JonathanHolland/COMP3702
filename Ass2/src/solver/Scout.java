package solver;

import java.util.*;

import problem.*;
import problem.Cycle.Speed;

public class Scout {
	
	// The final map to store a mapping of each track and cycle combo
	// and the associated net winnings value as an Integer
	Map<Map<Track,Cycle>, Double> cyclesForTracks; 
	
	/**
	 * Something to initialise the class
	 */
	Scout() {
		cyclesForTracks = new HashMap<Map<Track,Cycle>, Double>();
	}
	
	/**
	 * Iterate through all the possible tracks and decide 
	 * what type of bike would be best for each one.
	 * @param tracks
	 */
	void tracks(List<Track> tracks, List<Cycle> cycles) {
		Map<Track, Cycle> trackCycle;
		// Cycle variables
		double price;
		
		// Track variables
		double winnings;
		// For every track ~ for every cycle
		for(int i=0; i<tracks.size(); i++) {
			Track tracke = tracks.get(i);
			winnings = tracke.getPrize() - tracke.getRegistrationFee();
			double mapSizeFactor = tracke.getNumCols()*tracke.getNumRows();
			double enemiesFactor = tracke.getNumOpponents();
			double distractorNo = tracke.getDistractors().size();
			// A counter for the number of obstacles in order to use as weighting for the wildness
			int obscount = 0;
			for(int p = 0; p<tracke.getMap().size(); p++) {
				for(int z = 0; z<tracke.getMap().get(p).size(); z++) {
					if(tracke.getMap().get(p).get(z).name()=="OBSTACLE") {
						obscount++;
					}
				}
				
			}
			for(int j=0; j<cycles.size(); j++) {
				trackCycle = new HashMap<Track,Cycle>();
				double speedFactor;
				double reliableFactor;
				double wildFactor;
				
				// "Scout" the cycle on this particular track, and give an average winnings
				Cycle bike = cycles.get(j);
				
				price = bike.getPrice();
				if(bike.getSpeed().name()=="SLOW") {
					speedFactor = 20;
				} else if(bike.getSpeed().name()=="MEDIUM") {
					speedFactor = 40;
				} else {
					speedFactor = 60;
				}
				
				if(bike.isReliable()) {
					reliableFactor = 0;
				} else {
					reliableFactor = distractorNo*75;
				}
				
				if(bike.isWild()) {
					wildFactor = 0;
				} else {
					wildFactor = obscount*50;
				}
				// Minus the price from the winnings to get overall net
				// Then include a weighting for the size of the map, the speed of the bike, the reliability/
				// wildness and the number of distractors/opponents
				Double net = winnings-price-reliableFactor-wildFactor - enemiesFactor*1/speedFactor + 1000; 
//				Double net = winnings-price+speedFactor+reliableFactor+wildFactor-mapSizeFactor-enemiesFactor;
				trackCycle.put(tracke, bike);
				cyclesForTracks.put(trackCycle, net);
			}
		}
		
		
		
	}
	
}
