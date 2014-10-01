package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mcts.TreeNode;
import problem.*;
import problem.Cycle.Speed;

public class MonteNode {
//	A list of all possible actions
	static final Action actions[] = {Action.FS, Action.NE, Action.SE, Action.ST, Action.FM, Action.FF};
	static double epsilon = 1e-6;
	int nActions = 4;
	
	RaceState state;
	Action action;
	double nVisits, totValue;
	MonteNode children[];
	
	public MonteNode(Action action) {
		this.action = action;
		
	}
	
	public MonteNode(Action action, Cycle cycle) {
		this.action = action;
		if(cycle.getSpeed() == Speed.FAST) {
			nActions = 6;
		} else if(cycle.getSpeed() == Speed.MEDIUM) {
			nActions = 5;
		}
	}
	
	public  Action bestAction() {
		List<MonteNode> visited = new LinkedList<MonteNode>(); // make a list of visited things
        MonteNode cur = this;
        visited.add(this); // add us to the visited list
        while (hasChildren()) {
            cur = cur.select();
             System.out.println("Adding: " + cur);
            visited.add(cur);
        }
        
		return null;
	}
	
	private MonteNode select() {
        TreeNode selected = null;
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
            children[i] = new MonteNode(actions[i]);
        }
    }
    
    public boolean rollOut(TreeNode tn) {
        // Simulate the rest of the race here
        return false;
    }
    
    public boolean hasChildren() {
        return children != null;
    }
	
	
}
