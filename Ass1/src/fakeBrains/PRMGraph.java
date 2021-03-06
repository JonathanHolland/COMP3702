package fakeBrains;

import java.util.*;
import java.awt.geom.*;

import problem.*;
import tester.Tester;

public class PRMGraph {
	
	// The list of nodes
	List<Node> nodes;
	// The List of Points
	List<Point2D> points;
	
	// The List of Obstacles
	List<Obstacle> obstacles;
	
	// le initial range
	double range;
	
	// Really dodgy constructor
	public PRMGraph(){
		nodes = new ArrayList<Node>();
		points = new ArrayList<Point2D>();
	}
	
	/* Populates a space the size of mapSize with points.
	 * Converts them to nodes and connects nodes within range if they
	 * there can be a legitimate connection.
	 */
	public PRMGraph(List<Obstacle> obstacles, double range, int pointCount, Point2D.Double mapSize){
		// Init them thingos
		nodes = new ArrayList<Node>();
		points = new ArrayList<Point2D>();
		this.range = range;
		this.obstacles = obstacles;
		Node.setNodeIndex(0);
		
		// Make the points
		System.out.print("Making points -> ");
		makePoints(mapSize, pointCount);
		System.out.print("Converting to Nodes -> ");
		this.nodes = convertPoints2Nodes(points);
		System.out.print("Linking Nodes -> ");
		connectNodes();
		System.out.println("DONE");
	}
	
	/* Alternate constructor for when you don't want to change mapsize */
	public PRMGraph(List<Obstacle> obstacles, double range, int pointCount){
		this(obstacles, range, pointCount, new Point2D.Double(1.0,1.0));
	}
	
	public PRMGraph(List<Obstacle> obstacles, double range){
		this.obstacles = obstacles;
		this.range = range;
		nodes = new ArrayList<Node>();
		points = new ArrayList<Point2D>();
	}
	
		
	/* Connects the nodes if the connections are valid and they are within a certain range */
	private void connectNodes(){
		for(int i = 0; i < nodes.size(); i++)
			for(int k = 0; k < nodes.size(); k++){
				// For each node, check if there's a valid connection to another node nearby
				Node a = new Node(nodes.get(i)); Node b = new Node(nodes.get(k)); // don't make pointers
				// if we're not the same and the distance is good
				if(!a.equals(b) && b.getPos().distance(a.getPos()) <= range){
					// make a temp edge
					Edge edge = new Edge(a, b);
					// if the edge isn't blocked add the edge to the nodes
					if(!edge.isBlocked(obstacles)){
						nodes.get(i).addEdge(nodes.get(k));
					}
				}
			}
	}
	
	private void connectNode(Node n){
		for(int k = 0; k < nodes.size(); k++) {
		// For each node, check if there's a valid connection to another node nearby
			Node b = nodes.get(k);
			
			// if we're not the same and the distance is good
//			System.out.print("E: " + n.equals(b) + " Dist: " + b.getPos().distance(n.getPos()));
			if(!n.equals(b) && b.getPos().distance(n.getPos()) <= range){
				// make a temp edge
				Edge edge = new Edge(n, b);
//				System.out.println(" Blocked: " + edge.isBlocked(obstacles));
				// if the edge isn't blocked add the edge to the nodes
				if(!edge.isBlocked(obstacles)){
//					System.out.println("Added " + edge);
					n.addEdge(nodes.get(k));
				}
			}
		}
	}
	
	
	/* Makes an amount of points and randomly distributes them through the area mapSize.
	 * If a point lands in an obstacle it is shifted until it isn't by using obstaclePoint
	 */
	private void makePoints(Point2D.Double mapSize, int num){
		// Start 
		Random tempRand = new Random(); boolean badSpot;
		Random rand = new Random(tempRand.nextInt());
		for(int i=0; i < num; i++){
			badSpot = false;
			double ran1 = rand.nextDouble();
			//System.out.println("Using " + ran1 + " as a seed");
			double ran2 = rand.nextDouble();
			Point2D.Double temp = new Point2D.Double((mapSize.x * ran1), (mapSize.y * ran2));
			for(int k = 0; k < obstacles.size(); k++){
				Rectangle2D tempRect = Tester.grow(obstacles.get(k).getRect(), 0.1);
				if(tempRect.contains(temp.x, temp.y)) badSpot = true;
			}
			if(badSpot){
				points.add(obstaclePoint(temp));			
			}
			if(!badSpot) points.add(temp);
		}
		//System.out.println(points.size());
	}
	
