package co.inharmonic.net;

import java.util.ArrayList;

/**
 * simple storage of a layer in the neural net
 */
public class Layer {
	private ArrayList<Node> nodes;
	
	/**
	 * creates a new layer with the given nodes
	 * @param nodes
	 */
	public Layer(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * creates a new layer, creating the number of nodes specified, all with weights set to 1
	 * @param numberOfNodes
	 */
	public Layer(int numberOfNodes) {
		nodes = new ArrayList<>();
		for (int i = 0; i < numberOfNodes; i++) {
			nodes.add(new Node());
		}
	}
	
	/**
	 * creates new default-value weights for each node in the layer
	 * @param weights
	 */
	public void setNumberOfWeightsPerNode(int weights) {
		for (Node node : nodes) {
			node.setNumberOfWeights(weights);
		}
	}
	
	/**
	 * this should be called only on the top input layer
	 * *check before calling that the number of inputValues is the same 
	 * as the number of nodes in this layer*
	 * @param inputValues the array of inputs into the neural net
	 */
	public void setInputs(float[] inputValues, float[] maxInputs) {
		for (int i = 0; i < nodes.size(); i++) {
			// this normalises the inputs to the range 0 - 1 as they are entered
			nodes.get(i).calculateOutput(inputValues[i] / maxInputs[i]);
		}
	}
	
	/**
	 * this method should be given the layer above it in the neural net.
	 * It then passes this layer on to each node, each node is, in turn responsible
	 * for calculating it's own output.
	 * @param layer
	 */
	public void calculateOutputs(Layer layer) {
		for (Node node : nodes) {
			node.calculateOutput(layer);
		}
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (Node node : nodes) {
			output += node.toString() + ", ";
		}
		return output;
	}
}
