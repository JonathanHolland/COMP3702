package fakeBrains;

import java.util.*;
import java.awt.geom.*;
import java.awt.geom.Point2D.Double;

import tester.Tester;
import visualDebugger.VisualHelper;
import problem.*;

public class Interpolate {

	// The List of Obstacles
	private List<Obstacle> obstacles;

	// The Path found with Astar
	private List<Node> path;

	private Tester test;
	
	double err;

	public Interpolate(List<Obstacle> obstacles, List<Node> path) {
		this.obstacles = obstacles;
		this.path = path;
		test = new Tester();
		this.err = 0.000001;
	}

	/**
	 * Moves from one node asv configuration to another, changing the
	 * configuration shape as needed to meet the end result and to avoid
	 * obstacles it comes near
	 * 
	 * @param start
	 *            - the node to begin at
	 * @param end
	 *            - the node to end at
	 * @return the list of the states created in between start and end
	 */
	public List<ASVConfig> pathBetween(Node start, Node end) {
		System.out.println("_____________________________________\n__________________________________________");
		List<ASVConfig> pathPiece = new ArrayList<ASVConfig>();

		ASVConfig sConfig = null; // Step Config
		ASVConfig cConfig = start.getConfig(); // currentConfig
		List<Point2D> cPos; // Current Positions
		boolean endReached = false;
		
		int switcher = 0;
		while (!endReached) {
			cPos = cConfig.getASVPositions();
			List<Point2D> sPos = new ArrayList<Point2D>(cConfig.getASVCount()); // Step Positions

			
			switch (switcher%2) {
				case 0: // Translate
					sPos = xyMove(cPos, end);
					
					sConfig = new ASVConfig(sPos);
					
					break;
					
				case 1: //Rotate
					sPos = rotmove(cConfig, end);
					if(sPos == null){switcher++; continue;}
//					System.out.println(sPos);
					sConfig = new ASVConfig(sPos);
//					System.out.println(stepPos);
					break;
			}
			
			// Check is legit
			if(!test.hasValidBoomLengths(sConfig)){
				System.out.println("### BAD CONFIG ###");
				System.out.println(sConfig.getPosition(0).distance(sConfig.getPosition(1)));
				System.out.println(sConfig.getPosition(2).distance(sConfig.getPosition(1)));
			}
			if(test.hasCollision(sConfig, obstacles)) {
				System.out.println("### COLLISION ###");
				System.out.println(Configurator.asvObstacleCheck(sConfig));
			}
			
			// // Before adding the asv configuration, check if it hit any
			// // obstacles
			// boolean collide = test.hasCollision(stepPos, obstacles);
			//
			// if (collide) {
			// // find out which obstacle collided and change its move
			// // accordingly
			// } else {
			// // Add position config to ASVconfig list to return
			// pathPiece.add(stepPos);
			// }

			pathPiece.add(sConfig);
			cConfig = sConfig;

			// This is not the ideal end pos check
			endReached = true;
			for(int i = 0; i < start.getConfig().getASVCount(); i++) {
				if(cConfig.getPosition(i) != end.getConfig().getPosition(i)) {
					endReached = false;
					System.out.println(cConfig.getPosition(i));
					System.out.println(end.getConfig().getPosition(i));
					break;
				}
			}
			switcher++;
		}
		return pathPiece;
	}

