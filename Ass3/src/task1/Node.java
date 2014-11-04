package task1;

import java.util.*;

public class Node {

	static int nodeIndex;

	// The string specified in the file
	private String identifier;
	// Store the parents to the current node
	private List<Node> parents;
	// Store the position of the node (for each dataSet)
	private int nodePos;
	// CPT values
	private Map<Parents, Double> cpt = new HashMap<Parents, Double>();

	public Node(String id, List<Node> parents, int nodePos) {
		setIdentifier(id);
		setParents(parents);
		setNodePos(nodePos);
	}

	// For use in part 2
	public Node(String id, int nodePos) {
		setIdentifier(id);
		setParents(null);
		setNodePos(nodePos);
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<Node> getParents() {
		return parents;
	}

	public void setParents(List<Node> parents) {
		this.parents = parents;
	}

	public int getNodePos() {
		return nodePos;
	}

	public void setNodePos(int nodePos) {
		this.nodePos = nodePos;
	}

	public Map<Parents, Double> getValue() {
		return cpt;
	}

	public void setValue(Parents p, double value) {
		cpt.put(p, value);
	}
	
	@Override
	public String toString() {
		String s = identifier + ": ";
		if(parents.contains(null)) return s + parents;
		for(int i = 0; i < parents.size(); i++) {
			s += (parents.get(i).identifier + ", ");
		}
		return s;
	}
}
