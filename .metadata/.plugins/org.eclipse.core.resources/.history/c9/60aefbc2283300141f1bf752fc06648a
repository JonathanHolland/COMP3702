package fakeBrains;

import java.util.*;
import java.awt.geom.*;

import problem.Obstacle;

// The edge weight calculation is already done for us in ASVConfig
// Allowing us to compare the current position from getASVPositions()
// to the intended position constructed as a node to move to
// Then we simply utilise totalDistance(ASVConfig otherState);
// (Max distance is not necessarily what we want as we want to minimise OVER
// ALL ASV's)
public class Edge {
	private Node A;
	private Node B;
		
	public Edge(Node one, Node two){
		this.A = new Node(one);
		this.B = new Node(two);
	}
	
	/*
	 * If given one side, return the other.
	 */
	public Node getOther(Node side) throws NoSuchNodeException{
		if(side.equals(this.A)){
			return this.B;
		} else if (side.equals(this.B)) {
			return this.A;
		}
		throw new NoSuchNodeException("Node didn't exist in this edge");
	}
	
	/*
	 * Return the sides of an edge
	 */
	public List<Node> getSides(){
		List<Node> sides = new ArrayList<Node>();
		sides.add(A); sides.add(B);
		return sides;
	}
	
	public boolean isBlocked(List<Obstacle> o){
		for(int i = 0; i < o.size(); i++){
			// Get the rectangle of the obstacle and check that the line made by the 
			// edge doesn't intersect. if it does, return true
			if(o.get(i).getRect().intersectsLine(A.getPos().x, A.getPos().y, B.getPos().x, B.getPos().y)){
				return true;
			}
		}
		
		return false;
	}
	
	public double getLength(){
		return A.getPos().distance(B.getPos());
	}
	
	public Line2D getLine(){
		return new Line2D.Double(A.getPos(), B.getPos());
	}
	
	@Override
	public String toString(){
		return "Edge from: " + A.toString() + " to " + B.toString(); 
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		Edge e = (Edge) o; // the object to compare
		return (this.A.equals(e.A) && this.B.equals(e.B));
	}
}
