package task1;

import java.io.IOException;
import java.util.*;
import java.math.*;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Solution {
	
	static final String file1 = "CPTnoMissingData-d1.txt";
	static final String file2 = "CPTnoMissingData-d2.txt";
	static final String file3 = "CPTnoMissingData-d3.txt";
	static final String file4 = "lectEx.txt";
	static final String file5 = "part-one-d1.txt";
	static final String file6 = "part-one-d2.txt";
	static final String file7 = "part-one-d3.txt";
	static final String file8 = "part-one-d4.txt";
	
	public List<Node> nodes = new ArrayList<Node>();

	public ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) throws IOException {
		
		// EDIT ME!
		String USING_FILE = file4;
		
		// load the network
		File.read("data/" + USING_FILE);
		Solution s = new Solution(File.nodes, File.dataSets);
		for(Node n : s.nodes) { // print us the nodes?
			System.out.println(n);
		}
		System.out.println("\nCPTs: ");
		for(Node n : s.nodes) {
			s.findCPT(n);
			System.out.println(n.getIdentifier() + ": " + n.getValues());
		}
		System.out.println("Likelyhood: " + s.likelyhood());
		System.out.println("Log Likelyhood: " + s.log_likelyhood());
		
		File.writeCPT(USING_FILE, s);
	}
	
	public Solution(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
		this.nodes = nodes;
		this.dataset = dataset;
	}
	
	private void findCPT(Node n) {
		double nodeVal, num, den;
		
		
		if(n.getParents().isEmpty()) { // if there's no parents just do this 
			num = count_in_data(n, 1) + 1; // the number of times this node is true
			n.setValue(null,  num/(dataset.size() + 1));
			return;
		}
		
		List<List<Boolean>> results = new ArrayList<>();
		Parents.recursivelyCombine(results, new ArrayList<Boolean>(), n.getParents().size(), 0);
		
		for(List<Boolean> bs : results) {
			nodeVal = num = den = 0;
			Parents p = new Parents(n.getParents(), bs);
			// Search for the case described by bs in dataset and then use that to add the setValue to the node
			num = count_in_data(n, p) + 1;
			den = count_in_data(p) + 1;
			nodeVal = num/den;
			
			// add the value to the node
			n.setValue(p, nodeVal);
		}
	}
	
	/**
	 * Returns the number of times n has value i in the dataset
	 * @param n
	 * @param i
	 * @return
	 */
	private int count_in_data(Node n, int i) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			if(set.get(n.getNodePos()) == i) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * returns the number of time n is true and p matches with the dataset.
	 * @param n
	 * @param p
	 * @return count
	 */
	private int count_in_data(Node n, Parents p) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			if(set.get(n.getNodePos()) == 1) {
				int parentmismatch = 0;
				for(int i = 0; i < p.nodes().size(); i++) {
					Node t = p.nodes().get(i);
					int nodeBool = p.getVal(t) ? 1 : 0;
					// if the parent's needed value (nodeBool) isn't the same as the value in the data
					// inc parentmismatch.
					if(nodeBool != set.get(t.getNodePos())) parentmismatch++;
				}
				if(parentmismatch == 0) { // if there were no parent mismatches count this set
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Counts the number of times the pattern of p exists in the dataset
	 * @param n
	 * @param p
	 * @return
	 */
	private int count_in_data(Parents p) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			int parentmismatch = 0;
			for(int i = 0; i < p.nodes().size(); i++) {
				int nodeBool = p.getVal(p.nodes().get(i)) ? 1 : 0;
				// if the parent's needed value (nodeBool) isn't the same as the value in the data
				// inc parentmismatch.
				if(nodeBool != set.get(p.nodes().get(i).getNodePos())) parentmismatch++;
			}
			if(parentmismatch == 0) { // if there were no parent mismatches count this set
				count++;
			}
		}
		return count;
	}
	
	/**
	 * For each node product the value of their dataset's value, 
	 * @return
	 */
	private double likelyhood() {
		List<Double> setVals = new ArrayList<Double>();
		for(ArrayList<Integer> set : dataset) {
			List<Double> nodeVals = new ArrayList<Double>();
			for(Node n : nodes) {
//				System.out.println("NODE: - " + n.getIdentifier());
				// find the probability of the parents of this node matching the dataset
				double tempVal = 0;
				List<Node> parents = n.getParents(); // summon 'rents
				if(parents.isEmpty()) { // do we have parents?
					// if we don't then our value is at the null key
					if(set.get(n.getNodePos()) == 1) nodeVals.add(n.getValue(null));
					if(set.get(n.getNodePos()) == 0) nodeVals.add(1 - n.getValue(null)); // negate the value if the set has a false value
//					System.out.println("We got this val: " + nodeVals.get(nodeVals.size()-1));
					continue;
				}
//				System.out.println("We have Parent's see: " + n.getParents());
				List<Boolean> valOfParentsinSet = new ArrayList<Boolean>();
				for(Node pNode : parents) { // figure out what values our parents should have
					if(set.get(pNode.getNodePos()) == 1) valOfParentsinSet.add(true);
					else if(set.get(pNode.getNodePos()) == 0) valOfParentsinSet.add(false);
				}
//				System.out.println("Val of rents" + valOfParentsinSet);
				
				// add the expected value of us given our parents in the data set and negate it if necessary
				if(set.get(n.getNodePos()) == 1) nodeVals.add(n.getValue(valOfParentsinSet));
				if(set.get(n.getNodePos()) == 0) nodeVals.add(1 - n.getValue(valOfParentsinSet));
				
//				System.out.println("We got this val: " + nodeVals.get(nodeVals.size()-1));
			}
			// Total all the node's values
			double totalForSet = 1;
//			System.out.println("NodeVals: " + nodeVals);
			for(Double d : nodeVals) {
				totalForSet = totalForSet * d;
			}
			setVals.add(totalForSet);
		}
		// total all the dataset's values
//		System.out.println("Each Set's Values: " + setVals);
		double total = 1;
		for(Double d : setVals) {
			total = total * d;
		}
		return total;
	}
	
	public double log_likelyhood() {
		List<Double> setVals = new ArrayList<Double>();
		for(ArrayList<Integer> set : dataset) {
			List<Double> nodeVals = new ArrayList<Double>();
			for(Node n : nodes) {
//				System.out.println("NODE: - " + n.getIdentifier());
				// find the probability of the parents of this node matching the dataset
				double tempVal = 0;
				List<Node> parents = n.getParents(); // summon 'rents
				if(parents.isEmpty()) { // do we have parents?
					// if we don't then our value is at the null key
					if(set.get(n.getNodePos()) == 1) nodeVals.add(n.getValue(null));
					if(set.get(n.getNodePos()) == 0) nodeVals.add(1 - n.getValue(null)); // negate the value if the set has a false value
//					System.out.println("We got this val: " + nodeVals.get(nodeVals.size()-1));
					continue;
				}
//				System.out.println("We have Parent's see: " + n.getParents());
				List<Boolean> valOfParentsinSet = new ArrayList<Boolean>();
				for(Node pNode : parents) { // figure out what values our parents should have
					if(set.get(pNode.getNodePos()) == 1) valOfParentsinSet.add(true);
					else if(set.get(pNode.getNodePos()) == 0) valOfParentsinSet.add(false);
				}
//				System.out.println("Val of rents" + valOfParentsinSet);
				
				// add the expected value of us given our parents in the data set and negate it if necessary
				if(set.get(n.getNodePos()) == 1) nodeVals.add(n.getValue(valOfParentsinSet));
				if(set.get(n.getNodePos()) == 0) nodeVals.add(1 - n.getValue(valOfParentsinSet));
				
//				System.out.println("We got this val: " + nodeVals.get(nodeVals.size()-1));
			}
			// Total all the node's values
			double totalForSet = 0;
//			System.out.println("NodeVals: " + nodeVals);
			for(Double d : nodeVals) {
				totalForSet = totalForSet + Math.log(d);
			}
			setVals.add(totalForSet);
		}
		// total all the dataset's values
//		System.out.println("Each Set's Values: " + setVals);
		double total = 0;
		for(Double d : setVals) {
			total = total + d;
		}
		return total;
	}
	
	private List<Boolean> build_list_from_set(List<Integer> set) {
		List<Boolean> t = new ArrayList<Boolean>();
		for(Integer i : set) {
			if(i == 0) t.add(false);
			else if(i == 1) t.add(true);
		}
		return t;
	}
	
	public double getMIof(Node n1, Node n2) {
		double totalmi = 0;
		for(ArrayList<Integer> set : dataset) {
			List<Boolean> bs = new ArrayList<Boolean>(build_list_from_set(set));
			
		}
		return 0.0;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}

	public ArrayList<ArrayList<Integer>> getDataset() {
		return dataset;
	}


}





