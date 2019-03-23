package neuralNetwork2;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import supportClasses.OUTPUTNEURON;
import supportClasses.RandomNumbers;
import supportClasses.MathFunctions;

public class Neuron {

	public enum NEURON_TYPE {
		INPUT, OUTPUT, HIDDEN, BIAS, ERROR
	};

	private static final float LearnRateDecrease = 0.04f;
	private static boolean returnForAllOutNeurons = true;
	
	private NEURON_TYPE neuronType;
	private double stimuliIn;
	private double trainingReference;
	private double deltaError;
	private int calculatedForClockCycle;
	private double lastOutput = Double.NaN;
	private String name = "";
	private boolean iHaveBeenTrained = false;
	private LinkedList<neuronConnection> neuronInputList = new LinkedList<neuronConnection>();
	private LinkedList<neuronConnection> neuronOutputList = new LinkedList<neuronConnection>();
	

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param type
	 */
	public Neuron(String name, NEURON_TYPE type) {
		this.name = name;
		this.neuronType = type;
		resetMe();
	}

	/**
	 * Reset values.. todo.. why not constructor?
	 */
	public void resetMe() {
		stimuliIn = Float.NaN;
		calculatedForClockCycle = -999999;
		lastOutput = Double.NaN;
		trainingReference = Double.NaN;
		deltaError = 0.0f;
	}

	/**
	 * Link this neuron to another neuron, where I can get my input from
	 * 
	 * @param inputNeuron
	 */
	private void linkInputNeuron(neuronConnection nc) {
		neuronInputList.add(nc);
	}

	/**
	 * Link this neuron to another neuron, where I can send my output to
	 * 
	 * @param outputNeuron
	 */
	public void linkOutputNeuron(Neuron outputNeuron) {
		neuronConnection nc = new neuronConnection(this, outputNeuron);
		neuronOutputList.add(nc);
		/*
		 * Now tell this outputneuron about the new connection and that I am his
		 * input neuron where he'll get his input
		 */
		outputNeuron.linkInputNeuron(nc);
	}

	/**
	 * Calculate our own output.. Function will cause a recursive mass-call to
	 * all neurons. Each call to function is tracked by clockCycle as this
	 * function will get called several times per cycle..
	 */
	public double calculateOutput(int clockCycle, OUTPUTNEURON outNeuronInvolved, boolean forceOutput)
			throws Exception {

		float out = 0;

		if (neuronType == NEURON_TYPE.BIAS) {
			calculatedForClockCycle = clockCycle;
			lastOutput = 1f;
			return lastOutput;
		}
		if (neuronType == NEURON_TYPE.INPUT) {
			calculatedForClockCycle = clockCycle;
			if (iHaveBeenTrained == true) {
				lastOutput = stimuliIn;
			} else {
				lastOutput = Double.NaN;
			}
			return lastOutput;
		}

		if (neuronType == NEURON_TYPE.OUTPUT && Double.isNaN(trainingReference)) {
			lastOutput = Double.NaN;
			return lastOutput;
		}
		if (calculatedForClockCycle == clockCycle) {
			return lastOutput;
		}

		Iterator<neuronConnection> it = neuronInputList.iterator();
		synchronized (neuronInputList) {
			Collections.shuffle(neuronInputList);
		}

		double nextOut;

		while (it.hasNext()) {
			neuronConnection nextNeuronConnection = it.next();
			nextOut = nextNeuronConnection.getNeuronThatSends().calculateOutput(clockCycle, outNeuronInvolved,
					forceOutput);

			if (Double.isNaN(nextOut)) {
				continue;
			}
			if (forceOutput)
				out += nextOut * nextNeuronConnection.getWeight();
			else
				out += nextOut * nextNeuronConnection.getWeight(outNeuronInvolved);
		}
		lastOutput = MathFunctions.sigmoid(out);

		if (neuronType == NEURON_TYPE.OUTPUT) {
			// Im an output neuron. Then im responsible to set input stimuli to
			// potential other networks
			// below me..
			Iterator<neuronConnection> it2 = neuronOutputList.iterator();

			while (it2.hasNext()) {
				it2.next().getNeuronThatReceives().setStimuliInForInputNeuron(lastOutput, false);
			}
		}

		calculatedForClockCycle = clockCycle;
		return lastOutput;
	}

	/**
	 * Set stimuli in for input neurons Possible to set Float.NaN . That means
	 * it is a void stimuli, e.g. in a picture it is not black, but transparent,
	 * or in a chess game it would probably represent a free area..
	 * 
	 * @param stimuliIn
	 */
	public void setStimuliInForInputNeuron(double stimuliIn, boolean addNoise) throws Exception {

		if (this.neuronType == NEURON_TYPE.BIAS) {
			return;
		}

		if (this.neuronType == NEURON_TYPE.INPUT) {

			if(addNoise && Double.isNaN(stimuliIn)){
				this.stimuliIn = (double)RandomNumbers.getInstance().getDoubleZeroCentered(1);
			}else{
				this.stimuliIn = stimuliIn;
			}
			
			
			return;
		}
		throw new Exception("setStimuliIn used for none input neuron");
	}

	/**
	 * This is used to train the neuron. This is the training reference value
	 * 
	 * @param training
	 * @throws Exception
	 */
	public void setTrainingValue(double training) throws Exception {

		this.trainingReference = training;
	}

	public double getTrainingReference() {
		return trainingReference;
	}

	/**
	 * return previously calculated delta error
	 */
	public double getDeltaError() {
		return deltaError;
	}