	/** Takes a list of points, makes them into nodes and connects 
	 * the nodes to all their possible neighbours.
	 * Returns a Hashmap of the Nodes with their Points as their keys
	 */
	public Map<Point2D, Node> addPoints(List<Point2D> positions) {
		// Make map to return
		Map<Point2D, Node> newPoints = new HashMap<Point2D, Node>();
		// convert the points to nodes
		List<Node> newNodes = convertPoints2Nodes(positions);
//		System.out.println(newNodes);
		
		// Connect the Nodes
		for(int i = 0; i < newNodes.size(); i++){
			this.nodes.add(newNodes.get(i));
			this.points.add(newNodes.get(i).getPos());
			connectNode(newNodes.get(i));
		}
		
		// Make the map to return
		for(int i = 0; i < positions.size(); i++){
			newPoints.put(positions.get(i), newNodes.get(i));
		}
		return newPoints;
	}
	
	/**
	 * 
	 * @param pos
	 * @return
	 */
	public Node addPoint(Point2D pos){
		List<Point2D> tempList = new ArrayList<Point2D>();
		tempList.add(pos);
		return addPoints(tempList).get(pos);
	}
	
	/**
	 * 
	 * @param positions
	 * @return
	 */
	private List<Node> convertPoints2Nodes(List<Point2D> positions) {
		List<Node> n = new ArrayList<Node>();
		for(int i = 0; i < positions.size(); i++){
			n.add(new Node(positions.get(i).getX(), positions.get(i).getY()));
		}
		return n;
	}

	/**
	 * 
	 * 
	 * @param p
	 * @return
	 */
	private Point2D.Double obstaclePoint(Point2D.Double p){
		Random r = new Random(); boolean badSpot = false;
		//System.out.println("Got: " + r.nextGaussian());
		double newX = p.x + range*r.nextGaussian();
		double newY = p.y + range*r.nextGaussian();
		newX = newX <= 0 ? newX*-1.0 : newX;
		newY = newY <= 0 ? newY*-1.0 : newY;
		newX = newX > 1.0 ? 1.0 : newX;
		newY = newY > 1.0 ? 1.0 : newY;
		Point2D.Double newP = new Point2D.Double(newX, newY);
		for(int k = 0; k < obstacles.size(); k++){
			// Make the used rectangle slightly bigger, so we don't get points too close to edges
			Rectangle2D temp = Tester.grow(obstacles.get(k).getRect(), 0.01);
			if(temp.contains(newP.x, newP.y)) badSpot = true;
		}
		if(badSpot) return obstaclePoint(newP);
		else return newP;
	}
	
	
	public List<Point2D> getPoints(){
		return new ArrayList<Point2D>(points);
	}
	
	public List<Node> getNodes(){
		return new ArrayList<Node>(nodes);
	}

	
	// Give the initial state of the program to make a node from and add it to the graph
	public Node giveInitialState(ASVConfig initialState) {
		// Add the point to the Graph
		Node toReturn = this.addPoint(initialState.massCenter());
		
		// Give the node the goal state ASV
		toReturn.giveASVConfig(initialState);

		return toReturn;
	}

	public Node giveGoalState(ASVConfig goalState) {
		// Add the point to the Graph
		Node toReturn = this.addPoint(goalState.massCenter());
		
		// Give the node the goal state ASV
		toReturn.giveASVConfig(goalState);
		
		// return the Node
		return toReturn;
	}
	
}
