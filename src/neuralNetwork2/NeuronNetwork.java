package neuralNetwork2;

import java.util.Iterator;
import java.util.LinkedList;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.COORDINATE;
import neuralNetwork2.Neuron.NEURON_TYPE;
import supportClasses.AnalyzisResult;
import supportClasses.MathFunctions;
import supportClasses.OUTPUTNEURON;

public class NeuronNetwork implements Runnable {

	public final static double trainValue = 0.5f;
	private final static double learnRateKonstStart = 0.014f;//0.001f;
	private final static double learnRateBoostStart = 0.0000000001f;//0.014f;
	private final static double learnRateBoostDecrease = 0.00000f;// 0.00001f;

	private final static double trainingStopDiff = 0.01f;
	private final static int autoMaxTrainAttempts = 1000;
	private int clockCycle = 0;

	private int nwSizeinputX;
	private int nwSizeinputY;
	private int nwSizeZ;

	private int centerOutputNeuronX = 0;
	private int nextOutNeuronToTrain = 0;

	private int centerOutputNeuronY = 0;

	LinkedList<Double> outputList = null;

	Neuron[][][] network;

	public NeuronNetwork(int inputNeuronsX, int inputNeuronsY, int hiddenLayers) throws Exception {

		if (inputNeuronsX < 1 || inputNeuronsY < 1 || hiddenLayers < 1)
			throw new Exception("Bad size of neuron network");

		this.nwSizeinputX = inputNeuronsX;
		this.nwSizeinputY = inputNeuronsY;
		this.nwSizeZ = hiddenLayers + 2;
		this.centerOutputNeuronX = inputNeuronsX / 2;
		this.centerOutputNeuronY = inputNeuronsY / 2;
		network = new Neuron[inputNeuronsX][inputNeuronsY][nwSizeZ];
		createNeurons();
		linkNeurons();
	}

	private void createNeurons() {

		NEURON_TYPE type = NEURON_TYPE.ERROR;

		for (int x = 0; x < nwSizeinputX; x++) {
			for (int y = 0; y < nwSizeinputY; y++) {
				for (int z = 0; z < nwSizeZ; z++) {

					if (z == 0) /** First Z layer means input **/
					{
						type = NEURON_TYPE.INPUT;
						if ((x == (nwSizeinputX - 1)) && (y == (nwSizeinputY
								- 1)))/**
										 * Last neuron in top layer is bias neuron
										 */
						{
							type = NEURON_TYPE.BIAS;
						}
					} else if (z == (nwSizeZ
							- 1)) /** Last Z layer means output **/
					{
						type = NEURON_TYPE.OUTPUT;
					} else {
						type = Neuron.NEURON_TYPE.HIDDEN;
					}

					network[x][y][z] = new Neuron("" + x + ":" + y + ":" + z + ":", type);
				}
			}
		}
	}

	private void linkNeurons() {

		for (int x = 0; x < nwSizeinputX; x++) {
			for (int y = 0; y < nwSizeinputY; y++) {
				for (int z = 0; z < nwSizeZ; z++) {

					if (z != (nwSizeZ
							- 1)) { /**
									 * Last Z layer means output and those
									 * neurons shall not be linked
									 **/
						for (int m = 0; m < nwSizeinputX; m++) {
							for (int n = 0; n < nwSizeinputY; n++) {
								/**
								 * Now take this neuron and link it to all
								 * neurons in the next layer..
								 */
								network[x][y][z].linkOutputNeuron(network[m][n][z + 1]);

							}
						}
					}
				}
			}
		}
	}

	public void setStimuli(double stimuliIn, OUTPUTNEURON outNeuron, boolean addNoise) throws Exception {
		if (!rangeCheckXY(outNeuron.getX(), outNeuron.getY()))
			throw new Exception("bad range for addressing in neuron network");

		network[outNeuron.getX()][outNeuron.getY()][0].setStimuliInForInputNeuron(stimuliIn, addNoise);
	}

	/**
	 * Set a training value for a network for a specific output neuron
	 * 
	 * @param training
	 * @param X
	 * @param Y
	 * @throws Exception
	 */
	public void setTrainingValue(double training, int X, int Y) throws Exception {
		if (!rangeCheckXY(X, Y))
			throw new Exception("bad range (training) for addressing in neuron network");

		network[X][Y][nwSizeZ - 1].setTrainingValue(training);
	}

