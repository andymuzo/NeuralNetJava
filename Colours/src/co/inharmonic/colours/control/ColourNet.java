package co.inharmonic.colours.control;

import co.inharmonic.colours.control.TrainingData.brightness;
import co.inharmonic.colours.control.TrainingData.colour;
import co.inharmonic.colours.control.TrainingData.saturation;
import co.inharmonic.colours.net.NeuralNet;

public class ColourNet {
	int hiddenNeurons;
	double trainingRate;
	int trainingCycles;
	double minRandomWeight;
	double maxRandomWeight;
	NeuralNet net;
	// Training data; input X and output y.
	// * For each input there should be an expected output value
	// * The amount of inputs should be the same as the above int "inputs"
	// * the last input is always 1.0
	double[][] trainingInputs;
	double[][] trainingOutputs;
	String[] outputNames;
	TrainingData trainingData;
	
	/**
	 * main constructor for setting up the colour net.
	 * The variables for training the net are here, should perhaps be a part of the TrainingData class
	 */
	public ColourNet() {
		// The setup
		hiddenNeurons = 30;
		// The rate at which the weight updates each training pass
		trainingRate = 1.0;
		// The number of times the backpropagation algorithm is used
		trainingCycles = 60000;
		// The spread of initial random weights
		minRandomWeight = -0.5;
		maxRandomWeight = 0.5;

		net = new NeuralNet(hiddenNeurons, trainingRate,
				trainingCycles, minRandomWeight, maxRandomWeight);

		setTrainingData();

		net.trainNet();
		
		runTestValues();
	}

	/**
	 * creates the training data and adds it to the trainingData object
	 */
	private void setTrainingData() {
		// Training data; input X and output y.
		// * For each input there should be an expected output value
		// * The amount of inputs should be the same as the above int "inputs"
		// * the last input is always 1.0
		trainingData = new TrainingData(4, 15, 255.0, 1.0);
		
		// adding one example of each colour at mid brightness, full saturation
		trainingData.addTrainingData(255.0, 0.0, 0.0, colour.red, brightness.light, saturation.high);
		trainingData.addTrainingData(0.0, 255.0, 0.0, colour.green, brightness.light, saturation.high);
		trainingData.addTrainingData(0.0, 0.0, 255.0, colour.blue, brightness.light, saturation.high);
		trainingData.addTrainingData(128.0, 0.0, 128.0, colour.purple, brightness.medium, saturation.high);
		trainingData.addTrainingData(255.0, 255.0, .0, colour.yellow, brightness.light, saturation.high);
		trainingData.addTrainingData(255.0, 0.0, 255.0, colour.purple, brightness.light, saturation.high);
		trainingData.addTrainingData(255.0, 165.0, 0.0, colour.orange, brightness.light, saturation.high);
		trainingData.addTrainingData(139.0, 60.0, 0.0, colour.brown, brightness.medium, saturation.high);
		trainingData.addTrainingData(0.0, 0.0, 0.0, colour.black, brightness.dark, saturation.low);
		trainingData.addTrainingData(255.0, 255.0, 255.0, colour.white, brightness.light, saturation.low);
		// adding odd examples of bright, dark and de-saturated colours, fairly randomly 
		// selected but avoided ambiguous colours (e.g. blue-green)
		trainingData.addTrainingData(210.0, 105.0, 30.0, colour.brown, brightness.light, saturation.high);
		trainingData.addTrainingData(244.0, 164.0, 96.0, colour.brown, brightness.light, saturation.medium);
		trainingData.addTrainingData(255.0, 228.0, 196.0, colour.brown, brightness.light, saturation.low);
		trainingData.addTrainingData(2.0, 8.0, 13.0, colour.brown, brightness.dark, saturation.high);
		trainingData.addTrainingData(144.0, 238.0, 144.0, colour.green, brightness.light, saturation.medium);
		trainingData.addTrainingData(143.0, 188.0, 143.0, colour.green, brightness.light, saturation.low);
		trainingData.addTrainingData(0.0, 0.0, 128.0, colour.blue, brightness.medium, saturation.high);
		trainingData.addTrainingData(224.0, 255.0, 255.0, colour.blue, brightness.light, saturation.low);
		trainingData.addTrainingData(250.0, 250.0, 210.0, colour.yellow, brightness.light, saturation.low);
		trainingData.addTrainingData(216.0, 191.0, 216.0, colour.purple, brightness.light, saturation.low);
		trainingData.addTrainingData(66.0, 9.0, 67.0, colour.purple, brightness.dark, saturation.high);
		trainingData.addTrainingData(128.0, 128.0, 0.0, colour.yellow, brightness.medium, saturation.high);
		trainingData.addTrainingData(220.0, 20.0, 60.0, colour.red, brightness.light, saturation.high);		
		trainingData.addTrainingData(178.0, 34.0, 34.0, colour.red, brightness.medium, saturation.high);	
		trainingData.addTrainingData(255.0, 127.0, 80.0, colour.orange, brightness.light, saturation.medium);	
		trainingData.addTrainingData(53.0, 33.0, 26.0, colour.orange, brightness.dark, saturation.medium);
		trainingData.addTrainingData(36.0, 71.0, 34.0, colour.green, brightness.dark, saturation.medium);
		trainingData.addTrainingData(50.0, 51.0, 64.0, colour.blue, brightness.dark, saturation.low);
		trainingData.addTrainingData(58.0, 47.0, 48.0, colour.red, brightness.dark, saturation.low);
		trainingData.addTrainingData(125.0, 121.0, 60.0, colour.yellow, brightness.medium, saturation.medium);
		trainingData.addTrainingData(138.0, 74.0, 125.0, colour.purple, brightness.medium, saturation.medium);
		trainingData.addTrainingData(47.0, 112.0, 54.0, colour.green, brightness.medium, saturation.medium);
		net.setTrainingData(trainingData);
	}
	
