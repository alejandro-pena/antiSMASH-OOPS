package uk.ac.mib.antismashoops.core.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class SelfHomologyService {
    private static final String directory = "selfhomology";
    public static String seqOne;
	public static String seqTwo;
	public static String[] sequences;
    public static short[][] matrix;
    private static short match = 2;
	private static short og = -10;
	private static short eg = -2;
	private static short gl = 0;
	private static short g;
	private static int minMatch;
	private static int score;
	private static String fileName;

	/**
	 * 
	 * This class implements a version of the Smith-Waterman algorithm to find
	 * the suboptimal sequences in chunks of 1000 nucleotides against the entire
	 * BGC sequence
	 * 
	 */

	public SelfHomologyService() {
	}

	public static int calculateScore(String sequence, int mMatch, String cluster, String number) {
		int size = 1000;
		int counter = 0;
		score = 0;
		minMatch = mMatch;
		fileName = cluster + ".cluster" + number + ".txt";

		File directoryFile = new File(directory);
		File file = new File(directory, fileName);

		if (file.exists())
			file.delete();

		if (!directoryFile.exists())
			directoryFile.mkdirs();

		sequences = sequence.split("(?<=\\G.{" + size + "})");

		for (String s : sequences) {
			int localScore = execute(sequence, s, counter);
			if (localScore > (minMatch * 2))
				score += localScore;
			counter++;
		}

		// System.out.println("\nFinal score for the Cluster is: " + score);
		writeToFile("\nFinal score for the Cluster is: " + score);
		return score;
	}

	private static int execute(String sequence, String subsequence, int iteration) {
		seqOne = "-" + subsequence.toLowerCase();
		seqTwo = "-" + sequence.toLowerCase();
		matrix = new short[seqTwo.length()][seqOne.length()];

		initialiseMatrix();
		calculateMatrix();
		recalculateForSubOptimal();
		return getBacktrace(iteration);
	}

	private static void initialiseMatrix() {
		for (int i = 0; i < seqTwo.length(); i++) {
			for (int j = 0; j < seqOne.length(); j++) {
				if (i == 0 || j == 0)
					matrix[i][j] = 0;
				else
					matrix[i][j] = -1;
			}
		}
	}

	private static void calculateMatrix() {
		for (int i = 1; i < seqTwo.length(); i++) {
			for (int j = 1; j < seqOne.length(); j++) {
				if (matrix[i][j] == 0) {
					continue;
				}

				g = (short) (og + (gl * eg));
				if (seqTwo.charAt(i) == seqOne.charAt(j)) {
					gl = 0;
					matrix[i][j] = max(score(i, j, g), matrix[i - 1][j] + g, matrix[i][j - 1] + g);

				} else {
					gl++;
					matrix[i][j] = max(score(i, j, g), matrix[i - 1][j] + g, matrix[i][j - 1] + g);
				}
			}
		}
	}

	private static void recalculateForSubOptimal() {
		int maxI = 0;
		int maxJ = 0;
		int highest = 0;

		// FIND HIGHEST SCORE AND DELETE IT

		for (int i = 0; i < seqTwo.length(); i++) {
			for (int j = 0; j < seqOne.length(); j++) {
				if (matrix[i][j] >= highest) {
					highest = matrix[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}

		for (int i = maxI, j = maxJ; i > 0 && j > 0; i--, j--) {
			matrix[i][j] = 0;
		}

		calculateMatrix();
	}

	private static int getBacktrace(int iteration) {
		Stack<String> trace = new Stack<>();
		int maxI = 0;
		int maxJ = 0;
		int highest = 0;

		// FIND HIGHEST SCORE

		for (int i = 0; i < seqTwo.length(); i++) {
			for (int j = 0; j < seqOne.length(); j++) {
				if (matrix[i][j] >= highest) {
					highest = matrix[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}

		int i = maxI;
		int j = maxJ;

		while (matrix[i][j] > 0 && i != 0 && j != 0) {
			if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i - 1][j - 1]) {
				trace.push("m");
				i -= 1;
				j -= 1;
			} else if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i - 1][j]) {
				trace.push("d");
				i -= 1;
			} else if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i][j - 1]) {
				trace.push("i");
				j -= 1;
			} else
				System.out.println("Something funky going on here...");
		}

		@SuppressWarnings("unchecked")
		Stack<String> bTrace = (Stack<String>) trace.clone();

		StringBuilder seqOneAlignment = new StringBuilder();
		StringBuilder seqTwoAlignment = new StringBuilder();

		int iValue = i;
		int jValue = j;

		i++;
		j++;

		while (!trace.isEmpty()) {
			if (trace.peek().equalsIgnoreCase("i")) {
				seqTwoAlignment.append("-");
				trace.pop();
			} else {
				seqTwoAlignment.append(seqTwo.charAt(i++));
				trace.pop();
			}
		}

		while (!bTrace.isEmpty()) {
			if (bTrace.peek().equalsIgnoreCase("d")) {
				seqOneAlignment.append("-");
				bTrace.pop();
			} else {
				seqOneAlignment.append(seqOne.charAt(j++));
				bTrace.pop();
			}
		}

		// System.out.println("Alignment score: " + highest);
		writeToFile("Alignment score: " + highest + "\n");

		// System.out.println(seqOneAlignment.toString() + " | " + (jValue +
		// (iteration * 1000)) + " - "
		// + ((j - 1) + (iteration * 1000)));
		writeToFile(seqOneAlignment.toString() + " | " + (jValue + (iteration * 1000)) + " - "
				+ ((j - 1) + (iteration * 1000)) + "\n");

		// System.out.println(seqTwoAlignment.toString() + " | " + iValue + " -
		// " + (i - 1) + "\n");
		writeToFile(seqTwoAlignment.toString() + " | " + iValue + " - " + (i - 1) + "\n\n");

		// System.out.println("");

		return highest;
	}

	private static short score(int i, int j, short g) {
		if (seqTwo.charAt(i) == seqOne.charAt(j))
			return (short) (matrix[i - 1][j - 1] + match);
		else
			return (short) (matrix[i - 1][j - 1] + g);
	}

	private static short max(int scoreA, int scoreB, int scoreC) {
		short score = (short) ((scoreA > scoreB ? scoreA : scoreB) > scoreC ? (scoreA > scoreB ? scoreA : scoreB)
				: scoreC);
		return score > 0 ? score : 0;
	}

	public static void writeToFile(String data) {
		File file = new File(directory, fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(data);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
