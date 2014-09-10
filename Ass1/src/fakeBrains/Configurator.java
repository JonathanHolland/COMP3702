package fakeBrains;

import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.lang.reflect.Array;
import java.util.*;

import fakeBrains.*;
import tester.Tester;
import problem.*;
import visualDebugger.VisualHelper;

public class Configurator {
	
	// The path through the map
	List<Node> p;
	
	// The obstacles
	List<Obstacle> o;
	
	// tester instance
	Tester test;
	
	// Visualiser instance
//	Visual
	
	/**
	 * Default Constructor
	 * Takes and sets up needed vars
	 * @param path
	 * @param obstacles
	 */
	public Configurator(List<Node> path, List<Obstacle> obstacles){
		this.p = path;
		this.o = obstacles;
		test = new Tester();
	}
	
	/**
	 * Spider over all the nodes in path and create valid ASVConfigs
	 * for each of them for Interpolate to use later
	 * @param path
	 * @return true if success
	 */
	public boolean giveConfigurations(){
		// Shortcut goal node
		Node goal = p.get(p.size()-1);
		
		// counter
		int i = 1;
		
		// can ignore the first node, as it is the starting node
		Node current;
		Node prev = p.get(0);
		
		// Time to Spider
		while(i < p.size()-1){
			current = p.get(i++);
			
			// Make a tentative config the same shape as the last node's
			ASVConfig t = new ASVConfig(current.getPos(), prev);
			
			// If there is no collision at this node then there's no worry :D
			// Just give current prev's shifted config
			if(!test.hasCollision(t, o) && test.fitsBounds(t)){
//				System.out.println("Translated");
				current.giveASVConfig(new ASVConfig(t));
				prev = current;
				continue;
			}
			
			// Now check if we could fit purely with rotation
			ASVConfig t2 = checkRotations(t, current.getPos());
			
			// if t2 != null we have a valid solution and can continue
			if(t2 != null) {
//				System.out.println("Rotated");
				current.giveASVConfig(new ASVConfig(t2));
				prev = current;
				continue;
			}
			
			/* If we're still here that means no matter how we rotate this
			 * config it's not fitting.
			 * Let's figure out which ASVs are causing the pain and shift
			 * them slightly
			 */
			
			/* SCREW THAT */
			/* LETS JUST RANDOMISE A CONFIG UNTIL ONE FITS */

			ASVConfig rCon = makeConfig(current);
			
			if (!test.hasValidBoomLengths(rCon)) System.out.println("Not valid boomlengths");
			
			if(!test.hasCollision(rCon, o) && test.fitsBounds(rCon)){
				System.out.println("Generated");
				current.giveASVConfig(new ASVConfig(rCon));
				prev = current;
				continue;
			}
			
			// Update things for the next round
			prev = current;
			
			if(prev.getConfig() == null) {
				return false;
			}
		}
		
		
		return true;
	}

	
	/**
	 * Generate a config that fits the area
	 * @param n
	 * @return
	 */
	private ASVConfig makeConfig(Node n) {
		// The previous node, for reference
		ASVConfig prev = new ASVConfig(n.getPrevious().getConfig());
		
		// The number of ASVs
		int num = prev.getASVCount();
		
		// the anchor Point
//		Point2D a = new Point2D.Double(n.getPos().x, n.getPos().y);
		
		// Generate a list of configs
		List<ASVConfig> possibleConfigs = new ArrayList<ASVConfig>(100);
		
		// Make the random object
		Random R = new Random();
		int count = 0;
		while(possibleConfigs.size() < 100) {
			count++;
			// Create the random points
			List<Point2D> t = new ArrayList<Point2D>();
			t.add(n.getPos());
			for(int i = 0; i < (num-1)/2; i++) {
				Point2D a = t.get(t.size()-1);
				Point2D tP = new Point2D.Double(a.getX()+0.05, a.getY());
				Interpolate.rotateAroundPoint(tP, 360*R.nextDouble(), a);
				t.add(tP);
			}
			for(int i = 0; i < num - 1 - num/2; i++) {
				Point2D a = t.get(0);
				Point2D tP = new Point2D.Double(a.getX()-0.05, a.getY());
				Interpolate.rotateAroundPoint(tP, 360*R.nextDouble(), a);
				t.add(0,tP);
			}
			
			ASVConfig tC = new ASVConfig(t);
			if(test.fitsBounds(tC) && !test.hasCollision(tC, o) && test.hasEnoughArea(tC) && test.isConvex(tC) && test.hasValidBoomLengths(tC)){ 
				possibleConfigs.add(tC);
			}
			
			if(count > 2000000 && possibleConfigs.size() > 20) {
				break;
			}
		}
		
		System.out.println("Made 100 Valid configs in: " + count + "\npicking best now");
		
		ASVConfig closest;
		closest = possibleConfigs.get(0);
		for(int i = 1; i < possibleConfigs.size(); i++) {
			double currentMinDistance = prev.maxDistance(closest);
			double newMaxDistance = prev.maxDistance(possibleConfigs.get(i));
			
			// If the distance is shorter, make closest it
			closest = (newMaxDistance < currentMinDistance)	? possibleConfigs.get(i) : closest;
		}
		
		return new ASVConfig(closest);
	}

