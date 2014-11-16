package task1;

import java.io.IOException;
import java.util.*;
import java.math.*;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Solution {
	
	static final String file1 = "CPTnoMissingData-d1.txt";
	static final String file2 = "CPTnoMissingData-d2.txt";
	static final String file3 = "CPTnoMissingData-d3.txt";
	static final String file4 = "noMissingData-d1.txt";
	static final String file5 = "noMissingData-d2.txt";
	static final String file6 = "noMissingData-d3.txt";
	static final String file7 = "lectEx.txt";
	static final String file8 = "part-one-d1.txt";
	static final String file9 = "part-one-d2.txt";
	static final String file10 = "part-one-d3.txt";
	static final String file11 = "part-one-d4.txt";
	
	public List<Node> nodes = new ArrayList<Node>();
	public List<Node> tempNodes = new ArrayList<Node>();
	public List<Node> tempNodes2 = new ArrayList<Node>();
	
	public ArrayList<ArrayList<Integer>> dataset = new ArrayList<ArrayList<Integer>>();
	
	private List<Edge> tree;
	private boolean set = false;
	
	public static void main(String[] args) throws IOException {

		System.out.println("=========== TASK1 ===========");
		
		// EDIT ME!
		String USING_FILE = file7;
		// load the network
		File.read("data/" + USING_FILE);
		Solution s = new Solution(File.nodes, File.dataSets);
		for(Node n : s.nodes) { // print us the nodes?
			System.out.println(n);
		}
		System.out.println("\nCPTs: ");
		for(Node n : s.nodes) {
			s.findCPT(n, s.dataset);
			System.out.println(n.getIdentifier() + ": " + n.getValues());
		}
		System.out.println("Likelyhood: " + s.likelyhood(s.nodes, s.dataset));
		System.out.println("Log Likelyhood: " + s.log_likelihood(s.nodes, s.dataset));
		
		File.writeCPT(USING_FILE, s);
		
		System.out.println("\n=========== TASK4 ===========\n");
		
		// EDIT ME!
		USING_FILE = file4;
		
		// load the network
		s = File.task2_read("data/" + USING_FILE);
		for(Node n : s.nodes) { // print us the nodes?
			System.out.println(n);
		}
		// Run task 4
		// This point assumes nodes/datasets have been loaded from file
		// but not that parents or CPT's have been
		List<Node> task4result = s.findStructure();
		System.out.println(task4result);
		
	}
	
	public Solution(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
		this.nodes = nodes;
		this.dataset = dataset;
	}
	
	private void findCPT(Node n, ArrayList<ArrayList<Integer>> dataset) {
		double nodeVal, num, den;
		
		
		if(n.getParents().isEmpty()) { // if there's no parents just do this 
			num = count_in_data(n, 1, dataset) + 1; // the number of times this node is true
			n.setValue(null,  num/(dataset.size() + 1));
			return;
		}
		
		List<List<Boolean>> results = new ArrayList<>();
		Parents.recursivelyCombine(results, new ArrayList<Boolean>(), n.getParents().size(), 0);
		
		for(List<Boolean> bs : results) {
			nodeVal = num = den = 0;
			Parents p = new Parents(n.getParents(), bs);
			// Search for the case described by bs in dataset and then use that to add the setValue to the node
			num = count_in_data(n, p, dataset) + 1;
			den = count_in_data(p, dataset) + 1;
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
	private int count_in_data(Node n, int i, ArrayList<ArrayList<Integer>> dataset) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			if(set.get(n.getNodePos()) == i) {
				count++;
			}
		}
		return count;
	}
	
	private int count_in_data(Node n1, Node n2, List<Boolean> bs, ArrayList<ArrayList<Integer>> dataset) {
		int count = 0;
		for(ArrayList<Integer> set : dataset) {
			if(set.get(n1.getNodePos()) == (bs.get(0)?1:0) && set.get(n2.getNodePos()) == (bs.get(1)?1:0)) {
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
	private int count_in_data(Node n, Parents p, ArrayList<ArrayList<Integer>> dataset) {
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
	private int count_in_data(Parents p, ArrayList<ArrayList<Integer>> dataset) {
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
	private double likelyhood(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
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
	
	public double log_likelihood(List<Node> nodes, ArrayList<ArrayList<Integer>> dataset) {
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
		List<List<Boolean>> truthTable = new ArrayList<>();
		Parents.recursivelyCombine(truthTable, new ArrayList<Boolean>(), 2, 0);
		for(List<Boolean> bs : truthTable) {
			double tempMi = 0;
			tempMi += count_in_data(n1, n2, bs, dataset);
			
			double num = tempMi;
			double den = count_in_data(n1, (bs.get(0))?1:0, dataset) * count_in_data(n2, (bs.get(1))?1:0, dataset);
			
			tempMi *= Math.log(num/den);
			totalmi += tempMi;
		}
		
		return totalmi;
	}
	
	// function to create the tree with an edge between every vertex
	public List<Edge> getMaxEdges() {
		
		List<Edge> fullSpanning = new ArrayList<Edge>();
		// for every node
		for(int i=0; i<nodes.size(); i++) {
			// to every other node
			Node nodeOne =  nodes.get(i);
			for(int j=0; j<nodes.size(); j++) {
				// find the MI weighting between them and create an edge to represent this
				Node nodeTwo = nodes.get(j);
				if(!nodeTwo.equals(nodeOne)) {
					fullSpanning.add(new Edge(nodeOne,nodeTwo,getMIof(nodeOne,nodeTwo)));
				}
			}
		}
		
		// return the resultant max number of edges
		return fullSpanning;
		
	}

	public void minSpanTree() {
		List<Edge> minSpan = new ArrayList<Edge>();
		List<Edge> maxSpan = new ArrayList<Edge>();
		List<Node> nodesReached = new ArrayList<Node>();
		
		// if nodes 1 or less, return 
		if(nodes.size() <=1) {
			return;
		}
		
		maxSpan = getMaxEdges();
		Collections.sort(maxSpan);

		// if the number of edges added is nodeNum - 1, stop
		// as we have found the minimum spanning tree
		addEdge(minSpan,maxSpan,nodesReached);
	}
	
	private void addEdge(List<Edge> minSpan, List<Edge> maxSpan,
			List<Node> nodesReached) {
		// If finished recursive adding
		if(nodesReached.size()>=nodes.size()) {
			if(this.set==false) {
				setTree(minSpan);
			}
			return;
		}
		
		// maxSpan is sorted; extract lowest value
		Edge lowest =  maxSpan.get(0);
		if(nodesReached.contains(lowest.getStart()) && 
				nodesReached.contains(lowest.getEnd())) {
			// Skip this edge as it would be cyclic
			maxSpan.remove(lowest);
			addEdge(minSpan,maxSpan,nodesReached);
		}
		//if not, add minimum weighted edge
		else {
			maxSpan.remove(lowest);
			minSpan.add(lowest);
			// If a new node has been reached, add it
			if(!nodesReached.contains(lowest.getStart())) {
				nodesReached.add(lowest.getStart());
			}
			if(!nodesReached.contains(lowest.getEnd())) {
				nodesReached.add(lowest.getEnd());
			}
			addEdge(minSpan,maxSpan,nodesReached);
		}
	}

	// assuming minSpanTree has run and populated this.tree
	// we then pick the directionality of children and parents
	// and compare the resulting likelihood values (to maximise)
	public double generateDirection() {
		tempNodes = new ArrayList<Node>();
		Random r = new Random();
		
		// for every edge in the minimum spanning tree (this.tree)
		for(int i=0; i<tree.size();i++) {
			// Extract the nodes from the edges
			Node one = new Node(tree.get(i).getStart());
			Node two = new Node(tree.get(i).getEnd());
			// randomly make one of the nodes a parent and the other a child
		    if(r.nextInt(1)==1) {
		    	one.addParent(two);
		    } else {
		    	two.addParent(one);
		    }
		    if(!tempNodes.contains(one)) {
		    	tempNodes.add(one);
		    }
		    if(!tempNodes.contains(two)) {
		    	tempNodes.add(two);
		    }   
		}
		// calculate cpt for all of tempNodes
		for(Node n : tempNodes) {
			findCPT(n, dataset);
		}
		// Check the log-likelihood of tempNodes
		return log_likelihood(tempNodes, dataset);
		
	}
	
	// Sorts through a bunch of possibilities and finds the best structure
	public List<Node> findStructure() {
		minSpanTree();
		
		int i=0;
		double likely;
		double oldlikely = Double.MIN_VALUE*-1;
		
		// while less than threshold (3minutes time)
		while(i<10) {			
			// generateDirection(); compare likelihoods
			likely = generateDirection();
			if(likely>=oldlikely) {
				tempNodes2 = new ArrayList<Node>();
				// the better value is stored in tempNodes2
				for(Node n : tempNodes) {
					// put tempNodes into tempnodes2
					tempNodes2.add(new Node(n));
				}
			}
			oldlikely = likely;
			i++;
		}
		
		
		
		return tempNodes2;
	}
	public void setTree(List<Edge> minSpan) {
		//set minimum spanning tree to its class variable
		this.tree = minSpan;
		this.set = true;
	}
	
	public List<Edge> getTree() {
		return this.tree;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public ArrayList<ArrayList<Integer>> getDataset() {
		return dataset;
	}


}