	/**
	 * Calculate output for a specific output neuron
	 * 
	 * @param X
	 * @param Y
	 * @return
	 * @throws Exception
	 */
	public double calcOutputValue(OUTPUTNEURON outNeuron, boolean forceOutput) throws Exception {
		if (!rangeCheckXY(outNeuron.getX(), outNeuron.getY()))
			throw new Exception("bad range (getoutput) for addressing in neuron network");

		return network[outNeuron.getX()][outNeuron.getY()][nwSizeZ - 1].calculateOutput(clockCycle++, outNeuron,
				forceOutput);
	}

	/**
	 * Calculate the value for the center output neuron
	 * 
	 * @return
	 * @throws Exception
	 */
	public double calcOutputValue(boolean forceOutput) throws Exception {

		return calcOutputValue(new OUTPUTNEURON(centerOutputNeuronX, centerOutputNeuronY), forceOutput);
	}

	public AnalyzisResult calcAllOutputValuesMultiThread() throws Exception {

		outputList = new LinkedList<Double>();
		Thread t1 = new Thread(this);
		Thread t2 = new Thread(this);
		clockCycle++;

		t1.start();
		t2.start();
		t1.join();
		t2.join();

		AnalyzisResult a = getNumberOfHitsFromOutput(outputList);
		outputList = null;
		return a;
	}

	public void run() {
		try {
			// calcAllOutputValues2();
		} catch (Exception ex) {
		}
	}

	/**
	 * 
	 */
	public AnalyzisResult calcAllOutputValues() throws Exception {

		OUTPUTNEURON outneuron = new OUTPUTNEURON(0, 0);

		// if(outputList == null){
		outputList = new LinkedList<Double>();
		// }

		for (int x = 0; x < nwSizeinputX; x++) {
			for (int y = 0; y < nwSizeinputY; y++) {
				outneuron.setX(x);
				outneuron.setY(y);

				double tmpRes = network[x][y][nwSizeZ - 1].calculateOutput(clockCycle++, outneuron, false);
				if (Double.isNaN(tmpRes))
					continue;
				synchronized (outputList) {
					outputList.add(tmpRes);
				}
			}
		}
		return getNumberOfHitsFromOutput(outputList);
	}

	private AnalyzisResult getNumberOfHitsFromOutput(LinkedList<Double> list) {

		Iterator<Double> it = list.iterator();
		int hitCount = 0;
		double smallestDiff = Double.MAX_VALUE;
		double totalDiff = 0;

		while (it.hasNext()) {
			double nextValue = it.next();
			if (MathFunctions.inRange(nextValue, trainValue, trainingStopDiff * 1.05f)) {
				hitCount++;
				double diff = Math.abs(nextValue - trainValue);
				totalDiff += diff;
				if (smallestDiff > diff)
					smallestDiff = diff;
			}
		}
		AnalyzisResult ret = new AnalyzisResult(null, smallestDiff, hitCount);
		ret.setScore((float)totalDiff);
		return ret;
	}