	private ASVConfig flexConfig(Integer i, ASVConfig current) {
		for(int angle = 0; angle < 360; angle++) {
			List<Point2D> rawr = new ArrayList<Point2D>();

			// rotate the point around the points
			
			List<Point2D> points2rotate = current.getASVPositions().subList(i, current.getASVCount());
			System.out.println("\nrotating: \n" + points2rotate + "\naround: \n" + current.getPosition(i));
			
			rawr = pointsAroundPoint(points2rotate, angle, current.getPosition(i));
			
			// make a new config to from the points to test with
			ASVConfig t = new ASVConfig(rawr);
			
			if(!test.hasCollision(t, o) && test.fitsBounds(t)){
				System.out.println("Flexed");
				return new ASVConfig(t);
			} else {
				List<Integer> infringing = asvObstacleCheck(t);
				System.out.println("ASVsbeing blocked: " + infringing);
				for(int z = 0; z < infringing.size(); z++) 
					System.out.println(infringing.get(z)+ ": " + t.getASVPositions().get(infringing.get(z)));
				return flexConfig(i+1, current);
			}
		}
		return null;
	}
	
	public List<Integer> asvObstacleCheck(ASVConfig config) {
		VisualHelper v = new VisualHelper();
		v.addRectangles(Assignment1.Ob2Rec(o));
		
		// Make a list to return
		List<Integer> infringing = new ArrayList<Integer>();
		List<Point2D> ps = new ArrayList<Point2D>();
		// Loop through the asv's
		asvs: for(int k = 0; k < config.getASVCount(); k++) {
			
			// Loop through the obstacles
			for(int ob = 0; ob < o.size(); ob++) {
				
				// if an object contains the point add the index of the point to infringing
				// and then continue with the next asv
				if(o.get(ob).getRect().contains(config.getASVPositions().get(k))) {
					infringing.add(k);
					ps.add(config.getASVPositions().get(k));
					continue asvs;
					
				//  if not, if the mapsize doesn't contain the point then also make it infringing
				//  before continuing witht he next asv
				} else {
					Rectangle2D r = new Rectangle2D.Double(0, 1.0, 1.0, 1.0);
					if(!r.contains(config.getASVPositions().get(k))) {
						// If this is run then the point is out of bounds
						infringing.add(k);
						System.out.println("out of bounds: " + config.getASVPositions().get(k));
						continue asvs;
					}
				}
			}
		}
		
		v.addPoints(ps);
		v.addLinkedPoints(config.getASVPositions());
		v.repaint();
		// return the infringing indexes
		return new ArrayList<Integer>(infringing);
		
		
		
		
//		
//		VisualHelper rawr = new VisualHelper();
//		List<Rectangle2D> rl = new ArrayList<Rectangle2D>();
//		rl.add(o.get(ob).getRect());
//		rawr.addRectangles(rl);
//		List<Point2D> po = new ArrayList<Point2D>();
//		po.add(config.getASVPositions().get(k));
//		rawr.addPoints(po);
	}

	private ASVConfig checkRotations(ASVConfig prevConfig, Point2D center) {

		for(int angle = 0; angle < 360; angle++) {
			Point2D tempPosArray[] = new Point2D[prevConfig.getASVCount()];
			
			// Make the turn
			AffineTransform rawr = AffineTransform.getRotateInstance(Math.toRadians(angle), center.getX(), center.getY());
			rawr.transform(prevConfig.getASVPositions().toArray(new Point2D[0]), 0, tempPosArray, 0, prevConfig.getASVCount());
			
			// make a new config from the turned points
			ASVConfig t2 = new ASVConfig(Arrays.asList(tempPosArray));
			
			// Check if we still have a collision
			if(test.hasCollision(t2, o) || !test.fitsBounds(t2)) {
				// If we do, try the next angle
				continue;
				
			} else {
				// If we're now free return the free config
				return new ASVConfig(t2);
			}
		}
		return null;
	}
	
	public static List<Point2D> pointsAroundPoint(List<Point2D> points, double deg, Point2D center) {

		Point2D tempPosArray[] = new Point2D[points.size()];
		
		// Make the turn
		AffineTransform rawr = AffineTransform.getRotateInstance(Math.toRadians(deg), center.getX(), center.getY());
		rawr.transform(points.toArray(new Point2D[0]), 0, tempPosArray, 0, points.size());
		return new ArrayList<Point2D>(Arrays.asList(tempPosArray));
	}
	
}
