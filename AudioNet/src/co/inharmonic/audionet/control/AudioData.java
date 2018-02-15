package co.inharmonic.audionet.control;

import java.util.ArrayList;
import java.util.Arrays;

import co.inharmonic.audionet.audiotools.StdAudio;
import co.inharmonic.audionet.neuralnet.NeuralNet;
import co.inharmonic.audionet.neuralnet.TrainingData;
import co.inharmonic.audionet.tools.MatrixOps;

/**
 * This is used to generate training and test data from an audio file, 
 * different methods will accomplish this in different ways.
 * @author Andrew
 *
 */
public class AudioData {
	private String fileName;
	private int sampleStart;
	private int sampleEnd;

	public void setAudioFile(String fileName, int sampleStart, int sampleEnd) {
		this.fileName = fileName;
		this.sampleStart = sampleStart;
		this.sampleEnd = sampleEnd;
	}
	
	/**
	 * creates training data whereby a certain number of samples used as inputs, 
	 * the output is one sample after the last one of the inputs.
	 * This is populated directly into the TrainingData object
	 * it is assumed there is just a single output
	 * @return
	 */
	public void predictiveTraining(TrainingData training) {
		double[] audioInput = Arrays.copyOfRange(StdAudio.read(fileName), sampleStart, sampleEnd);
		System.out.println("Opened audio file " + fileName + ", " + audioInput.length + " samples long");
		
		int inputNodes = training.getInputSize();
		int outputNodes = training.getOutputSize();
		// iterate over the array
		for (int i = 0; i < audioInput.length - inputNodes - outputNodes; i++) {
			// input
			double[] input = Arrays.copyOfRange(audioInput, i, i + inputNodes);
			// output
			double[] output = Arrays.copyOfRange(audioInput, i + inputNodes, i + inputNodes + outputNodes);
			training.addTrainingData(input, output);
		}
	}
	
	/**
	 * this only works with a single output, if more than one it constructs 
	 * an audio file just from the first number from each nested array
	 * @param filename the filename the audio should be saved as, e.g. "audio/violin.wav"
	 * @param outputs the array of outputs to be turned into an audiofile
	 */
	public void outputAudioFile(String filename, double[][] outputs, boolean needsTransposing) {
		if (needsTransposing) {
			StdAudio.save(filename, MatrixOps.transpose(outputs)[0]);
		}
		else {
			StdAudio.save(filename, outputs[0]);
		}
		System.out.println("file saved as \"" + filename + "\"");
	}
	
	/**
	 * "Next Sample Predictive" refers to the net where it looks at the previous samples in 
	 * the original audio then tries to predict what the next one might be. This takes an audio
	 * file and re-makes it as an input list where each item in double[] is a double[] of 
	 * values of length inputNodes from the start of each sample
	 * @param filename
	 * @param inputNodes
	 * @param outputNodes
	 * @return
	 */
	public double[][] getNextSamplePredictive(String filename, int inputNodes, int outputNodes, double minInput, double maxInput) {
		double[] inputFile = StdAudio.read(filename);
		// create a new input list (double[]) from the start of each sample
		double[][] output = new double[inputFile.length - inputNodes][inputNodes];
		// iterate over the array
		for (int i = 0; i < inputFile.length - inputNodes - 1; i++) {
			// input
			output[i] = Arrays.copyOfRange(inputFile, i, i + inputNodes);
		}
		return MatrixOps.normalise(output, minInput, maxInput);		
	}
	
	/**
	 * "Next Sample Predictive" refers to the net where it looks at the previous samples in 
	 * the original audio then tries to predict what the next one might be. This takes an audio
	 * file and re-makes it as an input list where each item in double[] is a double[] of 
	 * values of length inputNodes from the start of each sample
	 * @param inputFile
	 * @param inputNodes
	 * @param outputNodes
	 * @return
	 */
	public double[][] getNextSamplePredictive(double[] inputFile, int inputNodes, int outputNodes) {
		// create a new input list (double[]) from the start of each sample
		double[][] output = new double[inputFile.length - inputNodes][inputNodes];
		// iterate over the array
		for (int i = 0; i < inputFile.length - inputNodes - 1; i++) {
			// input
			output[i] = Arrays.copyOfRange(inputFile, i, i + inputNodes);
		}
		return output;		
	}
	
// Something doesn't work here *******************************************************************************************
// these methods combined are meant to act on an audio file several times over. I think its something to do with how
// the files are transposed. Need some serious bug fixing, possibly just delete the methods and start from scratch
// rather than trying to get away with editing previous methods.
	