	/**
	 * Train the network once. Note: Set stimuli and train value first!
	 * 
	 * @throws Exception
	 */
	private void trainNetwork(int learningCycle, OUTPUTNEURON outNeuronInvolved, double learnRateBoost)
			throws Exception {
		for (int z = (nwSizeZ - 1); z >= 0; z--) {
			for (int x = 0; x < nwSizeinputX; x++) {
				for (int y = 0; y < nwSizeinputY; y++) {
					network[x][y][z].calculateDeltaValue();
				}
			}
		}
		for (int z = (nwSizeZ - 2); z >= 0; z--) {
			for (int x = 0; x < nwSizeinputX; x++) {
				for (int y = 0; y < nwSizeinputY; y++) {
					network[x][y][z].calcNewWeights(learningCycle, outNeuronInvolved,
							learnRateKonstStart + learnRateBoost);
				}
			}
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void train(int learningCycle) throws Exception {
		train(autoMaxTrainAttempts, learningCycle, new OUTPUTNEURON(centerOutputNeuronX, centerOutputNeuronY), false,
				0.0d);
	}

	/**
	 * 
	 * @param maxTrainAttempts
	 * @throws Exception
	 */
	public void train(int maxTrainAttempts, int learningCycle) throws Exception {
		train(maxTrainAttempts, learningCycle, new OUTPUTNEURON(centerOutputNeuronX, centerOutputNeuronY), false, 0.0d);
	}

	public void trainNextFreeOutput(int maxTrainAttempts, int learningCycle) throws Exception {
		int X = nextOutNeuronToTrain % nwSizeinputX;
		int Y = nextOutNeuronToTrain / nwSizeinputY;
		nextOutNeuronToTrain++;

		if (!rangeCheckXY(X, Y))
			throw new Exception("trainNextFreeOutput. All neurons trained...");

		System.out.println("training neuron " + X + " " + Y);

		train(maxTrainAttempts, learningCycle, new OUTPUTNEURON(X, Y), false, 0.0d);

	}

	/**
	 * train the network.
	 * 
	 * @param maxTrainAttempts
	 *            max train attempts until stop. 1 is also ok to just go once
	 * @param refNeuronX
	 *            the output neuron to check output for training
	 * @param refNeuronY
	 * @throws Exception
	 */
	public int train(int maxTrainAttempts, int learningCycle, OUTPUTNEURON outNeuronInvolved, boolean useOwnTrainValue,
			double ownTrainValue) throws Exception {

		boolean train_once = false;
		double lastOut = 0.0f;
		double learnRateBoost = learnRateBoostStart;
		double trainValue = NeuronNetwork.trainValue;

		rangeCheckXY(outNeuronInvolved.getX(), outNeuronInvolved.getY());

		if (maxTrainAttempts == 1)
			train_once = true;

		if (useOwnTrainValue)
			trainValue = ownTrainValue;

		setTrainingValue(trainValue, outNeuronInvolved.getX(), outNeuronInvolved.getY());
		lastOut = calcOutputValue(outNeuronInvolved, true);

		for (int a = 0; a < maxTrainAttempts; a++) {

			setTrainingValue(trainValue, outNeuronInvolved.getX(), outNeuronInvolved.getY());
			trainNetwork(learningCycle, outNeuronInvolved, learnRateBoost);
			learnRateBoost -= learnRateBoostDecrease;

			lastOut = calcOutputValue(outNeuronInvolved, false);

			if (MathFunctions.inRange(trainValue, lastOut, trainingStopDiff)) {
				//System.out.println("done training after " + a + " attempts. Output: " + lastOut);
				return 0;
			}
		}
		if (!train_once) {
			System.out.println("Failed training! out after trainining: " + lastOut);
		}
		return -1;
	}

	public boolean isStimuliMatch() throws Exception {
		calcOutputValue(new OUTPUTNEURON(centerOutputNeuronX, centerOutputNeuronY), false);
		// System.out.println("diff: "+ (calcout - trainValue));
		return MathFunctions.inRange(NeuronNetwork.trainValue,
				calcOutputValue(new OUTPUTNEURON(centerOutputNeuronX, centerOutputNeuronY), false), trainingStopDiff);
	}

	public int getCenterNeuronX() {
		return centerOutputNeuronX;
	}

	public int getCenterNeuronY() {
		return centerOutputNeuronY;
	}

	public static double getTrainvalue() {
		return trainValue;
	}

	private boolean rangeCheckXY(int X, int Y) {
		if ((X >= nwSizeinputX || X < 0) || (Y >= nwSizeinputY || Y < 0)) {
			return false;
		} else {
			return true;
		}
	}

	public void printInputLayerInput() {

		String str;
		System.out.println("--- Network input: ---");

		for (int x = 0; x < nwSizeinputX; x++) {
			str = "x: " + x + " ";

			for (int y = 0; y < nwSizeinputY; y++) {

				str = str + " y: " + y + " " + network[x][y][0].getStimuliIn();
			}
			System.out.println(str);
		}

	}

	@Override
	public String toString() {
		for (int z = 0; z < nwSizeZ; z++) {
			for (int x = 0; x < nwSizeinputX; x++) {
				for (int y = 0; y < nwSizeinputY; y++) {

					System.out.println(network[x][y][z].toString());
				}
			}
		}
		return super.toString();
	}

	/**
	 * 
	 * @param inputNetwork
	 * @param outputNetwork
	 */
	public static void connectNetworks(NeuronNetwork inputNetwork, NeuronNetwork outputNetwork) {
		for (int x = 0; (x < inputNetwork.nwSizeinputX) && (x < outputNetwork.nwSizeinputX); x++) {
			for (int y = 0; (y < inputNetwork.nwSizeinputY) && (y < outputNetwork.nwSizeinputY); y++) {
				inputNetwork.network[x][y][inputNetwork.nwSizeZ - 1].linkOutputNeuron(outputNetwork.network[x][y][0]);
			}
		}
	}

	public void printOutputColors(PlayBoard coloringBoard) throws Exception {
		for (int x = 0; x < nwSizeinputX; x++) {
			for (int y = 0; y < nwSizeinputY; y++) {
				//System.out.println(network[x][y][nwSizeZ - 1].toString());
				coloringBoard.setColor(network[x][y][nwSizeZ - 1].getLastOutput(), new COORDINATE(x, y, coloringBoard));
			}
		}

	}

}
