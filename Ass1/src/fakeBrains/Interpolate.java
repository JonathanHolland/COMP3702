package fakeBrains;

import java.util.*;
import java.awt.geom.*;

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
	public List<ASVConfig> pathBetween(Node start, Node end,
			VisualHelper visualHelper) {

		List<ASVConfig> pathPiece = new ArrayList<ASVConfig>();
		// int i = start.getConfig().getASVCount();
		// int j = 0;
		// Map<Point2D, Point2D> asvStartEnd = new HashMap<Point2D, Point2D>();
		// Map<Integer, Double> primitiveSteps = new HashMap<Integer, Double>();

		// while (i > j) {
		// // Assign variables to positions
		// Point2D positionOne = start.getConfig().getPosition(j);
		// Point2D positionTwo = end.getConfig().getPosition(j);
		// // While there are still asv's to check, add the start and end
		// // positions of each one to a map
		// asvStartEnd.put(positionOne, positionTwo);
		// // Use the start and end positions to extract how many primitive
		// // steps each one needs to travel
		// primitiveSteps.put(j, positionOne.distance(positionTwo) / 0.001);
		// j++;
		// }

		ASVConfig stepPos;
		ASVConfig currentPos = start.getConfig();
		List<Point2D> cPos;
		boolean endReached = false;

		while (!endReached) {
			cPos = currentPos.getASVPositions();
			double[] sPos = new double[currentPos.getASVCount() * 2];
			int count = 0;
			double[] listxy;
			boolean odd;

			int cPossize = 0;
			// Make the for loop start from each end and move towards the center
			// To do this, check if odd or even asvCount first
			if (currentPos.getASVCount() % 2 == 1) {
				odd = true;
				cPossize = cPos.size() + 1;
			} else {
				odd = false;
				cPossize = cPos.size();
			}
			for (int k = 0; k < (cPossize / 2); k++) {
				// Only apply the +x+y movement equal to 0.001 for the asv's on
				// each end
				// i.e. only the first and last asv's
				if (k == 0) {
					// Apply the trajectory movement to the first asv
					listxy = xymove(cPos.get(k).getX(), cPos.get(k).getY(), end
							.getConfig().getPosition(k));
					sPos[count] = (cPos.get(k).getX() + listxy[0]); // add x
																	// movement
					sPos[count + 1] = (cPos.get(k).getY() + listxy[1]); // add y
																		// movement

					// Apply the trajectory movement to the last asv
					listxy = xymove(cPos.get(cPos.size() - 1).getX(),
							cPos.get(cPos.size() - 1).getY(), end.getConfig()
									.getPosition(cPos.size() - 1));
					sPos[sPos.length - (count + 2)] = (cPos
							.get(cPos.size() - 1).getX() + listxy[0]); // add x
																		// movement
					sPos[sPos.length - (count + 1)] = (cPos
							.get(cPos.size() - 1).getY() + listxy[1]); // add y
																		// movement
				} else {
					// Now we have applied the trajectory movements to both ends
					// of the asv chain,
					// drag the rest of the asv's by their new distances (in
					// order to contrain the boom length to 0.05

					// error isolated to sPos[count-2] and sPos[count-1]
					// equalling 0 on some accounts. This then causes the
					// difference calc in constrainBoom to give back the
					// original x and y values and then add them on
					listxy = constrainBoom(cPos.get(k).getX(), cPos.get(k)
							.getY(), sPos[count - 2], sPos[count - 1]);
					if (Double.isNaN(listxy[0])) {
						System.out.print("cPos.get(k)?:");
						System.out.print(cPos + "\n");
						System.out.println(k);
						endReached = true;
						break;
					}
					if (cPos.get(k).getX() > 10) {
						System.out.print("cPos.get(k)?:");
						System.out.print(cPos + "\n");
						System.out.println(k);
						endReached = true;
						break;
					}

					sPos[count] = (cPos.get(k).getX() + listxy[0]); // add x
																	// movement
					sPos[count + 1] = (cPos.get(k).getY() + listxy[1]); // add y
																		// movement

					// So as not to do the middle asv twice
					if (!((odd == true) && ((k + 1) > (cPos.size() / 2)))) {
						listxy = constrainBoom(
								cPos.get(cPos.size() - 1).getX(),
								cPos.get(cPos.size() - 1).getY(),
								sPos[cPos.size() - (count)], sPos[cPos.size()
										- (count - 1)]);
						sPos[sPos.length - (count + 2)] = (cPos.get(
								cPos.size() - 1).getX() + listxy[0]); // add x
																		// movement
						sPos[sPos.length - (count + 1)] = (cPos.get(
								cPos.size() - 1).getY() + listxy[1]); // add y
																		// movement
					}

				}
				count = count + 2;
			}
			stepPos = new ASVConfig(sPos);
			System.out.println(stepPos.getASVPositions());
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

			visualHelper.addLinkedPoints(stepPos.getASVPositions());

			if (pathPiece.size() > 2000) {
				visualHelper.repaint();
				break;
			}

			// Instead of if we reached the end we check if an asv is within a 1
			// move threshold. This will occur when the end two asv's reach
			// their positions but the center asv's have not
			// if (currentPos.equals(end.getConfig())) {
			// endReached = true;
			// }
			if (currentPos.getPosition(0).distance(
					end.getConfig().getPosition(0)) < 0.001) {
				// rotate the rest and keep this one here??
				endReached = true;

			}
		}
		return pathPiece;
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
	 * constrainBoom is very similar to xymove in that it calculates the
	 * movement in x and y for a single asv. However, in this case it does so
	 * via the movement of another asv and it's associated boom length
	 * constraint
	 * 
	 * @param y
	 *            - y position
	 * @param x
	 *            - x position
	 * @param xconn
	 * @param yconn
	 * @return resultxy - the x and y values to move by
	 */

	// Unsure if negative directions need to be taken into account here (or
	// whether the trig takes care of them itself)
	private double[] constrainBoom(double x, double y, double xconn,
			double yconn) {
		double[] resultxy = new double[2];
		boolean negx;
		boolean negy;
		double xmove = 0;
		double ymove = 0;
		double xdiff = 0;
		double ydiff = 0;
		double thetaa = 0;
		double distanceToMove = 0;
		double diagonalDistance = 0;
		
		// Given the newly moved point of the asv next to this one, we calculate
		// the distance between them and then subsequently move this point (x,y)
		// so that the boom length is the specified constrained value
		xdiff = Math.abs(xconn - x);
		ydiff = Math.abs(yconn - y);
		
		if (xdiff > 0) {
			negx = false;
		} else {
			negx = true;
			xdiff = Math.abs(xdiff);
		}
		if (ydiff > 0) {
			negy = false;
		} else {
			negy = true;
			ydiff = Math.abs(ydiff);
		}
		
		
		
		diagonalDistance = Math.sqrt(Math.pow(xdiff, 2)
				+ Math.pow(ydiff, 2));
		// The distance to move is the total diagonal distance minus the boom
		// length constraint (0.05)
		distanceToMove = diagonalDistance - 0.05;
		// Then, break distanceToMove into x and y positions using similar
		// triangles with the original xdiff,ydiff and diagonalDistance
		System.out.println("ConstrainBoomFunc");
		System.out.println(x);
		System.out.println(y);
		System.out.println(xconn);
		System.out.println(yconn);
		System.out.println(xdiff);
		System.out.println(ydiff);
		thetaa = Math.atan(ydiff / xdiff);
		System.out.println(thetaa);
		ymove = Math.sin(thetaa)*distanceToMove;
		xmove = Math.cos(thetaa)*distanceToMove;
		// System.out.println(ymove);
		// System.out.println(xmove);
		resultxy[0] = xmove;
		resultxy[1] = ymove;

		return resultxy;
	}

	/**
	 * Blends everything together. interpolate would work between nodes but this
	 * is meant to spit out the over-all solution
	 * 
	 * @return solution - every single ASVConfig moved through to reach the goal
	 */
	public List<ASVConfig> makeSolution(Node start, Node goal,
			VisualHelper visualHelper) {
		List<ASVConfig> solution = new ArrayList<ASVConfig>();

		int i = 1;
		Node current = path.get(i++);

		Node previous = start;
		while (previous != goal) {

			// Move between the 2
			solution.addAll(pathBetween(previous, current, visualHelper));

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

	private ASVConfig makeValidConfig() {

		return null;
	}

}
