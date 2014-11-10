package task1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
	
	public static void writeCPT(String trainingDataName, Solution s) throws IOException {
		BufferedWriter writer = null;
		
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream("solutions/cpt-"+trainingDataName), "utf-8"));
		    
		    for(int i=0; i< s.nodes.size(); i++) {
		    	// Write the identifier of this node first
		    	writer.write(s.nodes.get(i).getIdentifier());
		    	// Then add each parent
		    	int parentSize = s.nodes.get(i).getParents().size();
		    	for(int j=0; j<parentSize; j++) {
		    		writer.write(" "+ s.nodes.get(i).getParents().get(j).getIdentifier());
		    	}
		    	// New line
			    writer.newLine();
			    // Write the associated CPT
			    // for each parent again, take the values in ascending order
			   
			    // This bit isn't finished? huh?
			    List<Boolean> bs =  new ArrayList<Boolean>();
			    //Fill the list with false
			    for(int p=0; p<parentSize;p++) {
			    	bs.add(false);
			    }
			    while(bs.contains(false)) {
			    	writer.write(s.nodes.get(i).getValue(bs).toString()+" ");
			    	bs = fullAdder(bs, bs.size()-1);
			    }
			    // Run the final all true boolean list
			    writer.write(s.nodes.get(i).getValue(bs).toString()+" ");
			    
			    
			    writer.newLine();
		    }
		    
		    Double llh = s.log_likelyhood();
		    writer.write(llh.toString());
		    writer.newLine();
		    
		    
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	public static List<Boolean> fullAdder(List<Boolean> bs, int indexToCheck) {
		// if the last element is a false, make it true
		// if the last element is true, check the one before it again and again until it is false,
    	// then set it true  and all the others after it to false
    	// eg. from 0 1 1 to 1 0 0
		if(bs.get(indexToCheck)==false) {
    		bs.set(indexToCheck, true);
    		// set whatever was after it to false
    		int numAfter = (bs.size()-1)-indexToCheck;
    		while(numAfter!=0) {
    			bs.set(bs.size()-numAfter, false);
    			numAfter--;
    		}
    	} else {
    		fullAdder(bs, indexToCheck-1);
    	}
		
		return bs;
	}
}
