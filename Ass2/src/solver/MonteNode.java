package solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import problem.*;

public class MonteNode {
	static Random r = new Random();
	// A list of all possible actions
	static final Action actions[] = {Action.FS, Action.NE, Action.SE, Action.ST, Action.FM, Action.FF};
	static double epsilon = 1e-6;
	static int nActions = 4; // may need to be bigger
	
	RaceState state;
	Track track;
	
	Action action;
	double nVisits, totValue;
	List<MonteNode> children = new ArrayList<MonteNode>();
	
	public MonteNode(Tour tour) {
		state = tour.getLatestRaceState();
		track = tour.getCurrentTrack();
	}
	
	public MonteNode(Action action, RaceState state) {
		this.action = action;
		
	}

	public void exploitExpand() {
		List<MonteNode> visited = new LinkedList<MonteNode>(); // make a list of visited things
        MonteNode cur = this;
        visited.add(this); // add us to the visited list
        //System.out.println("Exploit Expand ");
        while (cur.hasChildren()) {
            if(!(cur.select() == null)) {
            	cur = cur.select();
                //System.out.println("Adding: " + cur);
                visited.add(cur);
            }
        	
        }
        cur.expand();
        MonteNode newNode = cur.select();
        visited.add(newNode);
        double value = rollOut(newNode);
        for (MonteNode node : visited) {
            // would need extra logic for n-player game
            // System.out.println(node);
            node.updateStats(value);
        }
	}
	
	public MonteNode select() {
        MonteNode selected = null;
        double bestValue = Double.MIN_VALUE;
        for (MonteNode c : children) {
            double uctValue = 
                    c.totValue / (c.nVisits + epsilon) +
                            Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
                            r.nextDouble() * epsilon;
            // small random number to break ties randomly in unexpanded nodes
            // System.out.println("UCT value = " + uctValue);
            if (uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        // System.out.println("Returning: " + selected);
        return selected;
    }
	
    public void expand() {
    	children = new ArrayList<MonteNode>(); // make a new node for each possible action
        for (int i=0; i<nActions; i++) {
            children.add(new MonteNode(actions[i], state));
        }
    }
    
    /**
     * simulate the game into the future
     * @param n
     * @return
     */
    
    // This function is broke at the moment because sim isn't a variable.
    // But this is where you play the game into the future to see if this is a valid route.
    public double rollOut(MonteNode n) {
    	// Make a timeout period
//    	int count = sim.getTrack().getNumCols()+3;
//        
//    	// make the Actions list
//    	List<Action> A = new ArrayList<Action>();
//    	A.add(Action.FS);
//    	while(!sim.isFinished() || count-- > 0) {
//        	sim.stepTurn(A);
//        }
//    	
//    	if(sim.getCurrentStatus() == RaceState.Status.WON) {
//    		return sim.getTrack().getPrize() - sim.getTotalDamageCost();
//    	}
        return 1;
    }
    
    public boolean hasChildren() {
        return (!children.isEmpty());
    }
    
    public void updateStats(double value) {
        nVisits++;
        totValue += value;
    }
	
	
}
