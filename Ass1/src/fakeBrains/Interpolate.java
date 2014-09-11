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

	public Interpolate(List<Obstacle> obstacles, List<Node> path) {
		this.obstacles = obstacles;
		this.path = path;
		test = new Tester();
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

		List<ASVConfig> pathPiece = new ArrayList<ASVConfig>();

		ASVConfig stepPos = null;
		ASVConfig currentPos = start.getConfig();
		List<Point2D> cPos;
		boolean endReached = false;
		
		int switcher = 0;
		while (!endReached) {
			cPos = currentPos.getASVPositions();
			List<Point2D> sPos = new ArrayList<Point2D>(currentPos.getASVCount());
			double[] listxy;
			
			switch (switcher%2) {
				case 0: // Translate
					listxy = xymove(cPos.get(0).getX(), cPos.get(0).getY(), end
							.getConfig().getPosition(0));
					for (int k = 0; k < (cPos.size()); k++) {
						double x = (cPos.get(k).getX() + listxy[0]); // add x movement
						double y = (cPos.get(k).getY() + listxy[1]); // add y
						sPos.add(new Point2D.Double(x,y));
					}
					stepPos = new ASVConfig(sPos);
					
					break;
					
				case 1: //Rotate
					sPos = rotmove(currentPos, end.getConfig());
					if(sPos == null){switcher++; continue;}
//					System.out.println(sPos);
					stepPos = new ASVConfig(sPos);
//					System.out.println(stepPos);
					break;
			}
			
			// Check is legit
			if(!test.hasValidBoomLengths(stepPos)){
				System.out.println("### BAD CONFIG ###");
				System.out.println(stepPos.getPosition(0).distance(stepPos.getPosition(1)));
				System.out.println(stepPos.getPosition(2).distance(stepPos.getPosition(1)));
			}
			if(test.hasCollision(stepPos, obstacles)) {
				System.out.println("### COLISION ###");
				System.out.println(Configurator.asvObstacleCheck(stepPos));
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

			pathPiece.add(stepPos);
			currentPos = stepPos;

			// This is not the ideal end pos check
//			System.out.println("END Config: " + end.getConfig());
//			System.out.println("CURRENT Config: " + currentPos);
			if (currentPos.getPosition(0).distance(end.getConfig().getPosition(0)) < 0.001) {
				// rotate the rest and keep this one here??
				endReached = true;

			}
			switcher++;
		}
		return pathPiece;
	}

	/**
	 * rot move returns 
	 * @param cPos
	 * @param config
	 * @return
	 */
	private List<Point2D> rotmove(ASVConfig cPos, ASVConfig goal) {
		// Point 1
		Point2D f = cPos.getPosition(0);
		
		// Check if we need to return
		double cAngle = Assignment1.angleOf2Points(cPos.getPosition(1), f);
		System.out.println("CurrentAngle: "+ cAngle);
		double gAngle = Assignment1.angleOf2Points(goal.getPosition(1), goal.getPosition(0));
		System.out.println("Goal Angle"+ gAngle);
		double angle2Turn = cAngle - gAngle;
		System.out.println("Trying to turn" + angle2Turn);
		
		// List to return
		List<Point2D> points = cPos.getASVPositions();
		
		// Find the point farthest from point 1
		for(int i = 1; i < cPos.getASVPositions().size(); i++) {
			double currentMaxDist = f.distance(cPos.getPosition(0));
			double newMaxDist = cPos.getPosition(0).distance(cPos.getPosition(i));
			f = (newMaxDist > currentMaxDist) ? cPos.getPosition(i) : f;
		}
		
		// Find the angle you can turn this at before it moves > 0.001
		double radius = f.distance(cPos.getPosition(0));
		double maxAngle = Math.atan(0.001 / radius);
		
		double angle;
		if(maxAngle >= angle2Turn){
			angle = angle2Turn;
		} else {
			angle = maxAngle;
		}
		
		System.out.println("#########Turning: " + angle);

		// Turn the points
		Point2D tempPosArray[] = new Point2D[cPos.getASVCount()];
		
		AffineTransform rawr = AffineTransform.getRotateInstance(
				-angle, cPos.getPosition(0).getX(), cPos.getPosition(0).getY());
		
		rawr.transform(points.toArray(new Point2D[0]), 0, tempPosArray, 0, points.size());
//		System.out.println("Array " + tempPosArray);
		return new ArrayList<Point2D>(Arrays.asList(tempPosArray));
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
