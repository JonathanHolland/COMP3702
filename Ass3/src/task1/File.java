package task1;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class File {

	private final static String EMPTY_LINE = "";
	static int nodeNumber;
	static int dataNumber;

	// The nodes list contains Node types that contain references to parents,
	// and to their list of dataSets
	public static List<Node> nodes = new ArrayList<Node>();
	
	// The list of lists that corresponds to the dataSets being read
	public static ArrayList<ArrayList<Integer>> dataSets = new ArrayList<ArrayList<Integer>>();

	/**
	 * The training data sets for this part are the .txt files whose names start
	 * with CPTNoMissingData. The format of each data file is:
	 * 
	 * The first line contains two numbers separated by a white space. The first
	 * number is the number of nodes in the Bayesian Network. The second number
	 * is the number of data (lines?) in the file. Let’s denote the number of
	 * nodes as K and the number of data as N. • Each line at line 2 to K+1
	 * represents a node of the Bayesian Network and its parents. Each line
	 * contains one or more words, separated by a white space. The first word
	 * represents the name of the node, while the rest represents the names of
	 * the node’s parents. For example, A B C means Node B and C are the parents
	 * of node A. • Each line at line K+2 to K+N+1 represents the data, in the
	 * same order the nodes are written. For instance, in
	 * CPTNoMissingData8d1.txt, each line of data represents the value of A B C.
	 * 
	 * @param fileName
	 *            the file to read the Bayesian Network from
	 * @return variables related to the Network (such as blabla)
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 */

	public static void read(String fileName) throws IOException {

		nodeNumber = 0;
		dataNumber = 0;
		String nodeID;
		int posCount = 0;

		FileReader fr = new FileReader(fileName); // the file to read from
		Scanner in = new Scanner(fr); // scanner for reading the file
		int lineNumber = 0; // the number of the line being read

		// A mapping for parents of nodes so that parents can be declared
		// as parents before they are declared as nodes
		Map<Node, List<String>> parentMap = new HashMap<Node, List<String>>();
		List<String> nodeParents; 
		
		while (in.hasNextLine()) {
			String line = in.nextLine(); // read in the file line-by-line
			// If line is null or equal to empty string representation, break
			// loop
			if (line == null || EMPTY_LINE.equals(line)) {
				break;
			}

			if (lineNumber == 0) {
				String[] parts = line.split(" ");
				nodeNumber = Integer.parseInt(parts[0]);
				dataNumber = Integer.parseInt(parts[1]);
			}

			
			else if (lineNumber < (nodeNumber + 1)) {
				String[] parts = line.split(" ");
				// Reset nodeParents to empty
				nodeParents = new ArrayList<String>();
				nodeID = parts[0];
				
				for (int i = 1; i < parts.length; i++) {
					nodeParents.add(parts[i]);
				}
				
				// Create the node
				Node n = new Node(nodeID, posCount);
				parentMap.put(n, nodeParents);
				nodes.add(n);
				posCount++;
			}

			else {
				String[] parts = line.split(" ");
				ArrayList<Integer> dataSet = new ArrayList<Integer>();
				for (int i = 0; i < parts.length; i++) {
					dataSet.add(Integer.parseInt(parts[i]));
				}
				dataSets.add(dataSet);
			}
			lineNumber++;
		}
		List<String> parents = new ArrayList<String>();
		Node n;
		for (Map.Entry<Node, List<String>> entry : parentMap.entrySet())
		{
			n = entry.getKey(); 
			List<Node> parentNodes =  new ArrayList<Node>();
		    parents = entry.getValue();
		    for(int i=0; i<parents.size(); i++) {
		    	for(int j=0; j<nodes.size(); j++) {
		    		if(parents.get(i).equals(nodes.get(j).getIdentifier())) {
		    			parentNodes.add(nodes.get(j));
		    		}
		    	}
		    	
		    }
		    //System.out.println(parentNodes);
		    n.setParents(parentNodes);
		}
		in.close();
	}

	public static Node getNode(String id) {
		for (int i = 0; i < nodes.size(); i++) {
			if (id.equals(nodes.get(i).getIdentifier())) {
				return nodes.get(i);
			}
		}
		System.out.println("#############################");
		return null;
	}
	
	public void writeCPT() {
		
	}
}
