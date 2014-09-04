package fakeBrains;

import java.util.List;

import problem.*;

public class Interpolate {

	// The List of Obstacles
	private List<Obstacle> obstacles;
	
	// The Path found with Astar
	private	List<Node> path;
	
	public Interpolate(List<Obstacle> obstacles, List<Node> path){
		this.obstacles = obstacles;
		this.path = path;
	}
	/**
	 * Moves from one node asv configuration to another,
	 * changing the configuration shape as needed to meet
	 * the end result and to avoid obstacles it comes near 
	 * 
	 * @param start - the node to begin from
	 * @param end - the node to end at
	 * @return the node of the end if successful
	 */
	public List<ASVConfig> Interpolate(Node start, Node end) {
		
		boolean asvLeft =  true;
		
		while(asvLeft) {
			// While there are still asv's to check, add the start and end positions of each one to a map
			
		}
		
		// Use the start and end positions from the map to extract how many primitive steps each one needs to travel
		
		// Make a separate function to run through the movements WITH the constraints
		// Make this separate function call another function on each iteration that checks constraints
		// Same as above for the checking constrains but also checking for nearby obstacles
		
		return null;
	}
	
	private ASVConfig makeValidConfig(){
		
		return null;
	}
	
}
