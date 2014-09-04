package fakeBrains;

import java.awt.geom.*;
import java.util.*;
import fakeBrains.*;
import tester.Tester;
import problem.*;


public class Configurator {
	
	/**
	 * Spider over all the nodes in path and create valid ASVConfigs
	 * for each of them for Interpolate to use later
	 * @param path
	 * @return true if success
	 */
	public static boolean giveConfigurations(List<Node> path){
		// Shortcut to the goal Node
		Node goal = path.get(path.size()-1);
		
		// can ignore the first node, as it is the starting node
		Node current = path.get(1); 
		while(current != goal){
			
			
			
			
			
			
			
		}
		return true;
	}
	
}
