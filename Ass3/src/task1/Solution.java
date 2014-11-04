package task1;

import java.io.IOException;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Solution {
	
	static final String file1 = "data/CPTnoMissingData-d1.txt";
	static final String file2 = "data/CPTnoMissingData-d2.txt";
	static final String file3 = "data/CPTnoMissingData-d3.txt";
	
	List<Node> nodes = new ArrayList<Node>();
	ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) throws IOException {
		
		// load the network
		File.read(file1);
		Solution s = new Solution(File.nodes, File.dataSets);
		for(Node n : s.nodes) { // print us the nodes?
			System.out.println(n);
		}
		for(Node n : s.nodes) {
			s.findCPT(n);
		}
	}
	
	public Solution(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
		this.nodes = nodes;
		this.dataset = dataset;
	}
	
	private void findCPT(Node n) {
		double nodeVal = 0;
		
		double num = count_in_data(n, 1) + 1; // the number of times this node is true
		
		if(n.getParents().size() < 1) { // if there's no parents just do this 
			n.setValue(null,  num/(dataset.size()+1));
			return;
		}
		
		List<List<Boolean>> results = new ArrayList<>();
		Parents.recursivelyCombine(results, new ArrayList<Boolean>(), n.getParents().size(), 0);
		
		for(List<Boolean> bs : results) {
			// Search for the case described by bs in dataset and then use that to add the setValue to the node
			
			// add the value to the node
			n.setValue(new Parents(n.getParents(), bs), nodeVal);
		}
	}
	
	private int count_in_data(Node n, int i) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			if(set.get(n.getNodePos()) == i) {
				count++;
			}
		}
		return count;
	}
	
	

}
