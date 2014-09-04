package fakeBrains;

import java.util.*;
<<<<<<< HEAD
import java.awt.geom.*;
=======
import problem.*;
>>>>>>> 4faebc539a2d83ef48074ae76b48e5410b501469

public class Interpolate {

	// The List of Obstacles
	private List<Obstacle> obstacles;
	
	// The Path found with Astar
	private	List<Node> path;
	
	public Interpolate(List<Obstacle> obstacles, List<Node> path){
		this.obstacles = obstacles;
		this.path = path;
	}
	
	// Not sure if we need this
	/**
	 * Blends everything together. interpolate would work between nodes
	 * but this is meant to spit out the over-all solution
	 * @return solution - every single ASVConfig moved through to reach the goal
	 */
	public List<ASVConfig> makeSolution(Node start, Node goal){
		List<ASVConfig> solution = new ArrayList<ASVConfig>();
		
		int i = 1;
		Node current = path.get(i++);
		Node previous = start;
		while(previous != goal){
			
			// Move between the 2
			solution.addAll(Interpolate(previous, current));

			// Update things for the next round
			previous = current;
			current = path.get(i++);
		}
		
		return null;
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
		Map<Point2D, Point2D> asvStartEnd =  new HashMap<Point2D, Point2D>();
		
		
		while(asvLeft) {
			// While there are still asv's to check, add the start and end positions of each one to a map
			asvStart.add
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
