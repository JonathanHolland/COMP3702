package fakeBrains;

import java.awt.geom.*;
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
		Node current = p.get(i);
		Node prev = p.get(0);
		
		// Time to Spider
		while(prev != goal){
			ASVConfig t = new ASVConfig(current.getPos(), prev.getConfig());

			// If there is no collision at this node then there's no worry :D
			// Just give current prev's shifted config
			if(!test.hasCollision(t, o)){
				current.giveASVConfig(new ASVConfig(t));
				prev = current;
				current = p.get(i++);
				continue;
			}
			
						
			

			
			// Update things for the next round
			prev = current;
			current = p.get(i++);
		}
		return true;
	}
	
}
