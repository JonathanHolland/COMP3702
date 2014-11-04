package task1;

import java.util.*;

public class Parents {

	private Map<Node, Boolean> m;
	
	/**
	 * Should be used as: Node, Boolean, Node, Boolean, Node, Boolean etc
	 * @param args
	 * @throws Exception
	 */
	public Parents(Node n, Boolean b){
		m = new HashMap<Node, Boolean>();
		m.put(n, b);
	}
	
	public Parents(List<Node> ns, List<Boolean> bs){
		m = new HashMap<Node, Boolean>();
		for(int i = 0; i < ns.size(); i++) {
			m.put(ns.get(i), bs.get(i));
		}
	}	
	
	public Parents(Parents p) {
		this.m = new HashMap<Node, Boolean>(m);
	}
	
	/**
	 * @requires ns and bs to be the same length
	 * @param ns
	 * @param bs
	 */
	public void add(List<Node> ns, List<Boolean> bs) {
		for(int i = 0; i < ns.size(); i++) {
			m.put(ns.get(i), bs.get(i));
		}
	}
	
	public void add(Node n, Boolean b) {
		m.put(n, b);
	}
	
	public Map<Node, Boolean> get() {
		return new HashMap<Node, Boolean>(m);
	}
	
	public List<Node> nodes() {
		return new ArrayList<Node>(m.keySet());
	}
	
	public Boolean getVal(Node n) {
		return m.get(n);
	}
	
	public static List<Parents> permutations(int size) {
		
		return null;
	}
	
	private static List<Parents> recursive_perm(List<Parents> p, int pos) {
		return null;
	}
	
	static void recursivelyCombine(List<List<Boolean>> result, List<Boolean> current, int size, int index) {
	    if (index == size) {
	        result.add(current);
	        return;
	    }
	    ArrayList<Boolean> temp1 = new ArrayList<Boolean>(current);
	    temp1.add(Boolean.FALSE);
        recursivelyCombine(result, temp1, size, index+1);
        
	    ArrayList<Boolean> temp2 = new ArrayList<Boolean>(current);
	    temp2.add(Boolean.TRUE);
        recursivelyCombine(result, temp2, size, index+1);
	}
	
//	static void recursivelyCombine(List<List<Boolean>> result, List<Boolean> current, List<Boolean> in1, List<Boolean> in2, int index) {
//	    if (index == in1.size()) {
//	        result.add(current);
//	    } else {
//	        if (in1.get(index).equals(in2.get(index))) {
//	           current.add(in1.get(index));
//	           recursivelyCombine(result, current, in1, in2, index+1);
//	        } else {
//	           List<Boolean> temp = new ArrayList<>(current);
//	           temp.add(Boolean.TRUE);
//	           recursivelyCombine(result, temp, in1, in2, index+1);
//
//	           temp = new ArrayList<>(current);
//	           temp.add(Boolean.FALSE);
//	           recursivelyCombine(result, temp, in1, in2, index+1);
//	        }
//	    }
//	}
	
	
	
	
	
	
	
	
	
	
}