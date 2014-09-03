package fakeBrains;

import fakeBrains.*;
import java.util.*;
import java.awt.geom.*;

public class Astar {

	 /**
     *
     */
    public List<Node> findPath(Node first, Node end) throws NoSuchNodeException {
    	 System.out.println("#############################_STARTING_ASTAR_#################################################");
    	
    	List<Node> openList = new LinkedList<Node>();
        List<Node> closedList = new LinkedList<Node>();
        first.setCost(0);
        openList.add(first); // add starting node to open list

        double costFromStart = 0;
        double costToEnd = costFromStart + guessToEnd();
        
        boolean done = false, firstRun = true;
        Node current = first;
        while (!done) {
        	System.out.println("~~~~~~~~~~~~~~~~~~~_NEW_LOOP_~~~~~~~~~~~~~~~~~~~~~~~~");
        	
        	if(firstRun) {
        		current = first;
        		firstRun = false;
        	}
        	else current = closestNodeInOpen(current); // get node with lowest fCosts from openList
        	
        	System.out.println("Old Node: " + current);
        	System.out.println("Old Node Edges: " + current.getEdges());
            
            System.out.println("Current Node: " + current);
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if (current.equals(end)) { // found goal
                return calcPath(first, current);
            }
            
            if(closedList.size()>200) {
            	System.out.println(openList);
            	return calcPath(first,current);
            }
            // for all adjacent nodes:
            List<Node> adjacentNodes = current.getAdjacent();
            System.out.println("AdjNodes: " + adjacentNodes);
            for(int i = 0; i < adjacentNodes.size(); i++) {
                Node currentAdj = adjacentNodes.get(i);
                if (closedList.contains(currentAdj))
                {
                	continue; // If this is true we aren't interested in this adjacent node
                			  // so skip to the next one. 
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
            System.out.println("oL" + openList.size());
            System.out.println(closedList.size());
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
		
		while(firs) {
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

	private Node closestNodeInOpen(Node f) throws NoSuchNodeException {
		List<Edge> edgeList = f.getEdges();
		Edge shortestEdge;
		// For each edge listed from the current node f
		// check the distance, and take the lowest
		if(edgeList.size()!=0) {
			double shortest = edgeList.get(0).getLength();
			shortestEdge = edgeList.get(0); 
			
			if(edgeList.size()>1) {
				for(int i=1; i <edgeList.size(); i++) {
					if(edgeList.get(i).getLength() < shortest) {
						shortestEdge = edgeList.get(i);
					}
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
