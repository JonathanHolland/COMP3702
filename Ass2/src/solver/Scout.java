package solver;

import java.util.*;
import java.util.Map.Entry;
import java.util.List;

import problem.*;

public class Scout {
	
	// The final map to store a mapping of each track and cycle combo
	// and the associated net winnings value as an Integer
	Map<Map<Track,Cycle>, Double> cyclesForTracks;
	Cycle bestCycle;
	List<Track> bestTracks;
	
	Scout() {
		cyclesForTracks = new HashMap<Map<Track,Cycle>, Double>();
		bestTracks = new ArrayList<Track>();
	}
	
	/**
	 * Iterate through all the possible tracks and decide 
	 * what type of bike would be best for each one.
	 * @param tracks
	 */
	void best(List<Track> tracks, List<Cycle> cycles) {
		Map<Track, Cycle> trackCycle;
		// Cycle variables
		double price;
		
		// Track variables
		double winnings;
		
		// For every track ~ for every cycle
		for(int i=0; i<tracks.size(); i++) {
			Track trk = tracks.get(i);
			winnings = trk.getPrize() - trk.getRegistrationFee();
			double mapSize = trk.getNumCols()*trk.getNumRows();
			double enemiesFactor = trk.getNumOpponents();
			double distractorNo = trk.getDistractors().size();
			// A counter for the number of obstacles in order to use as weighting for the wildness
			int obscount = 0;
			for(int p = 0; p<trk.getMap().size(); p++) {
				for(int z = 0; z<trk.getMap().get(p).size(); z++) {
					if(trk.getMap().get(p).get(z).name()=="OBSTACLE") {
						obscount++;
					}
				}
				
			}
			for(int j=0; j<cycles.size(); j++) {
				trackCycle = new HashMap<Track,Cycle>();
				double speedFactor = 0;
				double reliableFactor = 0;
				double wildFactor = 0;
				
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
					// Go through the distractors and find the likelihood of them appearing
					// Add this to the reliable factor
					for(int k=0; k<distractorNo; k++){
						reliableFactor += trk.getDistractors().get(k).getAppearProbability()*75;	
					}
				}
				// Compare the number of obstacles to how large the track is
				if(bike.isWild()) {
					wildFactor = 0;
				} else {
					wildFactor = obscount/mapSize*10000; // Change this 300 through testing
				}
				
				//
				
				// Minus the price from the winnings to get overall net
				// Then include a weighting for the size of the map, the speed of the bike, the reliability/
				// wildness and the number of distractors/opponents	
				
				Double net = (winnings-price-reliableFactor-wildFactor-enemiesFactor*1/speedFactor)+1000; 
				
				System.out.println("----------- One bike/track added ------------");
				trackCycle.put(trk, bike);
				cyclesForTracks.put(trackCycle, net);
				System.out.println(cyclesForTracks.size());
			}
		}
	}
	/**
	 * Select the best 3 tracks for a single bike
	 * @param tour
	 */
	void selection(Tour tour){
		best(tour.getTracks(), tour.getPurchasableCycles());
		
		// Select the 3 highest net worths from s now
		double firstValue = -10000.0;
		double secondValue = -10000.0;
		double thirdValue = -10000.0;
		Track firstTrack = null;
		Track secondTrack = null;
		Track thirdTrack = null;
		Cycle firstCycle = null;
		
		Iterator<Entry<Map<Track, Cycle>, Double>> iterator = cyclesForTracks.entrySet().iterator();
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
				if(value>firstValue) {
					firstValue =  value;
					firstTrack = t;
					//System.out.println(firstTrack.getFileNameNoPath());
					firstCycle = c;
				}
			}
			
		}
		bestCycle = firstCycle;
		//bestTracks.add(firstTrack);
		System.out.println(firstTrack.getFileNameNoPath());
		iterator = cyclesForTracks.entrySet().iterator();
		t = null;
		c = null;
		while(iterator.hasNext()) {
			Entry<Map<Track, Cycle>, Double> entry = iterator.next();
			Double value = entry.getValue();
			Iterator<Entry<Track,Cycle>> it = entry.getKey().entrySet().iterator();
			while(it.hasNext()) {
				Entry<Track,Cycle> entry2 = it.next();
				t = entry2.getKey();
				c = entry2.getValue();
				if((value>secondValue) && !firstTrack.equals(t) && c.equals(firstCycle)) {
					secondValue = value;
					secondTrack = t;
				}
			}
		}
		System.out.println(secondTrack.getFileNameNoPath());
		
		iterator = cyclesForTracks.entrySet().iterator();
		t = null;
		c = null;
		while(iterator.hasNext()) {
			Entry<Map<Track, Cycle>, Double> entry = iterator.next();
			Double value = entry.getValue();
			Iterator<Entry<Track,Cycle>> it = entry.getKey().entrySet().iterator();
			while(it.hasNext()) {
				Entry<Track,Cycle> entry2 = it.next();
				t = entry2.getKey();
				c = entry2.getValue();
				if((value>thirdValue) && !firstTrack.equals(t) && !secondTrack.equals(t) &&
						c.equals(firstCycle)) {
					thirdTrack = t;
					thirdValue = value;
				}
			}	
		}
		System.out.println(thirdTrack.getFileNameNoPath());
		
		System.out.println(firstValue);
		System.out.println(firstCycle.getName());
		bestTracks.add(firstTrack);
		bestTracks.add(secondTrack);
		bestTracks.add(thirdTrack);
		
	}
	/**
	 * Return the best Cycle chosen by selection
	 * @return
	 */
	Cycle getCycle() {
		return bestCycle;
	}
	/**
	 * Return the best 3 tracks chosen by selection
	 * @return
	 */
	List<Track> getTracks() {
		return bestTracks;
	}
}
