package fakeBrains;

import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.util.*;

import problem.*;
import visualDebugger.VisualHelper;

public class Assignment1 {

	public static void main(String[] args) throws NoSuchNodeException {
		ProblemSpec problem = new ProblemSpec();
		try {
			problem.loadProblem("testcases/3ASV-noOb");
		} catch (Exception x) {
			System.out.println("The file failed to load. Make sure it was legit");
		}
		// Starting and ending points, these will be different for the final
		
		
		List<Point2D> positions = new ArrayList<Point2D>();
		positions.add(new Point2D.Double(0.1, 0.1));
		positions.add(new Point2D.Double(0.2, 0.2));
		positions.add(new Point2D.Double(0.4, 0.4));
		positions.add(new Point2D.Double(0.3, 0.5));
		positions.add(new Point2D.Double(0.7, 0.4));
		positions.add(new Point2D.Double(0.6, 0.8));
		positions.add(new Point2D.Double(0.9, 0.7));
		positions.add(new Point2D.Double(0.5, 0.3));
		positions.add(new Point2D.Double(0.9, 0.9));
		
		PRMGraph prm = new PRMGraph(problem.getObstacles(), 0.5);
		
		Map<Point2D, Node> nodes = prm.addPoints(positions);
		
		VisualHelper visualHelper = new VisualHelper();
//		visualHelper.addPoints(prm.getPoints());
		
//		visualHelper.addLines(getNodeNetwork(prm.getNodes()));
//		visualHelper.addRectangles(Ob2Rec(problem.getObstacles()));
//		visualHelper.repaint();
		
		Astar alg = new Astar();
		System.out.println(nodes);
		System.out.println(positions);
		Node start = nodes.get(positions.get(0));
		System.out.println(start);
		Node end = nodes.get(positions.get(positions.size()-1));
		System.out.println(end);
		
		for(int i = 0; i < positions.size(); i++) {
			System.out.println(nodes.get(positions.get(i)).getEdges());
		}
		
		
		List<Node> path = alg.findPath(start, end);
		List<Point2D> listy =  new ArrayList<Point2D>();
		for(int i=0;i<path.size();i++) {
			listy.add(path.get(i).getPos());
			System.out.println(path.get(i).getPos());
		}
		visualHelper.addPoints(listy);
//		visualHelper.addPoints(positions);
//		visualHelper.addLines(getNodeNetwork(prm.getNodes()));
		
		visualHelper.repaint();
		
	}
	
	
	private static List<Rectangle2D> Ob2Rec(List<Obstacle> o){
		List<Rectangle2D> rects = new ArrayList<Rectangle2D>();
		for(int i = 0; i < o.size(); i++){
			rects.add(o.get(i).getRect());
		}
		return rects;
	}
	
	private static List<Line2D> getNodeNetwork(List<Node> n){
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
}