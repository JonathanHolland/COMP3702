package fakeBrains;

import java.util.*;
import java.awt.geom.*;
import java.math.*;

import problem.ProblemSpec;
import problem.ASVConfig;

// This is a trivial representation of a Node
// In reality for this problem a node needs to represent ALL ASV's and their
// positions
// Therefore, the variables would be the same as contained in the current
// getASVPositions()
public class Node {
	// This iterates for every new Node that is created
	static int nodeIndex;

	// The position of the node
	private Point2D.Double pos;

	// The Configuration of the ASV's
	private ASVConfig config = null;

	// Should be unique for every new node
	private int ID;

	private List<Edge> edges; // Holds teh edges

	// Store the previous node to link the final path
	private Node previousNode;

	// Store the current cost up to this node from the start state
	private double cost;

	// Store the heuristic cost to the end state
	private double hcost;

	/* Init from x,y */
	public Node(double x, double y) {
		ID = nodeIndex++;
		this.pos = new Point2D.Double(x, y); // Make the point
		edges = new ArrayList<Edge>(); // #soedgy
	}

	/* Init from a point */
	public Node(Point2D.Double position) {
		ID = nodeIndex++;
		this.pos = new Point2D.Double(position.x, position.y); // Make the Point
		edges = new ArrayList<Edge>(); // #soedgy
	}

	/* Init from another Node */
	public Node(Node node) {
		// This Constructor doesn't increment nodeIndex 'cause you're just
		// cloning shtuff
		this.edges = new ArrayList<Edge>(node.edges);
		this.pos = new Point2D.Double(node.pos.x, node.pos.y);
		this.ID = node.ID;
	}

	/* return the Node's position */
	public Point2D.Double getPos() {
		return new Point2D.Double(pos.x, pos.y);
	}

	/* Return a list of the edges of a node */
	public List<Edge> getEdges() {
		return new ArrayList<Edge>(edges);
	}

	/* adds an edge that terminates at node */
	public void addEdge(Node node) {
		Edge temp = new Edge(this, node);
		edges.add(temp);
		node.edges.add(temp);
	}

	/* remove e from both nodes */
	public boolean rmEdge(Edge e) {
		boolean temp;
		try {
			temp = e.getOther(this).edges.remove(e);
		} catch (Exception ex) {
			return false;
		}
		;
		return edges.remove(e) && temp;

	}

	public List<Node> getAdjacent() throws NoSuchNodeException {
		List<Node> adjacent = new ArrayList<Node>();
		for (int i = 0; i < edges.size(); i++) {
			Node adding = edges.get(i).getOther(this);
			adjacent.add(adding);
		}
		return adjacent;
	}

	public int getID() {
		return ID;
	}

	public double getCost() {
		return this.cost + this.hcost;
	}

	public static void setNodeIndex(int num) {
		nodeIndex = num;
	}

	@Override
	public String toString() {
		return "Node" + ID + "@" + pos.x + "," + pos.y;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node)) {
			return false;
		}
		Node n = (Node) o; // the object to compare
		return (pos.equals(n.pos) && nodeIndex == n.nodeIndex);
	}

	public void setPrevious(Node previous) {
		this.previousNode = previous;

	}

	public void updateCost(Node previous) {
		// setting cost for the current node as the old nodes cost + the edge
		// weight between
		cost = previous.cost + this.getPos().distance(previous.getPos());
	}

	public double calcCost(Node previous) {
		return previous.cost + this.getPos().distance(previous.getPos())
				+ hcost;
	}

	public Node getPrevious() {
		return previousNode;
	}

	public void setCost(double i) {
		this.cost = i;
	}

	public void sethCost(Node current, Node end) {
		// Set the heuristic cost not only to the distance to the end state (in
		// reference to our node positions from our path which are the mass
		// centers of the configs) but also to include distances to nearby
		// obstacles
		Double distance;
		double obsx;
		double obsy;
		int obscount = 0;
		int outcodeval = 0;
		ArrayList<Double> obsnearby =  new ArrayList<Double>();
		for (int i = 0; i < Assignment1.obstacles.size(); i++) {
			Rectangle2D obs = Assignment1.obstacles.get(i).getRect();
			// Once we have the center of the obstacle, determine using outcode
			// which side the node is on (and then add minus the width/2 or
			// height/2 from the distance - to get edge distance to node)
			obsx = obs.getCenterX();
			obsy = obs.getCenterY();
			outcodeval = obs.outcode(current.getPos());

			if (outcodeval == Rectangle2D.OUT_TOP) {
				obsy = obsy + (obs.getHeight() / 2);
			} else if (outcodeval == Rectangle2D.OUT_BOTTOM) {
				obsy = obsy - (obs.getHeight() / 2);
			} else if (outcodeval == Rectangle2D.OUT_LEFT) {
				obsx = obsx - (obs.getWidth() / 2);
			} else if (outcodeval == Rectangle2D.OUT_RIGHT) {
				obsx = obsx + (obs.getWidth() / 2);
			}
			// This will get the center of one of the four edges closes to the
			// point, is that close enough?

			distance = current.getPos().distance(obsx, obsy);
			if (distance < 0.1) {
				obsnearby.add(distance);
				obscount++;
			}
		}
		System.out.println(obscount);
		double hcostintermediate = current.getPos().distance(end.getPos());
		while(obscount!=0) {
			hcostintermediate += (Math.exp(1/obsnearby.get(obscount-1)));
			obscount--;
		}
		
		this.hcost = hcostintermediate;
	}

	public void giveASVConfig(ASVConfig asvConfig) {
		this.config = asvConfig;
	}

	public ASVConfig getConfig() {
		return config;
	}
}