	/**
	 * rot move returns 
	 * @param cConfig
	 * @param config
	 * @return
	 */
	private List<Point2D> rotmove(ASVConfig cConfig, Node goalN) {
		// Get config from goalN for shortcutting
		ASVConfig goal = goalN.getConfig();
		
		if(cConfig.maxDistance(goal) <= err) return goal.getASVPositions();
		// A subset of cConfig's Positions
		List<Point2D> _cPos = new ArrayList<Point2D>();
		
		// Figure out which link we're up to
		double cAngle=0, gAngle=0, angle2Turn;
		int j, m = cConfig.getASVCount()-1;
		for(j = 1; j < cConfig.getASVCount(); j++) {
			cAngle = Assignment1.angleOf2Points(cConfig.getPosition(j), cConfig.getPosition(j-1));
			System.out.println("CurrentAngle: "+ Math.toDegrees(cAngle));
			
			gAngle = Assignment1.angleOf2Points(goal.getPosition(j), goal.getPosition(j-1));
			System.out.println("Goal Angle: "+ Math.toDegrees(gAngle));

			// if this angle is close, proceed to next points
			if(cAngle - gAngle <= err) {
				System.out.println("link " + (j-1) + " to " + j + " is good.");
				if(j == m) {System.out.println("Moving instead");return xyMove(cConfig.getASVPositions(), goalN);}
				continue; // This angle is all good :D
			}
			System.out.println((cAngle - gAngle) + "is < " + err);
			break;
		}
		
		// Make a list of points that still need to be altered
		_cPos = cConfig.getASVPositions().subList(j-1, cConfig.getASVCount()-1); 

		// The Angle we'd need to turn to reach the goal angle
		angle2Turn = cAngle - gAngle;
		System.out.println("Trying to turn: " + Math.toDegrees(angle2Turn));
	
		// f is the furthest point from _cPos.get(0)
		Point2D f = _cPos.get(0);
		
		// Find the point farthest from point 1
		for(int i = 1; i < _cPos.size(); i++) {
			double currentMaxDist = f.distance(_cPos.get(0));
			double newMaxDist = _cPos.get(0).distance(_cPos.get(i));
			f = (newMaxDist > currentMaxDist) ? _cPos.get(i) : f;
		}
		
		// Find the angle you can turn this at before it moves > 0.001
		double radius = f.distance(_cPos.get(0));
		double maxAngle = Math.atan(0.001 / radius);
		
		// The amount we will actually turn
		double angle;

		// Check how much we need to turn
		if(Math.abs(maxAngle) >= Math.abs(angle2Turn)){
			angle = angle2Turn;
		} else {
			angle = Math.signum(angle2Turn) * maxAngle;
		}
		
		// Make sure to take the shortest route
		if(Math.abs(angle2Turn) > 180) {
			angle = angle * -1;
		}
		
		System.out.println("##Turning: " + Math.toDegrees(angle));

		// Affine Transformation to turn about the current point 
		AffineTransform rawr = AffineTransform.getRotateInstance(
					-angle, _cPos.get(0).getX(), _cPos.get(0).getY());

		// Array to hold the turned points
		Point2D tempPosArray[] = new Point2D[_cPos.size()];
		
		rawr.transform(_cPos.toArray(new Point2D[0]), 0, tempPosArray, 0, _cPos.size());
//		System.out.println("Array " + tempPosArray);
		return new ArrayList<Point2D>(Arrays.asList(tempPosArray));
	}

	
	/**
	 * xymoveWrapper
	 */
	private List<Point2D> xyMove(List<Point2D> cPos, Node endPos) {
		double[] listxy; List<Point2D> sPos = new ArrayList<Point2D>();
		
		listxy = xymove(cPos.get(0).getX(), cPos.get(0).getY(), endPos
				.getConfig().getPosition(0));
		for (int k = 0; k < (cPos.size()); k++) {
			double x = (cPos.get(k).getX() + listxy[0]); // add x movement
			double y = (cPos.get(k).getY() + listxy[1]); // add y
			sPos.add(new Point2D.Double(x,y));
		}
		return sPos;
	}
	/**
	 * xymove calculates the required x and y distances to move the asv in order
	 * to not exceed 0.001 and to do so in the direction of the goal
	 * 
	 * @param y
	 *            - y position
	 * @param x
	 *            - x position
	 * @param endpos
	 *            - end position to compare to
	 * @return resultxy - the x and y values to move by
	 */
	private double[] xymove(double x, double y, Point2D endpos) {
		double[] resultxy = new double[2];
		boolean negx = false;
		boolean negy = false;
		double xmove;
		double ymove;

		// Compare the current x and y to the end state
		// We need to do this every state we move just in case
		// we had to override for obstacles
		double resx = (endpos.getX() - x);
		double resy = (endpos.getY() - y);
		 //Check direction and take absolute for trig
		 if (resx > 0) {
			 negx = false;
		 } else {
			 negx = true;
			 resx = Math.abs(resx);
		 }
		 if (resy > 0) {
			 negy = false;
		 } else {
			 negy = true;
			 resy = Math.abs(resy);
		 }

		// Use similar triangles to take angles for direction of move
		double thetaa = Math.atan(resy / resx);
		double thetab = (0.5 * Math.PI) - thetaa;

		// Use these angles in the sine rule subbed into pythagoras to find x
		// and y distances with a hypotenuse of 0.001
		double signratio = Math.sin(thetab) / Math.sin(thetaa);
		xmove = (signratio)
				/ (Math.sqrt(1000000 * Math.pow(signratio, 2) + 1000000));
		ymove = (Math.sin(thetaa) * xmove) / Math.sin(thetab);

		if (negx) {
			resultxy[0] = -xmove;
		} else {
			resultxy[0] = xmove;
		}
		if (negy) {
			resultxy[1] = -ymove;
		} else {
			resultxy[1] = ymove;
		}

		return resultxy;
	}


	/**
	 * Blends everything together. interpolate would work between nodes but this
	 * is meant to spit out the over-all solution
	 * 
	 * @return solution - every single ASVConfig moved through to reach the goal
	 */
	public List<ASVConfig> makeSolution(Node start, Node goal) {
		List<ASVConfig> solution = new ArrayList<ASVConfig>();

		int i = 1;
		Node current = path.get(i++);

		Node previous = start;
		while (previous != goal) {

			// Move between the 2
			solution.addAll(pathBetween(previous, current));

			// Update things for the next round
			previous = current;
			try {
				current = path.get(i++);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			// Because it's not reaching the goal - infinite loop
		}

		return solution;
	}

	public static Point2D rotateAroundPoint(Point2D point, double deg, Point2D centerPoint) {
		AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(deg), centerPoint.getX(), centerPoint.getY());
		return t.transform(point,point);
	}

}
