package co.inharmonic.net;

import java.util.ArrayList;

/**
 * simple storage of a node in the neural net
 */
public class Node {
	private ArrayList<Float> inputWeights = new ArrayList<Float>();
	private float activationThreshold;
	private float output;
	
	/**
	 * constructor where weights are already given
	 * @param inputWeights
	 * @param activationThreshold
	 */
	public Node(ArrayList<Float> inputWeights, float activationThreshold) {
		this.inputWeights = inputWeights;
		this.activationThreshold = activationThreshold;
		this.output = 0f; // defaults to an output of 0
	}
	
	/**
	 * empty node with no weights and an activation threshold set to 0
	 */
	public Node() {
		this.inputWeights = new ArrayList<>();
		this.activationThreshold = 0f;
		this.output = 0f; // defaults to an output of 0
	}
	
	/**
	 * creates the specified number of weights for the node all at the default value.
	 * @param weights
	 */
	public void setNumberOfWeights(int weights) {
		inputWeights = new ArrayList<>();
		for (int i = 0; i < weights; i++) {
			// set the weight to a default value
			inputWeights.add(new Float(1f));
		}
	}
	
	/**
	 * this method is used if the node is in the 1st layer, it calculates 
	 * it's outputs based on the input to the net, not based on the layer above
	 * @param the input assigned to this node
	 */
	public void calculateOutput(float input) {
		// NOTE: this may all be totally off!
		// TODO: I suspect that the output should simply be assigned to the input, check this later.
		output = input * inputWeights.get(0);
		// for now the activation threshold simply shuts down the output if it is less.
		// this will eventually be replaced with a sigmoid function
		if (output < activationThreshold) {
			output = 0f;
		}
	}
	
	/**
	 * this method calculates the node's output based on the layer above, 
	 * this node's weights and the activation threshold of the node.
	 * @param layer the layer above the one this node is in. If this is the top 
	 * layer use the method with a float input
	 */
	public void calculateOutput(Layer layer) {
		// reset the output
		output = 0f;
		// get the nodes from the layer above
		ArrayList<Node> nodes = layer.getNodes();
		// iterate over the previous layer and this nodes weights, adding them to the output.
		for (int i = 0; i < nodes.size(); i++) {
			output += nodes.get(i).getOutput() * inputWeights.get(i);
		}
		// for now the activation threshold simply shuts down the output if it is less.
		// this will eventually be replaced with a sigmoid function
		if (output < activationThreshold) {
			output = 0f;
		}
	}
	
	public ArrayList<Float> getWeights() {
		return inputWeights;
	}
	
	public float getActivationThreshold() {
		return activationThreshold;
	}
	
	public float getOutput() {
		return output;
	}
	
	public void setWeights(ArrayList<Float> inputWeights) {
		this.inputWeights = inputWeights;
	}
	
	public void setActivationThreshold(float activationThreshold) {
		this.activationThreshold = activationThreshold;
	}
	
	@Override
	public String toString() {
		String string = "{(";
		for (Float weight : inputWeights) {
			string += weight + " ";
		}
		string += ")[" + activationThreshold + "]=" + output + "}";
		return string;
	}
}
