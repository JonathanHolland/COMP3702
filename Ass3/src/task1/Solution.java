package task1;

import java.io.IOException;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Solution {
	
	static final String file1 = "data/CPTnoMissingData-d1.txt";
	static final String file2 = "data/CPTnoMissingData-d2.txt";
	static final String file3 = "data/CPTnoMissingData-d3.txt";
	static final String file4 = "data/lectEx.txt";
	
	List<Node> nodes = new ArrayList<Node>();
	ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) throws IOException {
		
		// load the network
		File.read(file3);
		Solution s = new Solution(File.nodes, File.dataSets);
		for(Node n : s.nodes) { // print us the nodes?
			System.out.println(n);
		}
		System.out.println("\nCPTs: ");
		for(Node n : s.nodes) {
			s.findCPT(n);
			System.out.println(n.getIdentifier() + ": " + n.getValue());
		}
//		System.out.println("Log Likelyhood: " + log_likely());
	}
	
	public Solution(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
		this.nodes = nodes;
		this.dataset = dataset;
	}
	
	private void findCPT(Node n) {
		double nodeVal, num, den;
		
		
		if(n.getParents().size() < 1) { // if there's no parents just do this 
			num = count_in_data(n, 1); // the number of times this node is true
			n.setValue(null,  num/(dataset.size()));
			return;
		}
		
		List<List<Boolean>> results = new ArrayList<>();
		Parents.recursivelyCombine(results, new ArrayList<Boolean>(), n.getParents().size(), 0);
		
		for(List<Boolean> bs : results) {
			nodeVal = num = den = 0;
			Parents p = new Parents(n.getParents(), bs);
			// Search for the case described by bs in dataset and then use that to add the setValue to the node
			num = count_in_data(n, p);
			den = count_in_data(p);
//			System.out.println("For: " + p);
//			System.out.println(" num         " + num);
//			System.out.println("-----       ------");
//			System.out.println(" den         " + den + "\n");
			nodeVal = num/den;
//			System.out.println("Giving: " + nodeVal);
			
			// add the value to the node
			n.setValue(new Parents(n.getParents(), bs), nodeVal);
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
	
	private double log_likely() {
		
		return (Double) null;
	}
	

}





