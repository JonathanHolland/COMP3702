package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mcts.TreeNode;
import problem.*;
import problem.Cycle.Speed;

public class MonteNode {
	static Random r = new Random();
	// A list of all possible actions
	static final Action actions[] = {Action.FS, Action.NE, Action.SE, Action.ST, Action.FM, Action.FF};
	static double epsilon = 1e-6;
	static int nActions = 4; // may need to be bigger
	
	RaceState state;
	RaceSim sim;
	Track track;
	List<Player> players = new ArrayList<Player>();
	Action action;
	double nVisits, totValue;
	MonteNode children[];
	
	public MonteNode(Player player, Track track) {
		this.track = track;
		players.add(player);
		
		this.state = new RaceState(players, track.getOpponents(), track.getDistractors());
		this.sim = new RaceSim(state, track);
	}

	public MonteNode(Action action, RaceSim sim) {
		this.action = action;
		this.sim = new RaceSim(sim.getCurrentState(), sim.getTrack());
		List<Action> A = new ArrayList<Action>();
		A.add(action);
		sim.stepTurn(A); // Progress the simulation to be 
	}
	
	public MonteNode(Action action, Cycle cycle, Track track) {
		this.action = action;
		if(cycle.getSpeed() == Speed.FAST) {
			nActions = 6;
		} else if(cycle.getSpeed() == Speed.MEDIUM) {
			nActions = 5;
		}
	}
	
	public void bestAction() {
		List<MonteNode> visited = new LinkedList<MonteNode>(); // make a list of visited things
        MonteNode cur = this;
        visited.add(this); // add us to the visited list
        while (hasChildren()) {
            cur = cur.select();
            System.out.println("Adding: " + cur);
            visited.add(cur);
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
        children = new MonteNode[nActions]; // make a new node for each possible action
        for (int i=0; i<nActions; i++) {
            children[i] = new MonteNode(actions[i], sim);
        }
    }
    
    /**
     * simulate the game into the future
     * @param n
     * @return
     */
    public double rollOut(MonteNode n) {
    	// Make a timeout period
    	int count = sim.getTrack().getNumCols()+3;
        
    	// make the Actions list
    	List<Action> A = new ArrayList<Action>();
    	A.add(Action.FS);
    	while(!sim.isFinished() || count-- > 0) {
        	sim.stepTurn(A);
        }
    	
    	if(sim.getCurrentStatus() == RaceState.Status.WON) {
    		return sim.getTrack().getPrize() - sim.getTotalDamageCost();
    	}
        return -sim.getTotalDamageCost();
    }
    
    public boolean hasChildren() {
        return children != null;
    }
    
    public void updateStats(double value) {
        nVisits++;
        totValue += value;
    }
	
	
}
