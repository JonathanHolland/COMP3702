package task1;

import java.util.*;

public class Edge implements Comparable<Edge>{
	private Node A;
	private Node B;
	private double MI;
		
	public Edge(Node one, Node two, double weight){
		this.A = one;
		this.B = two;
		this.MI = weight;
	}
	
	public Node getStart() {
		return this.A;
	}
	
	public Node getEnd() {
		return this.B;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		Edge e = (Edge) o; // the object to compare
		return (this.A.equals(e.A) && this.B.equals(e.B) && this.MI==e.MI);
	}


	@Override
	public int compareTo(Edge other) {
		/* Check how the costs compare. */
        if (MI < other.MI) return -1;
        if (MI > other.MI) return +1;
		return -1;

	}
}

