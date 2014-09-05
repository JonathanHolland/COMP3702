package fakeBrains;

import fakeBrains.*;
import java.util.*;
import java.awt.geom.*;

public class Astar {

	 /**
     *
     */
    public List<Node> findPath(Node first, Node end) throws NoSuchNodeException {
    	PriorityQueue<Node> pQueue = new PriorityQueue(20, new NodeComparator());
    	List<Node> openList = new LinkedList<Node>();
        List<Node> closedList = new LinkedList<Node>();
        first.setCost(0);
        openList.add(first); // add starting node to open list
        pQueue.add(first);
        Node goal =  null;
        
        while (openList.size()>0) {
            Node current = pQueue.poll();
            openList.remove(current); // delete current node from open list

            if (current.equals(end)) { // found goal
                //return calcPath(first, current);
            	goal = current;
            	break;
            }
            
            closedList.add(current); // add current node to closed list
            
            // for all adjacent nodes:
            List<Node> adjacentNodes = current.getAdjacent();
            for(int i = 0; i < adjacentNodes.size(); i++) {
            	Node currentAdj = adjacentNodes.get(i);
            	if(closedList.contains(currentAdj)) {
            		// If the node's in the closedList do nothing
                } else if (!openList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCost(current,end); // set h costs of this node (estimated costs to goal)
                    currentAdj.updateCost(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                    pQueue.add(currentAdj);
                } else { // node is in openList
                    if (currentAdj.getCost() > currentAdj.calcCost(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.updateCost(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

        }

        if(goal !=null) {
        	return calcPath(first,goal);
        }
        
        
        return null; // unreachable
    }

	private List<Node> calcPath(Node first, Node current) {
		List<Node> path = new ArrayList<Node>();
		Node nodeNext;
		boolean firs = false;
		
		// Add the current node as the first node in the path
		// Note now that the path will be reversed in actuality
		path.add(current);
		
		while(!firs) {
			// When we get all the way to the first node from the current node, firs is reached (so is t)
			nodeNext =  current.getPrevious();
			if(nodeNext.equals(first)){
				firs = true;
			}
			path.add(0,nodeNext);
			current = nodeNext;
		}
		return path;
	}
	
	
	
	
	
	
}
