package co.inharmonic.colours.net;

import co.inharmonic.colours.control.TrainingData;
import co.inharmonic.colours.tools.MatrixOps;

public class NeuralNet {
	MatrixOps m;
	int hiddenNeurons;
	double trainingRate; // The rate at which the weight updates each training pass
	int trainingCycles; // The number of times the backpropagation algorithm is used
	double minRandomWeight; // The spread of initial random weights
	double maxRandomWeight;
	double[][] X; // training input
	double[][] y; // training output
	double[][] synapse0; // weights between input layer and hidden
	double[][] synapse1; // weights between hidden layer and output
	double[][] layer1; // hidden layer
	double[][] layer2; // output layer
	
	/**
	 * Create a new artificial neural net with the parameters given
	 * @param inputs The number of input nodes in the top layer
	 * @param hiddenNeurons The number of nodes in the hidden layer
	 * @param outputNeurons The number of output nodes in the bottom layer
	 * @param trainingRate The rate at which the weight updates each training pass
	 * @param trainingCycles The number of times the backpropagation algorithm is used (60k works)
	 * @param minRandomWeight The spread of initial random weights - Min
	 * @param maxRandomWeight The spread of initial random weights - Max
	 */
	public NeuralNet(int hiddenNeurons, double trainingRate, 
			int trainingCycles, double minRandomWeight, double maxRandomWeight) {
		m = new MatrixOps();
		
		setParameters(hiddenNeurons, trainingRate,
				trainingCycles, minRandomWeight, maxRandomWeight);
	}
	
	/**
	 * 
	 * @param inputs The number of input nodes in the top layer
	 * @param hiddenNeurons The number of nodes in the hidden layer
	 * @param outputNeurons The number of output nodes in the bottom layer
	 * @param trainingRate The rate at which the weight updates each training pass
	 * @param trainingCycles The number of times the backpropagation algorithm is used (60k works)
	 * @param minRandomWeight The spread of initial random weights - Min
	 * @param maxRandomWeight The spread of initial random weights - Max
	 */
	public void setParameters(int hiddenNeurons,double trainingRate, 
			int trainingCycles, double minRandomWeight, double maxRandomWeight) {
		this.hiddenNeurons = hiddenNeurons;
		this.trainingRate = trainingRate;
		this.trainingCycles = trainingCycles;
		this.minRandomWeight = minRandomWeight;
		this.maxRandomWeight = maxRandomWeight;
	}
	
	/**
	 * pass the training data to the net before calling trainNet()
	 * This method might need updating when used in different applications,
	 * TrainingData in this instance is specific to the colours net, should
	 * be made into an interface in future
	 * @param trainingData
	 */
	public void setTrainingData(TrainingData trainingData) {
		// normalise the input and outputs
		this.X = m.normalise(trainingData.getTrainingInput(), trainingData.getMaxInput());
		this.y = m.normalise(trainingData.getTrainingOutput(), trainingData.getMaxOutput());
	}
	
	/**
	 * run this method after inputting the training data to train the net
	 */
	public void trainNet() {		
		// Synapses contain the weights for each layer, these are randomised to begin with
		synapse0 = new double[X[0].length][hiddenNeurons];
		populateRandom(synapse0, minRandomWeight, maxRandomWeight);
		
		synapse1 = new double[hiddenNeurons][y[0].length];
		populateRandom(synapse1, minRandomWeight, maxRandomWeight);
		
		// The layers are the output values of each layer.
		// They are initialised to nothing to begin with, created properly in the training loop
		layer1 = new double[0][0];
		layer2 = new double[0][0];
		
		// Printing the initial state of the system
		m.printMatrix("X:", X);
		m.printMatrix("y:", y);
		
		// The training loop
		for (int i = 0; i < trainingCycles; i++) {
			// calculate the values of each layer given the inputs and the weights
			layer1 = forwardPropogate(X, synapse0);
			layer2 = forwardPropogate(layer1, synapse1);
			
			// Calculate the delta error for each output layer, starting
			// with the bottom working up.
			// This is the difference between the expected values and actual values
			// times the derivative (gradient) of the sigmoid activation function
			// The 1st error comes from y - output
			double[][] layer2Delta = m.subtract(y, layer2);
			layer2Delta = delta(layer2Delta, layer2);
			// subsequent layers come from the delta of the lower layer divided by the weights
			double[][] layer1Delta = m.dot(layer2Delta, m.t(synapse1));
			layer1Delta = delta(layer1Delta, layer1);
			
			// Apply the error gradients to each weight, this moves the value closer to the expected
			// or reduces the error
			synapse1 = m.add(synapse1, m.scale(m.dot(m.t(layer1), layer2Delta), trainingRate));
			synapse0 = m.add(synapse0, m.scale(m.dot(m.t(X), layer1Delta), trainingRate));
		}
		// Run the input matrix through the net to get outputs for each training value on layer 2
		double[][] testNet = runData(X);
		// Show the results
		m.printMatrix("Output for X after training:", testNet);
		m.printMatrixInts("Output for X after training (out of 100:", testNet);
	}
	
	/**
	 * uses the trained neural net to return aan output for given training data
	 * @param inputs
	 */
	public double[][] runData(double[][] inputs) {
		layer1 = forwardPropogate(inputs, synapse0);
		layer2 = forwardPropogate(layer1, synapse1);
		
		return layer2.clone();
	}
	
	// Helper methods *************************************************************
	
	/**
	 * calculates the values of a layer given the inputs and the weights
	 * @param inputs
	 * @param weights
	 * @return
	 */
	private double[][] forwardPropogate(double[][] inputs, double[][] weights) {
		double[][] resultLayer = m.dot(inputs, weights);
		sigmoid(resultLayer);
		return resultLayer;
	}
	
	/**
	 * applies the sigmoid function to all elements in an matrix
	 * Input matrix is changed.
	 * @param array
	 */
	private void sigmoid(double[][] matrix) {
		// Sigmoid function:
		// = 1/(1+e^(-(inputs.weights)))
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = 1.0 / (1.0 + Math.exp(-matrix[i][j]));
			}
		}
	}
	
	/**
	 * populates the matrix with all random numbers from min to max
	 * @param matrix
	 */
	private void populateRandom(double[][] matrix, double min, double max) {
		double range = max - min;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (range * Math.random()) + min;
			}
		}
	}
	
	/**
	 * use to calculate the delta error for a layer
	 * @param error
	 * @param layer
	 * @return
	 */
	private double[][] delta(double[][] error, double[][] layer) {
		double[][] delta = new double[error.length][error[0].length];
		for (int j = 0; j < error.length; j++) {
			for (int k = 0; k < error[0].length; k++) {
				delta[j][k] = error[j][k] * (layer[j][k] * (1.0 - layer[j][k]));
			}
		}
		return delta;
	}
	
	/**
	 * prints the tag followed by the matrix
	 * @param tag
	 * @param matrix
	 */
	public void printMatrix(String tag, double[][] matrix) {
		m.printMatrix(tag, matrix);
	}
	
	/**
	 * prints the tag followed by the matrix as a more readable integer out of 100
	 * @param tag
	 * @param matrix
	 */
	public void printMatrixInts(String tag, double[][] matrix) {
		m.printMatrixInts(tag, matrix);
	}
}