	/**
	 * Calculate the delta error. Note that output neuronas that does not have
	 * trainingReference will get deltaError NaN
	 * 
	 * @throws Exception
	 */
	public void calculateDeltaValue() throws Exception {

		if (neuronType == NEURON_TYPE.OUTPUT) {
			// In this case we being the final output neuron layer, delta error
			// simply
			// the diff between training and calculated output.
			deltaError = lastOutput - trainingReference;
		} else {

			// Other neurons, calculate the deltaerror according to course...
			Iterator<neuronConnection> it = neuronOutputList.iterator();
			deltaError = 0.0f;
			double tmpErr;

			while (it.hasNext()) {
				neuronConnection next = it.next();
				tmpErr = next.getNeuronThatReceives().getDeltaError();

				if (Double.isNaN(tmpErr)) {
					continue;
				}

				deltaError += next.getWeight() * tmpErr;
			}
		}
	}

	/**
	 * Calulate new weigths. Look at the neurons output and get the delta from
	 * the receiveing neuron. Adjust the weight
	 */
	public void calcNewWeights(int learningCycle, OUTPUTNEURON outNeuronInvolved, double learnRateKonst)
			throws Exception {

		if (neuronType == NEURON_TYPE.OUTPUT) {
			return;
		}

		if (Double.isNaN(this.stimuliIn) && neuronType == NEURON_TYPE.INPUT) {
			return;
		}

		Iterator<neuronConnection> it = neuronOutputList.iterator();

		while (it.hasNext()) {
			neuronConnection next = it.next();
			double tmpErr = next.getNeuronThatReceives().getDeltaError();
			if (Double.isNaN(tmpErr)) {
				continue;
			}
			next.setWeight(MathFunctions.calcNewWeight(tmpErr, next.getWeight(), learnRateKonst), learningCycle,
					outNeuronInvolved);
		}
		iHaveBeenTrained = true;

	}

	/**
	 * get last calculated output (trigger) from neuron..
	 * 
	 * @return
	 */
	public double getLastOutput() {
		return lastOutput;
	}

	/**
	 * to string...
	 * 
	 * @return
	 */
	@Override
	public String toString() {

		String retString = "";

		Iterator<neuronConnection> it = neuronInputList.iterator();

		while (it.hasNext()) {
			neuronConnection nextNeuronConnection = it.next();
			retString = nextNeuronConnection.getWeight() + " " + retString;
		}
		return name + " " + neuronType + ": " + "out: " + lastOutput + " weights: " + retString;
	}

	public double getStimuliIn() {
		return stimuliIn;
	}

	public String getName() {
		return name;
	}

	/**
	 * Little helper that holds a neuron and a weight
	 * 
	 * @author Lenovo
	 *
	 */
	private class neuronConnection {
		private Neuron neuronThatSends;
		private Neuron neuronThatReceives;
		private double weight = (double) RandomNumbers.getInstance().getDoubleZeroCentered(5);
		private int trainedForCycle = -1;
		private float trainedCount = 1.0f;
		LinkedList<OUTPUTNEURON> involvedInOutNeuron = null;

		private neuronConnection(Neuron neuronThatSends, Neuron neuronThatReceives) {
			this.neuronThatSends = neuronThatSends;
			this.neuronThatReceives = neuronThatReceives;
			if(returnForAllOutNeurons == false){
				involvedInOutNeuron = new LinkedList<OUTPUTNEURON>();
			}
		}

		private void setWeight(double newWeight, int learningCycle) {

			if (trainedForCycle == -1) {
				trainedForCycle = learningCycle;
				this.weight = newWeight;
			} else {
				this.weight = this.weight + ((newWeight - this.weight) / trainedCount);
			}

			trainedCount += LearnRateDecrease;
			if (trainedCount > 1.5f) {
				// System.out.println(trainedCount);
			}

			trainedForCycle = learningCycle;
		}

		private void setWeight(double newWeight, int learningCycle, OUTPUTNEURON outNeuron) {
			
			if(returnForAllOutNeurons){
				setWeight(newWeight, learningCycle);
				return;
			}
			if (!doesOutputNeuronAlreadyExists(outNeuron)) {
				involvedInOutNeuron.add(outNeuron);
			}

			if (amInvolvedInOutNeuron(outNeuron)) {
				setWeight(newWeight, learningCycle);
			}
		}

		private boolean amInvolvedInOutNeuron(OUTPUTNEURON outNeuron) {
			Iterator<OUTPUTNEURON> it = involvedInOutNeuron.iterator();

			while (it.hasNext()) {
				if (isSameOutputNeuron(it.next(), outNeuron))
					return true;
			}
			return false;
		}

		private boolean isSameOutputNeuron(OUTPUTNEURON a, OUTPUTNEURON b) {
			if ((a.getX() == b.getX()) && (a.getY() == b.getY()))
				return true;
			else
				return false;
		}

		private boolean doesOutputNeuronAlreadyExists(OUTPUTNEURON n) {
			Iterator<OUTPUTNEURON> it = involvedInOutNeuron.iterator();

			while (it.hasNext()) {
				if (isSameOutputNeuron(it.next(), n))
					return true;
			}
			return false;
		}

		private double getWeight() {
			return weight;
		}

		private double getWeight(OUTPUTNEURON n) {

			if (returnForAllOutNeurons || amInvolvedInOutNeuron(n)) {
				return weight;
			} else {
				return 0f;
			}

		}

		private Neuron getNeuronThatSends() {
			return neuronThatSends;
		}

		private Neuron getNeuronThatReceives() {
			return neuronThatReceives;
		}
	}

}
