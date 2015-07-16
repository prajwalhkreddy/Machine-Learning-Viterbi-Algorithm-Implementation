import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/*Prajwal Halasahally KeshavaReddy
 * Viterbi Algorithm Implementation
 * Given a state diagram(in a fixed format) and the required observation sequence, the program calculates the most likelihood sequence.
 * Procedure followed:
 * 1.Read the model file into different variables and matrices as required
 * 2.Read the test file
 * 3.For each line/observation sequence the most likely path is obtained
 * 
 * The most likely path is obtained by retrieving the index of each character in the observation sequence and by then obtaining
 * the column number in the observation matrix.
 * 
 * Since initial probabilities are given it has been handled separately.
*/
public class ViterbiAlgorithm {

	static int numStates;
	static String[] initialProbabilities;
	static int numOfOutput;
	static String outputCharacters;
	static String[][] outputDistributions;
	static String[] tempDistributions;
	static String[][] transitionMatrix;
	static String[] tempMatrix;
	static String obsSequence;

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		if (args.length != 2) {
			System.out
					.println("Model and Test files not entered as command line arguments");
		}
		ViterbiAlgorithm va = new ViterbiAlgorithm();
		va.readModelData(args[0]);
		va.readTestData(args[1]);
		// va.calculateProbability();

	}

	
	//Read model file
	public void readModelData(String fileName) throws NumberFormatException,
			IOException {

		FileInputStream in = null;
		String input;
		BufferedReader br = null;
		int lineCounter = 1;

		try {
			File ipfile = new File(fileName);
			in = new FileInputStream(ipfile);
			br = new BufferedReader(new InputStreamReader(in));

			while ((input = br.readLine()) != null) {
				switch (lineCounter) {
				case 1:
					numStates = Integer.parseInt(input);
					// System.out.println("Num States:" + numStates);
					break;
				case 2:
					initialProbabilities = new String[numStates];
					initialProbabilities = input.split("\\s");

					/*
					 * System.out.println("Initial Probabilities:" +
					 * initialProbabilities[0] + " " + initialProbabilities[1] +
					 * " " + initialProbabilities[2]);
					 */

					break;
				case 3:
					tempMatrix = input.split("\\s");
					// System.out.println("Temp Matrix:"+ tempMatrix[0]);
					transitionMatrix = new String[numStates][numStates];
					int x = 0;
					for (int i = 0; i < numStates; i++) {
						for (int j = 0; j < numStates; j++) {

							if (x < tempMatrix.length) {

								transitionMatrix[i][j] = tempMatrix[x];
								// System.out.print(transitionMatrix[i][j] +
								// "\t");
								x++;
							}

						}
						// System.out.println("\n");
					}

					break;
				case 4:
					numOfOutput = Integer.parseInt(input);
					// System.out.println("Number of output: " + numOfOutput);
					break;
				case 5:
					outputCharacters = input;
					// System.out.println("Output Characters:" +
					// outputCharacters);
					break;
				case 6:
					tempDistributions = input.split("\\s");
					/*
					 * System.out.println("Temp Distributions:" +
					 * tempDistributions[0]);
					 */
					int z = 0;
					outputDistributions = new String[numStates][numOfOutput];
					for (int i = 0; i < numStates; i++) {
						for (int j = 0; j < numOfOutput; j++) {

							if (z < tempDistributions.length) {

								outputDistributions[i][j] = tempDistributions[z];
								/*
								 * System.out.print(outputDistributions[i][j] +
								 * "\t");
								 */
								z++;
							}

						}
						// System.out.println("\n");
					}

					break;
				}
				lineCounter++;

			}

		} catch (FileNotFoundException e) {
			System.out.println("Not able to open file:" + fileName);
		} finally {
			br.close();
		}

	}

	//Read test file
	public void readTestData(String fileName) throws IOException,
			FileNotFoundException {

		FileInputStream in = null;
		String input;
		BufferedReader br = null;

		int lineCounter = 0;
		try {
			File ipfile = new File(fileName);
			in = new FileInputStream(ipfile);
			br = new BufferedReader(new InputStreamReader(in));

			while ((input = br.readLine()) != null) {
				// System.out.println("LineCounter:" + lineCounter);
				obsSequence = input;
				calculateProbability(input);
				lineCounter++;

			}
			// System.out.println("Observation Sequence:" + obsSequence);

		} catch (FileNotFoundException e) {
			System.err.println("Not able to open file:" + fileName);
		} finally {
			br.close();
		}

	}

	// Double[] weights = new Double[numStates];
	int count = 0;
	ArrayList<Integer> indexes = new ArrayList<Integer>();
	ArrayList<Double> weights = new ArrayList<Double>();
	HashMap<Integer, ArrayList<Double>> weightedIndex = new HashMap<Integer, ArrayList<Double>>();
	
	
	//For every line or observation sequence the calculateProbability method is called
	void calculateProbability(String strSeq) {
		int count = 0;
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<Double> weights = new ArrayList<Double>(); //Stores the probabilities at each state and updated accordingly
		HashMap<Integer, ArrayList<Double>> weightedIndex = new HashMap<Integer, ArrayList<Double>>(); //notUsed
		
		int initTemp; 
		int indexTemp = 0; //Used to obtain the col numbers from the observation table
		
		// String abc="c c c c c c c c c c c c c a c a c a a a a a a a a";
		// String abc="c c c a c c c a a a";
		String[] sequence = strSeq.split("\\s");
		// System.out.println(strSeq);
		String temp = outputCharacters.replaceAll("\\s", ""); // Removing all the spaces in the string output character

		Integer[][] pathMatrix = new Integer[numStates][sequence.length];
		for (int i = 0; i < sequence.length; i++) {
			indexes.add(temp.indexOf(sequence[i]));
		}
		// System.out.println(indexes);

		
		//Iterate over each observation sequence
		for (int x = 0; x < sequence.length; x++) {
			if (count == 0) {
				
				//Enters only for the initial state anc calculates the probabilities and updates the weight variable
				for (int i = 0; i < numStates; i++) {
					initTemp = indexes.get(indexTemp);
					// System.out.println(initialProbabilities[i]);
					// System.out.println(outputDistributions[i][initTemp]);

					weights.add(Double.parseDouble(initialProbabilities[i])
							* Double.parseDouble(outputDistributions[i][initTemp]));
					// System.out.println("Initial Weights:" + weights);
					count++;
				}
				// System.out.println("Initial Weights:" + weights);
				weightedIndex.put(x, weights);
				double tempMax = 0.0;
				for (int p = 0; p < numStates; p++) {
					if (weights.get(p) >= tempMax) {
						tempMax = weights.get(p);
					}

				}
				// System.out.println(tempMax);
				
				//Updating the final pathMatrix as well
				for (int p = 0; p < numStates; p++) {
					pathMatrix[p][x] = weights.indexOf(tempMax);
					// System.out.println(weights.indexOf(tempMax));
				}

			} else {
				ArrayList<Double> weightedTemp = new ArrayList<Double>();
				double tempWeights = 0;
				double max = 0.0;
				int index = 0;
				initTemp = indexes.get(indexTemp);
				int r;
				// System.out.println("Indexes:"+initTemp);
				
				//Iterate over the remaining observation sequences
				//Note: Weights variable consists of the weights of the previous state
				for (int i = 0; i < numStates; i++) {
					for (r = 0; r < numStates; r++) {
						tempWeights = weights.get(r)
								* Double.parseDouble(transitionMatrix[r][i])
								* Double.parseDouble(outputDistributions[r][initTemp]);
						//Obtain the max weight for a given state and note its index value
						if (tempWeights >= max) {
							max = tempWeights;
							index = r;
						}

					}
					// System.out.println(index);
					// System.out.println("i"+i+"x"+x);
					pathMatrix[i][x] = index;

					weights.set(i, max);

				}
				// System.out.println(index);

				// System.out.println(weights);

			}

			indexTemp++;

		}

		// Final path Matrix
		/*
		 * for(int r=0;r<numStates;r++){ for(int c=0;c<sequence.length;c++){
		 * System.out.print(pathMatrix[r][c]+"\t"); } System.out.println(); }
		 */

		// Final state probabilities
		// System.out.println(weights);

		
		//Retrieve the state with the highest probability value
		double finalTemp = 0.0;
		int finalState = 0;
		for (int i = 0; i < numStates; i++) {
			if (weights.get(i) > finalTemp) {
				finalTemp = weights.get(i);
				finalState = i;
			}
		}

		int cur_State = finalState;
		// System.out.println(currentState);

		StringBuilder printBuffer = new StringBuilder();

		//Traverse the path matrix from the last column to the first and obtain the likely sequence
		for (int i = sequence.length - 1; i >= 0; i--) {

			if (i == 0) {
				printBuffer.insert(0, "S" + String.valueOf(cur_State + 1));
			} else {
				printBuffer.insert(0,
						" --> S" + String.valueOf(cur_State + 1));
				cur_State = pathMatrix[cur_State][i];
			}
		}

		// Output Display
		System.out.println("Given observation sequence: " + obsSequence);
		System.out.println("Most Likely State sequence:");
		System.out.println(printBuffer);

	}

}
