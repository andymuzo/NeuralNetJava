package co.inharmonic.net;

import java.util.ArrayList;

/**
 * net contains the layers, which contains the nodes
 */
public class NeuralNet {
	private ArrayList<Layer> layers;
	private int[] shapeOfNet;
	
	/**
	 * @param shapeOfNet each item in the array represents a layer, each number is how many nodes per layer
	 */
	public NeuralNet(int[] shapeOfNet) {
		this.shapeOfNet = shapeOfNet;
		this.layers = createNewNet(shapeOfNet);
	}
	
	/**
	 * creates an entirely new, blank net with all weights and thresholds set to their default values.
	 * @param shapeOfNet
	 * @return
	 */
	private ArrayList<Layer> createNewNet(int[] shapeOfNet) {
		ArrayList<Layer> net = new ArrayList<>();
		// puts in the nodes and layers
		for (int nodes : shapeOfNet) {
			net.add(new Layer(nodes));
		}
		createWeightsForEachNode(net);
		return net;
	}
	
	/** creates the weights for each node. The top layer has none, 
	 *  each layer underneath has the amount of weights per node 
	 *  as the layer above it.
	 */	
	private void createWeightsForEachNode(ArrayList<Layer> net) {
		for (int i = 0; i < net.size(); i++) {
			if (i == 0) {
				// top layer
				net.get(i).setNumberOfWeightsPerNode(1);
			} else {
				net.get(i).setNumberOfWeightsPerNode(net.get(i - 1).getNumberOfNodes());
			}
		}
	}
	
	/**
	 * 
	 * @param inputValues this must have the same amount of values as 
	 * the amount of nodes in the input layer. It will report an error 
	 * in the output log otherwise.
	 */
	public void inputValues(float[] inputValues, float[] maxValues) {
		// check the length
		if (inputValues.length != shapeOfNet[0]) {
			System.out.println("Error: Number of inputs should match amount of nodes in input layer. " 
						+ inputValues.length + " input values given, " + shapeOfNet[0] + " expected.");
		} else if (maxValues.length != shapeOfNet[0]) {
			System.out.println("Error: Number of max values should match both the number of nodes in the first layer and the number of input values. " 
					+ maxValues.length + " max values given, " + shapeOfNet[0] + " expected.");
		} else {
			// assign the input values to the nodes on the 1st layer
			layers.get(0).setInputs(inputValues, maxValues);
		}
	}
	
	/**
	 * this method runs the net, depending on the size and complexity it could be a fairly long operation.
	 * Before running:
	 * 1 create net
	 * 2 teach net
	 * 3 input values
	 * @return the output of the net
	 */
	public float[] getOutputValues() {
		// give each layer the layer above it
		// this loop starts at 1 because the top layer is already set
		for (int i = 1; i < layers.size(); i++){
			layers.get(i).calculateOutputs(layers.get(i - 1));
		}
		// get the output from the net
		Layer outputLayer = layers.get(layers.size() - 1);
		// create an array of the correct size
		float[] outputValues = new float[outputLayer.getNumberOfNodes()];
		// get the outputs
		for (int i = 0; i < outputLayer.getNumberOfNodes(); i++) {
			outputValues[i] = outputLayer.getNode(i).getOutput();
		}
		return outputValues;
	}
	
	public ArrayList<Layer> getNet() {
		return layers;
	}
	
	public int[] getShapeOfNet() {
		return shapeOfNet;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (Layer layer : layers) {
			output += layer.toString() + "\n";
		}
		return output;
	}
}
