package task1;

import java.util.List;

public class Node {

	static int nodeIndex;

	// The string specified in the file
	private String identifier;
	// Store the parents to the current node
	private List<Node> parents;
	// Store the position of the node (for each dataSet)
	private int nodePos;

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
}
