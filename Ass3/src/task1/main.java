package task1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import problem.Cycle;

public class main {

	List<Node> nodes = new ArrayList<Node>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void read_file(String filename) {

		FileReader fr = new FileReader(filename); // the file to read from
		Scanner in = new Scanner(fr); // scanner for reading the file
		int lineNumber = 0; // the number of the line being read
		// The list of the ASV start position co-ordinates

		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		int lineNo = 0;
		Scanner s;
		try {
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);
			int K = s.nextInt();
			int N = s.nextInt();
			s.close();

			
			line = input.readLine(); lineNo++;
			
		} catch (InputMismatchException e) {
			throw new IOException(String.format(
					"Invalid number format on line %d: %s", lineNo,
					e.getMessage()));
		} catch (NoSuchElementException e) {
			throw new IOException(String.format("Not enough tokens on line %d",
					lineNo));
		} catch (NullPointerException e) {
			throw new IOException(String.format(
					"Line %d expected, but file ended.", lineNo));
		} finally {
			input.close();
		}
	}
}
