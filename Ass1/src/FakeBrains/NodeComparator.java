package fakeBrains;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node first, Node second) {
	        if(first.getCost() < second.getCost()){
	            return -1;
	        }else if(first.getCost() > second.getCost()){
	            return 1;
	        }else{
	            return 0;
	        }
	}
}


