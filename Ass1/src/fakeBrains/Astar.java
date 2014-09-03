package fakeBrains;

import fakeBrains.*;
import java.util.*;
import java.awt.geom.*;

public class Astar {

	 /**
     *
     */
    public List<Node> findPath(Node first, Node end) throws NoSuchNodeException {
    	List<Node> openList = new LinkedList<Node>();
        List<Node> closedList = new LinkedList<Node>();
        first.setCost(0);
        openList.add(first); // add starting node to open list

        double costFromStart = 0;
        double costToEnd = costFromStart + guessToEnd();
        
        boolean done = false;
        Node current = first;
        while (!done) {
            
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if (current.equals(end)) { // found goal
                return calcPath(first, current);
            }
            // for all adjacent nodes:
            List<Node> adjacentNodes = current.getAdjacent();
            for(int i = 0; i < adjacentNodes.size(); i++) {
            	Node currentAdj = adjacentNodes.get(i);
            	if(closedList.contains(currentAdj)) {
                	continue;
                }
            	if (!openList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    //currentAdj.sethCosts(nodes[endX][endY]); // set h costs of this node (estimated costs to goal)
                    currentAdj.updateCost(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.getCost() > currentAdj.calcCost(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.updateCost(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

            if (openList.isEmpty()) { // no path exists
                return new LinkedList<Node>(); // return empty list
            }
            try {
            	current = closestNodeInOpen(current, openList); // get node with lowest fCosts from openList
            } catch(NullPointerException e) {
            	return calcPath(first,current);
            }
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
			path.add(nodeNext);
			current = nodeNext;
		}
		return path;
	}

	private Node closestNodeInOpen(Node f, List<Node> openList) throws NoSuchNodeException {
		List<Edge> edgeList = f.getEdges();
		Edge shortestEdge = null;
		double shortest = 0;
		boolean firsttime = true;
		// For each edge listed from the current node f
		// check the distance, and take the lowest
		// IFF it's not in closedList i.e. It's in openList
		if(edgeList.size()!=0) {
			for(int i=0; i<edgeList.size(); i++) {
				if(openList.contains(edgeList.get(i).getOther(f))){
					if(firsttime) {
						shortest = edgeList.get(i).getLength();
						shortestEdge = edgeList.get(i);
					} else {
						if(edgeList.get(i).getLength() < shortest) {
							shortestEdge = edgeList.get(i);
						}
					} 
					firsttime = true;
				}
			}
		return shortestEdge.getOther(f);
		} else {
			throw new NoSuchNodeException("The edgeList contains no edges for this node");
		}
	}
	
	private double guessToEnd(){
		return 0;
	}
	
	
	
	
	
	
}