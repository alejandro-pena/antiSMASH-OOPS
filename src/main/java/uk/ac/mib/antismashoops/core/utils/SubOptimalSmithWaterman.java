package uk.ac.mib.antismashoops.core.utils;

import java.util.Stack;

public class SubOptimalSmithWaterman
{
	public static String seqOne;
	public static String seqTwo;
	public static String[] sequences;
	private static short match = 2;
	private static short og = -10;
	private static short eg = -2;
	private static short gl = 0;
	private static short g;
	private static int minMatch;
	private static int score;

	public static short[][] matrix;

	public SubOptimalSmithWaterman()
	{
	}

	public static int calculateScore(String sequence, int mMatch)
	{
		int size = 1000;
		int counter = 0;
		score = 0;
		minMatch = mMatch;

		sequences = sequence.split("(?<=\\G.{" + size + "})");

		for (String s : sequences)
		{
			int localScore = execute(sequence, s, counter);
			if (localScore > (minMatch * 2))
				score += localScore;
			// System.out.println("----------------------------------------------------\n");
			counter++;
		}

		System.out.println("\nFinal score for the Cluster is: " + score);
		return score;
	}

	private static int execute(String sequence, String subsequence, int iteration)
	{
		seqOne = "-" + subsequence.toLowerCase();
		seqTwo = "-" + sequence.toLowerCase();
		matrix = new short[seqTwo.length()][seqOne.length()];

		initialiseMatrix();
		calculateMatrix();
		recalculateForSubOptimal();
		return getBacktrace(iteration);
	}

	private static void initialiseMatrix()
	{
		for (int i = 0; i < seqTwo.length(); i++)
		{
			for (int j = 0; j < seqOne.length(); j++)
			{
				if (i == 0 || j == 0)
					matrix[i][j] = 0;
				else
					matrix[i][j] = -1;
			}
		}
	}

	private static void calculateMatrix()
	{
		for (int i = 1; i < seqTwo.length(); i++)
		{
			for (int j = 1; j < seqOne.length(); j++)
			{
				if (matrix[i][j] == 0)
				{
					continue;
				}

				g = (short) (og + (gl * eg));
				if (seqTwo.charAt(i) == seqOne.charAt(j))
				{
					gl = 0;
					matrix[i][j] = max(score(i, j, g), matrix[i - 1][j] + g, matrix[i][j - 1] + g);

				} else
				{
					gl++;
					matrix[i][j] = max(score(i, j, g), matrix[i - 1][j] + g, matrix[i][j - 1] + g);
				}
			}
		}
	}

	private static void recalculateForSubOptimal()
	{
		int maxI = 0;
		int maxJ = 0;
		int highest = 0;

		// FIND HIGHEST SCORE AND DELETE IT

		for (int i = 0; i < seqTwo.length(); i++)
		{
			for (int j = 0; j < seqOne.length(); j++)
			{
				if (matrix[i][j] >= highest)
				{
					highest = matrix[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}

		for (int i = maxI, j = maxJ; i > 0 && j > 0; i--, j--)
		{
			matrix[i][j] = 0;
		}

		calculateMatrix();
	}

	private static int getBacktrace(int iteration)
	{
		Stack<String> trace = new Stack<>();
		int maxI = 0;
		int maxJ = 0;
		int highest = 0;

		// FIND HIGHEST SCORE

		for (int i = 0; i < seqTwo.length(); i++)
		{
			for (int j = 0; j < seqOne.length(); j++)
			{
				if (matrix[i][j] >= highest)
				{
					highest = matrix[i][j];
					maxI = i;
					maxJ = j;
				}
			}
		}

		System.out.println("Highest score: " + highest + " at (" + maxI + "," + maxJ + ")\n");

		int i = maxI;
		int j = maxJ;

		while (matrix[i][j] > 0 && i != 0 && j != 0)
		{
			if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i - 1][j - 1])
			{
				trace.push("m");
				i -= 1;
				j -= 1;
			} else if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i - 1][j])
			{
				trace.push("d");
				i -= 1;
			} else if (max(matrix[i - 1][j - 1], matrix[i - 1][j], matrix[i][j - 1]) == matrix[i][j - 1])
			{
				trace.push("i");
				j -= 1;
			} else
				System.out.println("Something funky going on here...");
		}

		Stack<String> bTrace = (Stack<String>) trace.clone();

		StringBuilder seqOneAlignment = new StringBuilder();
		StringBuilder seqTwoAlignment = new StringBuilder();

		int iValue = i;
		int jValue = j;

		i++;
		j++;

		while (!trace.isEmpty())
		{
			if (trace.peek().equalsIgnoreCase("i"))
			{
				seqTwoAlignment.append("-");
				trace.pop();
			} else
			{
				seqTwoAlignment.append(seqTwo.charAt(i++));
				trace.pop();
			}
		}

		while (!bTrace.isEmpty())
		{
			if (bTrace.peek().equalsIgnoreCase("d"))
			{
				seqOneAlignment.append("-");
				bTrace.pop();
			} else
			{
				seqOneAlignment.append(seqOne.charAt(j++));
				bTrace.pop();
			}
		}

		// System.out.println("");
		// System.out.println(seqOneAlignment.toString() + " | " + (jValue +
		// (iteration * 1000)) + " - "
		// + ((j - 1) + (iteration * 1000)));
		// System.out.println(seqTwoAlignment.toString() + " | " + iValue + " -
		// " + (i - 1));

		return highest;
	}

	private static short score(int i, int j, short g)
	{
		if (seqTwo.charAt(i) == seqOne.charAt(j))
			return (short) (matrix[i - 1][j - 1] + match);
		else
			return (short) (matrix[i - 1][j - 1] + g);
	}

	private static short max(int scoreA, int scoreB, int scoreC)
	{
		short score = (short) ((scoreA > scoreB ? scoreA : scoreB) > scoreC ? (scoreA > scoreB ? scoreA : scoreB)
				: scoreC);
		return score > 0 ? score : 0;
	}

	private void printMatrix(short[][] matrix)
	{
		System.out.println("");

		for (int i = 0; i < seqTwo.length(); i++)
		{
			if (i == 0)
			{
				for (int k = -1; k < seqOne.length(); k++)
				{
					if (k == -1)
						System.out.print("    ");
					else
						System.out.format("%4s", seqOne.charAt(k));
				}
				System.out.println("");
			}

			for (int j = -1; j < seqOne.length(); j++)
			{
				if (j == -1)
					System.out.format("%4s", seqTwo.charAt(i));
				else
					System.out.format("%4d", matrix[i][j]);
			}
			System.out.println("");
		}
	}
}
