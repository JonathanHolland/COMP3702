package problem;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Provides a method to read the ASV information from a text file.
 */

public class ReadFile {

	private final static String EMPTY_LINE = "";

	/**
	 * The file consists of k+4 lines, where k is the number of obstacles. The
	 * first line is n, the number of ASVs. The next two lines are the initial
	 * and goal configurations. The next line is k, the number of obstacles.
	 * Each line in the last k lines represents the vertices of each rectangular
	 * obstacle.
	 * 
	 * Each rectangular obstacle is written as a quadruple of the x-y
	 * coordinates of its vertices in counter8clockwise order, starting with the
	 * lower-left vertex.
	 * 
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return variables related to the ASV (asvNo, asvPosStart, asvPosEnd,
	 *         obstacles stored as elements of this class)
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 */

	public static void read(String fileName) throws IOException {

		FileReader fr = new FileReader(fileName); // the file to read from
		Scanner in = new Scanner(fr); // scanner for reading the file
		int lineNumber = 0; // the number of the line being read
		// The list of the ASV start position co-ordinates
		ArrayList<ArrayList<Integer>> asvPosStart = new ArrayList<ArrayList<Integer>>();
		// The list of the ASV end position co-ordinates
		ArrayList<ArrayList<Integer>> asvPosEnd = new ArrayList<ArrayList<Integer>>();
		// The Map of the obstacle numbers to their associated quad-set of
		// co-ordinates
		Map<Integer, ArrayList<ArrayList<Integer>>> obstacles = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
		// The number of lines of obstacles found
		int obstacleNoCheck = 0;

		while (in.hasNextLine()) {
			String line = in.nextLine(); // read in the file line-by-line

			// If line is null or equal to empty string representation, break
			// loop
			if (line == null || EMPTY_LINE.equals(line)) {
				break;
			}

			// Assign the number of ASV's
			if (lineNumber == 0) {
				int asvNo = Integer.parseInt(line);
			}
			// Add each beginning position co-ordinate double to the list
			else if (lineNumber == 1) {
				String[] lineParts = line.split("\\s+");
				// Check that lineParts.length is even (%2) or throw IOException
				for (int i = 0; i < lineParts.length - 1; i = i + 2) {
					ArrayList<Integer> coords = new ArrayList<Integer>();
					coords.add(Integer.parseInt(lineParts[i]));
					coords.add(Integer.parseInt(lineParts[i + 1]));
					asvPosStart.add(coords);
				}
			}
			// Add each desired end position co-ordinate double to the list
			else if (lineNumber == 2) {
				String[] lineParts = line.split("\\s+");
				// Check that lineParts.length is even (%2) or throw IOException
				// Check that asvPosEnd is of same length as asvPosStart and
				// same as asvNo
				for (int i = 0; i < lineParts.length - 1; i = i + 2) {
					ArrayList<Integer> coords = new ArrayList<Integer>();
					coords.add(Integer.parseInt(lineParts[i]));
					coords.add(Integer.parseInt(lineParts[i + 1]));
					asvPosEnd.add(coords);
				}
			}

			else if (lineNumber == 3) {
				int obstacleNo = Integer.parseInt(line);
			}

			else {
				obstacleNoCheck++;

				String[] lineParts = line.split("\\s+");
				// Check that lineParts.length is even (%2) or throw IOException
				// Check that obstacleNoCheck is <= obstacleNo

				ArrayList<ArrayList<Integer>> vertices = new ArrayList<ArrayList<Integer>>();
				for (int i = 0; i < lineParts.length - 1; i = i + 2) {
					ArrayList<Integer> coords = new ArrayList<Integer>();
					coords.add(Integer.parseInt(lineParts[i]));
					coords.add(Integer.parseInt(lineParts[i + 1]));
					vertices.add(coords);
				}

				obstacles.put(obstacleNoCheck, vertices);
			}

			lineNumber++;

		}
		in.close();
	}

	// getters and setter methods for all variables gathered above
	// so they can be used in other class files

}
