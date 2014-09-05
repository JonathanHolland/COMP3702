package fakeBrains;

import java.awt.geom.*;
import java.lang.reflect.Array;
import java.util.*;

import fakeBrains.*;
import tester.Tester;
import problem.*;


public class Configurator {
	
	// The path through the map
	List<Node> p;
	
	// The obstacles
	List<Obstacle> o;
	
	// tester instance
	Tester test;
	
	
	/**
	 * Default Constructor
	 * Takes and sets up needed vars
	 * @param path
	 * @param obstacles
	 */
	public Configurator(List<Node> path, List<Obstacle> obstacles){
		this.p = path;
		this.o = obstacles;
		test = new Tester();
	}
	
	/**
	 * Spider over all the nodes in path and create valid ASVConfigs
	 * for each of them for Interpolate to use later
	 * @param path
	 * @return true if success
	 */
	public boolean giveConfigurations(){
		// Shortcut goal node
		Node goal = p.get(p.size()-1);
		
		// counter
		int i = 1;
		
		// can ignore the first node, as it is the starting node
		Node current;
		Node prev = p.get(0);
		
		// Time to Spider
		while(i < p.size()-1){
			current = p.get(i++);
			
			// Make a tentative config the same shape as the last node's
			ASVConfig t = new ASVConfig(current.getPos(), prev);
			
			// If there is no collision at this node then there's no worry :D
			// Just give current prev's shifted config
			if(!test.hasCollision(t, o)){
				current.giveASVConfig(new ASVConfig(t));
				prev = current;
				continue;
			}
			
			// Now check if we could fit purely with rotation
			ASVConfig t2 = checkRotations(t, current.getPos());
			
			// if t2 != null we have a valid solution and can continue
			if(t2 != null) {
				current.giveASVConfig(new ASVConfig(t2));
				prev = current;
				continue;
			}
			
			/* If we're still here that means no matter how we rotate this
			 * config it's not fitting.
			 * Let's figure out which ASVs are causing the pain and shift
			 * them slightly
			 */
			
			

			
			// Update things for the next round
			prev = current;
		}
		
		
		return true;
	}

	private ASVConfig checkRotations(ASVConfig prevConfig, Point2D center) {

		for(int angle = 0; angle < 360; angle++) {
			Point2D tempPosArray[] = new Point2D[prevConfig.getASVCount()];
			
			// Make the turn
			AffineTransform rawr = AffineTransform.getRotateInstance(Math.toRadians(angle), center.getX(), center.getY());
			rawr.transform(prevConfig.getASVPositions().toArray(new Point2D[0]), 0, tempPosArray, 0, prevConfig.getASVCount());
			
			// make a new config from the turned points
			ASVConfig t2 = new ASVConfig(Arrays.asList(tempPosArray));
			
			// Check if we still have a collision
			if(test.hasCollision(t2, o)) {
				// If we do, try the next angle
				continue;
				
			} else {
				// If we're now free return the free config
				return new ASVConfig(t2);
			}
		}
		return null;
	}
	
}
