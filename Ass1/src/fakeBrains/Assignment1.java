package fakeBrains;

import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.util.*;

import problem.*;
import visualDebugger.VisualHelper;

public class Assignment1 {

	public static List<Obstacle> obstacles;
	
	public static void main(String[] args) throws NoSuchNodeException {
		ProblemSpec problem = new ProblemSpec();
		try {
			problem.loadProblem("testcases/7ASV-easy.txt");
		} catch (Exception x) {
			System.out.println("The file failed to load. Make sure it was legit!");
		}
		
		obstacles = problem.getObstacles();
		
		// Declare vars for use inside the do-while loop.
		PRMGraph prm; 
		Astar alg;
		List<Node> path;
		Node start, goal;
		int attempt = 1;
		do {
			System.out.println("Pathing Attempt #" + attempt);
			
			// Generate the Probabilistic Road Map 
			prm = new PRMGraph(problem.getObstacles(), 0.2, 500);		
			
			// Make and add the beginning and end points to the Graph
			start = prm.giveInitialState(problem.getInitialState());
			goal = prm.giveGoalState(problem.getGoalState());
			
			// Next we get the path through the map
			alg = new Astar();
	
			// Find a path through the Graph
			path = alg.findPath(start, goal);
			
			// Increment attempts
			attempt++;
		} while (path == null);
		
		// We've found a path, so show us
		System.out.println("We found a path!");
		System.out.println("PATH: " + path);
		
		// Display what we have	with visualHelper
		VisualHelper visualHelper = new VisualHelper();
		
		visualHelper.addLinkedPoints(nodes2Points(path));
		
		visualHelper.addPoints(prm.getPoints());
		visualHelper.addRectangles(Ob2Rec(problem.getObstacles()));
		visualHelper.addLinkedPoints(start.getConfig().getASVPositions());
		visualHelper.addLinkedPoints(goal.getConfig().getASVPositions());
		
		visualHelper.repaint();
		
		boolean configd = false;
		
		// Node find what each node should be like
		System.out.println("Configure each node");
		Configurator configor = new Configurator(path, problem.getObstacles());
		configd = configor.giveConfigurations();
		
		System.out.println("Displaying Configured Nodes");
		
		// Add all the intermediate configs to the picture
		if(configd) {
			for(int i = 0; i < path.size(); i++) {
//				System.out.println(path.get(i).getConfig().getASVPositions());
				if(path.get(i).getConfig().getASVPositions() != null) {
					visualHelper.addLinkedPoints(path.get(i).getConfig().getASVPositions());
				}
			}
			visualHelper.repaint();
		}

		// Interpolation!11!1
		System.out.println("Interpolate between each node");
		Interpolate inter = new Interpolate(problem.getObstacles(), path);
		problem.setPath(inter.makeSolution(start, goal));
		
		try {
			String savename = "testcases/solution.txt";
			problem.saveSolution(savename);
			System.out.println("Saved to " + savename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		visualHelper.repaint();
		
	}
	
	
	/**
	 * Converts a list of Nodes to a list of Point2Ds
	 * @param path
	 * @return
	 */
	private static List<Point2D> nodes2Points(List<Node> path) {
		List<Point2D> listy =  new ArrayList<Point2D>();
		for(int i=0;i<path.size();i++) {
			listy.add(path.get(i).getPos());
		}
		return listy;
	}


	/**
	 * Converts a list of obstacles to a list of rectangles
	 * @param o
	 * @return
	 */
	public static List<Rectangle2D> Ob2Rec(List<Obstacle> o){
		List<Rectangle2D> rects = new ArrayList<Rectangle2D>();
		for(int i = 0; i < o.size(); i++){
			rects.add(o.get(i).getRect());
		}
		return rects;
	}
	
	/**
	 * Returns a list of lines, portraying the connections of the
	 * nodes made by PRMGraph
	 */
	public static List<Line2D> getNodeNetwork(List<Node> n){
		List<Line2D> lines = new ArrayList<Line2D>();
		Node tempNode; Edge tempEdge;
		for(int i = 0; i < n.size(); i++){
			tempNode = n.get(i); // make a temp Node
			for(int e = 0; e < n.get(i).getEdges().size(); e++){
				tempEdge = tempNode.getEdges().get(e); // Make a temp Edge
				//System.out.println(tempEdge.getLine().getX1()+", "+tempEdge.getLine().getY1());
				lines.add(tempEdge.getLine()); // Add the line made by the Edge to lines
			}
		}
		return lines;
	}
	
	/**
	 * Determines the angle of a straight line drawn between point one and two. 
	 * The number returned, which is a double in degrees, tells us how much we 
	 * have to rotate a horizontal line clockwise for it to match the line 
	 * between the two points. * If you prefer to deal with angles using radians 
	 * instead of degrees, 
	 * just change the last line to: "return Math.atan2(yDiff, xDiff);" 
	 * */ 
	public static double angleOf2Points(Point2D p1, Point2D p2) { 
		double xDiff = p2.getX() - p1.getX(); 
		double yDiff = p2.getY() - p1.getY(); 
		return (Math.atan2(yDiff, xDiff));
	}
	
	// Just to return the debugging points
	private static List<Point2D> debugPositions(){
		List<Point2D> positions = new ArrayList<Point2D>();
		positions.add(new Point2D.Double(0.1, 0.1));
//		positions.add(new Point2D.Double(0.2, 0.2));
//		positions.add(new Point2D.Double(0.4, 0.4));
//		positions.add(new Point2D.Double(0.3, 0.5));
//		positions.add(new Point2D.Double(0.7, 0.4));
//		positions.add(new Point2D.Double(0.6, 0.8));
//		positions.add(new Point2D.Double(0.9, 0.7));
//		positions.add(new Point2D.Double(0.5, 0.3));
		positions.add(new Point2D.Double(0.9, 0.1));
		return positions;
	}

//	List<Point2D> positions = new ArrayList<Point2D>();
//	positions.add(new Point2D.Double(0.1, 0.1));
//	positions.add(new Point2D.Double(0.9, 0.1));
//	Map<Point2D, Node> map = prm.addPoints(positions);
//	Node start = map.get(positions.get(0));
//	Node goal = map.get(positions.get(1));
	
	
	
}