	public double[][] getNextSamplePredictiveMulti(double[][] inputFile, int inputNodes, int outputNodes) {
		// TODO: there's something here, need to make it setup the data so that it is in chunks of outputNode samples
		// need to have another method to load in the sample, normalised
		// need to create a way of stitching the output back into an audio file
		
		// create a new input list (double[]) from the start of each sample
		double[][] output = new double[(inputFile[0].length - inputNodes) / inputNodes][outputNodes];
		// iterate over the array, taking a chunk every outputNode samples
		for (int i = 0; i < inputFile.length - inputNodes - 1; i += inputNodes) {
			// input
			output[i] = Arrays.copyOfRange(inputFile[0], i, i + inputNodes);
		}
		return output;		
	}
	
	/**
	 * takes an audio file that is chopped up into sections and returns one that is one 
	 * continuous stream at position [0] in the array
	 * @param audio
	 * @return
	 */
	public double[][] stitchAudioFile(double[][] audio) {
		// I'm having a bad day, that's why the below code is just terrible!
		
		ArrayList<Double> output = new ArrayList<>();
		for (int i = 0; i < audio.length; i++) {
			for (int j = 0; j < audio[0].length; j++) {
				output.add(audio[i][j]);
			}
		}
		Double[] audioOut = new Double[output.size()];
		audioOut = output.toArray(audioOut);
		
		double[][] audioOutMatrix = new double[1][audioOut.length];
		for (int i = 0; i < audioOut.length; i++) {
			audioOutMatrix[0][i] = audioOut[i];
		}
		return audioOutMatrix;
	}
// *****************************************************************************************************	
	
	public double[][] loadNormalisedAudio(String filename, double minInput, double maxInput) {
		double[][] inputFile = new double[1][];
		inputFile[0] = MatrixOps.normalise(StdAudio.read(filename), minInput, maxInput);
		return inputFile;
	}
	
	public void printDifferences(String filename1, String filename2, int sampleStart, int sampleEnd) {
		double[][] output = new double[3][];
		output[0] = Arrays.copyOfRange(StdAudio.read(filename1), sampleStart, sampleEnd);
		output[1] = Arrays.copyOfRange(StdAudio.read(filename2), sampleStart, sampleEnd);
		output[2] = new double[sampleEnd - sampleStart];
		for (int i = 0; i < output[0].length; i++) {
			output[2][i] = output[0][i] - output[1][i];
		}
		MatrixOps.printMatrix(output);
	}
	
	/**
	 * creates a feedback loop using a single output node
	 * @param net
	 * @param filename
	 * @param startSample
	 * @param outputLength
	 */
	public double[][] hallucinateSingle(NeuralNet net, String filename, int sampleStart, int outputLength, int inputNodes) {
		double[][] output = new double[outputLength][1]; // added to every cycle
		double[][] input = new double[1][];
		input[0] = Arrays.copyOfRange(StdAudio.read(filename), sampleStart, sampleStart + inputNodes);; // modified every cycle
		
		input[0] = MatrixOps.normalise(input[0], -1.0, 1.0);
		
		for (int i = 0; i < output.length; i++) {
			output[i][0] = net.runData(input)[0][0];
			
			// shuffle input and add the new output
			for (int j = 0; j < inputNodes - 1; j++) {
				input[0][j] = input[0][j + 1];
			}
			input[0][inputNodes - 1] = output[i][0];
		}
		return output;		
	}
	
	/**
	 * creates a feedback loop using multiple output nodes, the number of inputs should equal the number of outputs
	 * @param net
	 * @param filename
	 * @param sampleStart
	 * @param outputLength
	 * @param inputNodes
	 * @return
	 */
	public double[][] hallucinateMultiple(NeuralNet net, String filename, int sampleStart, int outputLength, int inputNodes) {
		int iterations = outputLength / inputNodes;
		double[][] output = new double[iterations * inputNodes][1]; // added to every cycle
		double[][] input = new double[1][];
		input[0] = Arrays.copyOfRange(StdAudio.read(filename), sampleStart, sampleStart + inputNodes);; // modified every cycle
		input[0] = MatrixOps.normalise(input[0], -1.0, 1.0);
		
		System.out.println("hallucinating...");
		for (int i = 0; i < iterations; i++) {
			for (int j = 0; j < inputNodes; j++) {
				output[(i * inputNodes) + j][0] = input[0][j];
			}
			input[0] = net.runData(input)[0];
		}
		System.out.println("done hallucinating");
		return output;		
	}

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}