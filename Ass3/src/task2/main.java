package task2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import task1.Node;

public class main {
	private final static String EMPTY_LINE = "";

	static final String file1 = "noMissingData-d1.txt";
	static final String file2 = "noMissingData-d2.txt";
	static final String file3 = "noMissingData-d3.txt";
	
	static List<Node> nodes = new ArrayList<Node>();
	static int nodeNumber;
	static int dataNumber;
	
	public static void main(String[] args) throws IOException {
		
		// load the network
		read_file(file1);
		
		

	}
	
	public static void read_file(String filename) throws IOException {
		nodeNumber = 0;
		dataNumber = 0;
		String nodeID;
		List<Node> nodeParents;
		int posCount = 0;

		FileReader fr = new FileReader(filename); // the file to read from
		Scanner in = new Scanner(fr); // scanner for reading the file
		int lineNumber = 0; // the number of the line being read
		// The list of lists that corresponds to the dataSets being read
		ArrayList<ArrayList<Integer>> dataSets = new ArrayList<ArrayList<Integer>>();
		
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

			// Add each beginning position co-ordinate double to the list
			else if (lineNumber == 1) {
				String[] parts = line.split(" ");

				// All items on this lines are nodes
				for (int i = 0; i < parts.length; i++) {
					// Create the node
					Node n = new Node(parts[i], posCount);
					nodes.add(n);
					posCount++;
				}
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
		in.close();
	}
	
	private static Node getNode(String id) {
		for (int i = 0; i < nodes.size(); i++) {
			if (id == nodes.get(i).getIdentifier()) {
				return nodes.get(i);
			}
		}
		return null;
	}
}
