package co.inharmonic.audionet.control;

import co.inharmonic.audionet.neuralnet.NeuralNet;
import co.inharmonic.audionet.neuralnet.TrainingData;
import co.inharmonic.audionet.tools.MatrixOps;
import co.inharmonic.audionet.tools.Serializer;

public class TextUI {
	int inputNodes;
	int hiddenNodes;
	int outputNodes;
	
	double trainingRate;
	int trainingCycles;
	double minRandomWeight;
	double maxRandomWeight;
	double maxInput;
	double minInput;
	double minOutput;
	double maxOutput;
	NeuralNet net;
	String[] outputNames;
	TrainingData trainingData;
	AudioData audioData;
	String trainingFile;
	String trainingFileOutput;
	int startSample;
	int endSample;

	/**
	 * main constructor for setting up the colour net. The variables for
	 * training the net are here, should perhaps be a part of the TrainingData
	 * class
	 */
	public TextUI() {
		// set up all the parameters of the artificial neural network
		setup();
		
		boolean isTraining = false;
		
		if (isTraining) {
			// create the ANN
			net = new NeuralNet(hiddenNodes, trainingRate, trainingCycles,
					minRandomWeight, maxRandomWeight);

			// give the training data to the net
			setTrainingData();

			// run the training algorithm
			net.trainNet(true);

			// save the trained net
			saveNet("violin");
			// run the net for the training input
			showTrainingResults();
		} 
		else {
			loadNet("nets/ANN_200_220_200_1000violin");
			// then play with it!
			runTestValues();
			
			//audioData.printDifferences("audio/breath.wav", "audio/breathNoiseTest.wav", 2300, 2400);
		}
	}
	
	private void setup() {
		System.out.println("Setup");
		// the number of nodes in the 3 layers
		inputNodes = 200;
		hiddenNodes = 220;
		outputNodes = 200;
		
		// The rate at which the weight updates each training pass
		trainingRate = 0.01;
		
		// The number of times the backpropagation algorithm is used
		trainingCycles = 70000;
		
		// The spread of initial random weights
		minRandomWeight = -0.5;
		maxRandomWeight = 0.5;
		
		// The range of values in the input and output data
		minInput = -1.0;
		maxInput = 1.0;
		minOutput = -1.0;
		maxOutput = 1.0;
		
		audioData = new AudioData();
		
		trainingFile = "audio/violin.wav";
		trainingFileOutput = "audio/violinTraining.wav";
		startSample = 53000;
		endSample = 54000;
	}

	/**
	 * creates the training data and adds it to the trainingData object
	 */
	private void setTrainingData() {
		System.out.println("Set training data");
		// Training data; input X and output y.
		// * For each input there should be an expected output value
		// * The amount of inputs should be the same as the above int "inputs"
		// * the last input is always 1.0
		trainingData = new TrainingData();
		trainingData.setup(inputNodes, outputNodes, maxInput, minInput, minOutput, maxOutput);
		
		audioData.setAudioFile(trainingFile, startSample, endSample);
		
		audioData.predictiveTraining(trainingData);
		
		net.setTrainingData(trainingData);
	}
	
	private void showTrainingResults() {
		System.out.println("Saving training results to file:");
		double[][] outputData = net.runData(trainingData.getTrainingInput());

		outputResults(outputData, trainingFileOutput, true);
	}

	/**
	 * call this after training to run values through the net
	 */
	private void runTestValues() {
/*		
		double[][] data = MatrixOps.t(audioData.loadNormalisedAudio("audio/violin.wav", minInput, maxInput));
		for (int i = 0; i < 10; i++) {
			data = net.runData(audioData.getNextSamplePredictiveMulti(MatrixOps.t(data), inputNodes, outputNodes));
			data = audioData.stitchAudioFile(data);
			System.out.println("Fed back " + (i + 1) + " times.");
		}	
		outputResults(data, "audio/violinPredictiveViolin03.wav", false);
*/		
		
		double[][] outputData =	audioData.hallucinateMultiple(net, "audio/violin.wav", 5000, 100000, inputNodes);
		outputResults(outputData, "audio/violinHallucinate07.wav", true);
		
	}

	/**
	 * Saves the inputs and outputs of the net in an audio file
	 * 
	 * @param inputData
	 * @param outputData
	 */
	private void outputResults(double[][] outputData, String filename, boolean needsTransposing) {
		// convert it back to in the range -1.0 to 1.0 before outputting
		audioData.outputAudioFile(filename, MatrixOps.deNormalise(outputData, minOutput, maxOutput), needsTransposing);
	}
	
	private void saveNet(String tag) {
		Serializer serializer = new Serializer();
		// set the filename
		String filename = "nets/ANN_" + inputNodes + "_" + hiddenNodes + "_" + outputNodes + "_" + (endSample - startSample) + tag;
		serializer.serializeNet(net, filename);
	}
	
	private void loadNet(String filename) {
		Serializer serializer = new Serializer();
		// set the filename
		net = serializer.deserialzeNet(filename);
	}
}
