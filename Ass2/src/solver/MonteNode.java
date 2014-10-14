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
	Tour tour;
	
	Action action;
	double nVisits, totValue;
	List<MonteNode> children = new ArrayList<MonteNode>();
	
	public MonteNode(Tour tour) {
		this.tour = tour;
		state = tour.getLatestRaceState();
		track = tour.getCurrentTrack();
		// Make sure we can go really fast
		Cycle.Speed cSpeed = state.getPlayers().get(0).getCycle().getSpeed();
		if(cSpeed == Cycle.Speed.FAST) {
			nActions = 6; // We have access to both FM and FF
		} else if(cSpeed == Cycle.Speed.MEDIUM) {
			nActions = 5; // we have access to FM
		}
	}
	
	public MonteNode(Action action, Tour tour) {
		this.action = action;
		this.tour = tour;
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
            children.add(new MonteNode(actions[i], tour));
        }
    }
    
    /**
     * simulate the game into the future
     * @param n
     * @return
     */
    
    // This function is broke at the moment because sim isn't a variable.
    // Racesim is now a function in the problem package, is this what you meant?
    
    // But this is where you play the game into the future to see if this is a valid route.
    public double rollOut(MonteNode n) {
    	int count = tour.getCurrentTrack().getNumCols()*2;
    	int raceIndex = tour.getRaceIndex();
    	
    	// Make a simulator from the current tour data
    	RaceSim sim = new RaceSim(new ArrayList<RaceState>(tour.getStateHistory(raceIndex)), 
    			new ArrayList<ArrayList<Action>>(tour.getActionHistory(raceIndex)), tour.getCurrentTrack(), new Random());
    	
    	
    	List<Action> A = new ArrayList<Action>();
    	A.add(n.action);
    	while(count-- > 0 && !sim.isFinished()) {
    		sim.stepTurn(A);
    		A.clear();
    		A.add(defaultPolicy(sim.getCurrentState()));
        }
    	if(!sim.isFinished()) {
    		System.out.println("We didn't reach the end in time");
    	}
    	
    	// We shouldn't base it off getting to the finish so soon,
    	// instead it should just be the damage to move towards the goal?
    	
    	if(sim.getCurrentStatus() == RaceState.Status.WON) {
    		return sim.getTrack().getPrize() - sim.getTotalDamageCost();
    	}
        return 1 - sim.getTotalDamageCost();
    	
//    	count = 0;
//    	for(MonteNode c : n.children) {
//    		if(c.state.getTotalDamageCost()>n.state.getTotalDamageCost()) {
//    			// this is a bad thing
//    			count++;
//    		}
//    	}
//    	// Something like if too many of the possible children end in damage
//    	// return the bad 0 value, otherwise return 1?
//    	if(count>(n.children.size()/3)) {
//    		return 0;
//    	} else {
//    		return 1;
//    	}
    }
    
    public boolean hasChildren() {
        return (!children.isEmpty());
    }
    
    public void updateStats(double value) {
        nVisits++;
        totValue += value;
    }
	
    public Action defaultPolicy(RaceState state) {
    	GridCell p = state.getPlayers().get(0).getPosition();
    	int r = track.getNumRows(), c = track.getNumCols();
    	
    	if(track.getCellType(posIfMove(Action.FS)) == Track.CellType.OBSTACLE) {
    		try { // could error
				if(track.getCellType(posIfMove(Action.NE)) != Track.CellType.OBSTACLE) {
	    			return Action.NE;
    		}
    		} catch (Exception ex) {}
    		
    		try { // could error
    			if(track.getCellType(posIfMove(Action.SE)) != Track.CellType.OBSTACLE){
    				return Action.SE;
    		}
    		} catch (Exception ex) {}
    	}
    	if(c > p.getCol()+2 && track.getCellType(posIfMove(Action.FM)) == Track.CellType.OBSTACLE) {
    		return Action.FS;
    	}
    	if(c > p.getCol()+3 && track.getCellType(posIfMove(Action.FF)) == Track.CellType.OBSTACLE) {
    		return Action.FM;
    	}
    	return Action.FF;
    }
    
    private GridCell posIfMove(Action a) {
    	GridCell p = state.getPlayers().get(0).getPosition();
    	if(a == Action.FF) {
    		return new GridCell(p.getRow(), p.getCol()+3);
    	} else if(a == Action.FM) {
    		return new GridCell(p.getRow(), p.getCol()+2);
    	} else if(a == Action.FS) {
    		return new GridCell(p.getRow(), p.getCol()+1);
    	} else if(a == Action.NE) {
    		return new GridCell(p.getRow()-1, p.getCol()+1);
    	} else if(a == Action.SE) {
    		return new GridCell(p.getRow()+1, p.getCol()+1);
    	} else {
    		return new GridCell(p.getRow(), p.getCol());
    	}
    }
    
    @Override
    public String toString() {
    	GridCell pos = state.getPlayers().get(0).getPosition();
    	return "P" + pos.getRow() + "," + pos.getCol() + "-A_" + action + "-C" + children.size();
    }
	
}
