package co.inharmonic.testing;

import co.inharmonic.net.NeuralNet;

public class FullTest01 {
	public void runTest01() {
		// create a neural net with the structure specified below then print the
		// result

		// create the net
		int[] netStructure = { 2, 3, 1 };

		NeuralNet net = new NeuralNet(netStructure);

		System.out.println("The blank net:");
		System.out.print(net);

		// input the values
		float[] inputValues = { 1.5f, 0.7f };
		float[] maxValues = { 2f, 2f };

		net.inputValues(inputValues, maxValues);

		System.out.println("The net after the input has been set:");
		System.out.print(net);

		// get the output
		System.out.println("The output values:");

		String outputString = "";
		float[] outputValues = net.getOutputValues();
		for (int i = 0; i < outputValues.length; i++) {
			outputString += outputValues[i] + " ";
		}

		System.out.println(outputString);

		System.out.println("The net after running:");
		System.out.print(net);
	}
}