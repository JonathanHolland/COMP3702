package fakeBrains;

import java.util.*;
import java.awt.geom.*;

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
	 * @param start - the node to begin at
	 * @param end - the node to end at
	 * @return the list of the states created in between start and end
	 */
	public List<ASVConfig> pathBetween(Node start, Node end) {
		
			List<ASVConfig> pathPiece = new ArrayList<ASVConfig>();
		
			int i = start.getConfig().getASVCount();
			int j = 0;
			Map<Point2D, Point2D> asvStartEnd =  new HashMap<Point2D, Point2D>();
			Map<Integer, Double> primitiveSteps =  new HashMap<Integer, Double>();
			
			while(i>j) {
				// Assign variables to positions
				Point2D positionOne = start.getConfig().getPosition(j);
				Point2D positionTwo = end.getConfig().getPosition(j);
				// While there are still asv's to check, add the start and end positions of each one to a map
				asvStartEnd.put(positionOne, positionTwo);
				// Use the start and end positions to extract how many primitive steps each one needs to travel
				primitiveSteps.put(j,positionOne.distance(positionTwo)/0.001);
				j++;
			}
			
			ASVConfig stepPos;
			ASVConfig currentPos = start.getConfig();
			List<Point2D> cPos;
			boolean endReached =  false;
			while(!endReached) {
				cPos = currentPos.getASVPositions();
				double[] sPos = new double[currentPos.getASVCount()*2];
				int count = 0;
				double[] listxy;
				for(int k=0;k<cPos.size();k++) {
					listxy = xymove(cPos.get(k).getX(), cPos.get(k).getY(), end.getConfig().getPosition(k));
					sPos[count] = (cPos.get(k).getX()+listxy[0]); // add x movement
					sPos[count+1] = (cPos.get(k).getY()+listxy[1]); // add y movement
					count = count+2;
				}
				stepPos = new ASVConfig(sPos);
				
				// Add position config to ASVconfig list to return
				pathPiece.add(stepPos);
				
				currentPos = stepPos;
				
				if(currentPos.equals(end.getConfig())) {
					endReached = true;
				}
			}
			// CHECK CONSTRAINS EACH LOOP
			// CHECK FOR OBSTACLES EACH LOOP
			
			return pathPiece;
		}
	
	/**
	 * xymove calculates the required x and y distances to move the asv
	 * in order to not exceed 0.001 and to do so in the direction of the goal
	 * 
	 * @param y 
	 * @param x 
	 * @param endpos 
	 */
	private double[] xymove(double x, double y, Point2D endpos) {
		double[] resultxy = new double[2];
		boolean negx;
		boolean negy;
		double xmove;
		double ymove;
		
		// Compare the current x and y to the end state
		// We need to do this every state we move just in case
		// we had to override for obstacles
		double resx = (endpos.getX() - x);
		double resy = (endpos.getY() - y);
		// Check direction and take absolute for trig
		if(resx > 0) {
			negx = false;
		} else {
			negx = true;
			resx = Math.abs(resx);
		}
		if(resy > 0) {
			negy = false;
		} else {
			negy = true;
			resy = Math.abs(resy);
		}
		
		// Use similar triangles to take angles for direction of move
		double thetaa = Math.acos(resy/resx);
		double thetab = (0.5*Math.PI)-thetaa;
		
		// Use these angles in the sine rule subbed into pythagoras to find x and y distances with a hypotenuse of 0.001
		double signratio = Math.sin(thetab)/Math.sin(thetaa); 
		xmove = (signratio)/(Math.sqrt(1000000*Math.pow(signratio,2)+1000000));
		ymove = (Math.sin(thetaa)*xmove)/Math.sin(thetab);
		
		resultxy[0] = xmove;
		resultxy[1] = ymove;
		
		return resultxy;
	}

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
			solution.addAll(pathBetween(previous, current));

			// Update things for the next round
			previous = current;
			current = path.get(i++);
		}
		
		return null;
	}
	
	
	
	private ASVConfig makeValidConfig(){
		
		return null;
	}
	
}