	/**
	 * call this after training to run values through the net
	 */
	private void runTestValues() {
		double[][] inputData = new double[][] {
				{132.0, 160.0, 70.0, 255.0},
				{246.0, 255.0, 217.0, 255.0},
				{102.0, 18.0, 186.0, 255.0},
				{164.0, 100.0, 227.0, 255.0},
				{103.0, 166.0, 166.0, 255.0},
				{13.0, 144.0, 168.0, 255.0}};
		double[][] outputData = net.runData(inputData);
		
		outputResults(inputData, outputData);
	}
	
	/**
	 * Prints the inputs and outputs of the net in human-readable form
	 * @param inputData
	 * @param outputData
	 */
	private void outputResults(double[][] inputData, double[][] outputData) {
		net.printMatrix("Input values:", inputData);
		net.printMatrixInts("Output for X (out of 100):",
				outputData);
		for (int i = 0; i < inputData.length; i++) {
			
			// first for colours
			int firstPositionColour = 0;
			for (int j = 0; j < TrainingData.colour.values().length; j++) {
				if (outputData[i][j] > outputData[i][firstPositionColour]) {
					firstPositionColour = j;
				}
			}
			int secondPositionColour = (firstPositionColour + 1) % TrainingData.colour.values().length;
			for (int j = 0; j < TrainingData.colour.values().length; j++) {
				if (outputData[i][j] > outputData[i][secondPositionColour]
						&& j != firstPositionColour) {
					secondPositionColour = j;
				}
			}
			
			// now brightness
			int startBright = TrainingData.colour.values().length;
			int endBright = TrainingData.colour.values().length + TrainingData.brightness.values().length;
			int firstPositionBright = startBright;			
			for (int j = startBright; j < endBright; j++) {
				if (outputData[i][j] > outputData[i][firstPositionBright]) {
					firstPositionBright = j;
				}
			}
			int secondPositionBright = ((firstPositionBright + 1 - startBright) % (TrainingData.brightness.values().length)) + startBright;
			for (int j = startBright; j < endBright; j++) {
				if (outputData[i][j] > outputData[i][secondPositionBright]
						&& j != firstPositionBright) {
					secondPositionBright = j;
				}
			}
			
			// now saturation
			int startSat = endBright;
			int endSat = endBright + TrainingData.saturation.values().length;
			int firstPositionSat = startSat;			
			for (int j = startSat; j < endSat; j++) {
				if (outputData[i][j] > outputData[i][firstPositionSat]) {
					firstPositionSat = j;
				}
			}
			int secondPositionSat = ((firstPositionSat + 1 - startSat) % (TrainingData.saturation.values().length)) + startSat;
			for (int j = startSat; j < endSat; j++) {
				if (outputData[i][j] > outputData[i][secondPositionSat]
						&& j != firstPositionSat) {
					secondPositionSat = j;
				}
			}
			
			// put it into a string
			int[] cert = trainingData.getCertaintyPercentages(outputData[i]);
			
			String resultsText = "RGB values: " + ((int) inputData[i][0]) + ", "
					+ ((int) inputData[i][1]) + ", " + ((int) inputData[i][2])
					+ ": \nColour: "
					+ trainingData.getOutputName(firstPositionColour) + " "
					+ cert[firstPositionColour] + "%, "
					+ trainingData.getOutputName(secondPositionColour) + " "
					+ cert[secondPositionColour] + "%,\nBrightness: "
					+ trainingData.getOutputName(firstPositionBright) + " "
					+ cert[firstPositionBright] + "%, "
					+ trainingData.getOutputName(secondPositionBright) + " "
					+ cert[secondPositionBright] + "%,\nSaturation: "	
					+ trainingData.getOutputName(firstPositionSat) + " "
					+ cert[firstPositionSat] + "%, "
					+ trainingData.getOutputName(secondPositionSat) + " "
					+ cert[secondPositionSat] + "%\n";

			System.out.println(resultsText);
		}
	}
}
