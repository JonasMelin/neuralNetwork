package gameEngine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import supportClasses.AnalyzisResult;
import supportClasses.OUTPUTNEURON;
import supportClasses.RandomNumbers;
import neuralNetwork2.NeuronNetwork;
import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.BoardOperations;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.GameEngine;

public class Neuron2GameEngine extends GameEngine{
	
	private NeuronNetwork reptileBrainNetwork;
	//private NeuronNetwork brainsNetworkWinningMoves;
	//private NeuronNetwork brainsNetworkBeforeWinningMoves;
	
	private final static int hiddenLayers = 2;
	
	private float myValue = 100f;
	private float opponentValue = -100f;
	private float Freevalue = Float.NaN;
	
	private int learningCycle = 0;

	public Neuron2GameEngine(PlayBoard gameBoard, PlayBoard coloringBoard, BoardOperations boardOperations,
			CHARACTER myCharacter) throws Exception{
		super(gameBoard, coloringBoard, boardOperations, myCharacter);
		System.out.println("New Game enging..");
		reptileBrainNetwork = new NeuronNetwork(gameBoard.getBoardXHeigt(), gameBoard.getBoardYWidth(), hiddenLayers);
		//brainsNetworkWinningMoves = new NeuronNetwork(gameBoard.getBoardXHeigt(), gameBoard.getBoardYWidth(), hiddenLayers);
		//NeuronNetwork.connectNetworks(reptileBrainNetwork, brainsNetworkWinningMoves);
		//brainsNetworkBeforeWinningMoves = new NeuronNetwork(gameBoard.getBoardXHeigt(), gameBoard.getBoardYWidth(), hiddenLayers);
		//NeuronNetwork.connectNetworks(reptileBrainNetwork, brainsNetworkBeforeWinningMoves);
	}
	
	public void learn(COORDINATE opponentLastMove, COORDINATE opponentSecondLastMove, boolean winningMove, boolean learnRules, OUTPUTNEURON reptileoutputNeuron) throws Exception{
		
		if(winningMove && !learnRules){
			// train this network with the second last move from the opponent. Remove his last move, and learn..
			CHARACTER winningChar = gameBoard.getCharacter(opponentLastMove);
			gameBoard.removeCharacter(opponentLastMove);
			setStimuliOffsetCenter(opponentSecondLastMove, true, true, false, false);
			reptileBrainNetwork.calcAllOutputValues();
			//brainsNetworkBeforeWinningMoves.train(10, 0);
			gameBoard.setCharacter(winningChar, opponentLastMove);
			//System.out.println("output from brains 2nd before network winning move(" + myCharacter.toString() +"): "+brainsNetworkWinningMoves.calcOutputValue(false));
		}
		
		setStimuliOffsetCenter(opponentLastMove, true, true, false, false);
		
		if(learnRules){	
			if(reptileoutputNeuron == null){
				reptileBrainNetwork.trainNextFreeOutput(500, learningCycle++);
			}else{
				reptileBrainNetwork.train(500, learningCycle++, reptileoutputNeuron, false, 0.0d);
			}
		}else if(winningMove){
			reptileBrainNetwork.calcAllOutputValues();
			reptileBrainNetwork.train(1, learningCycle++, new OUTPUTNEURON(10, 10), false, 0.5d);
			//brainsNetworkWinningMoves.train(10, 0);
			///System.out.println("output from brains network winning move(" + myCharacter.toString() +"): "+brainsNetworkWinningMoves.calcOutputValue(false));
		}
	}

	@Override
	public COORDINATE DoNextMove(List<COORDINATE> interestingPositions, COORDINATE opponentLastMove)
			throws Exception {

		//System.out.println("Do next move for " + this.myCharacter.toString());
		
		if(interestingPositions.size() == 0){
			if(gameBoard.isCoordinateFree(gameBoard.getCenterPosition())){
				return gameBoard.getCenterPosition();
			}
			else{ 
				throw new Exception("game board is full");
			}
		}
		
		AnalyzisResult myRes = getBestPosition(interestingPositions, true);
		AnalyzisResult opponentRes = getBestPosition(interestingPositions, false);

		//if(opponentRes.getHitCount() > myRes.getHitCount()){
		if(opponentRes.getScore() < myRes.getScore()){
			System.out.println("Blocking!!");
			return opponentRes.getCoord();
		}else{
			setStimuliOffsetCenter(myRes.getCoord(), false, false, false, false);
			reptileBrainNetwork.setStimuli(convertBoardCharacterToFloat(myCharacter, false, false), new OUTPUTNEURON(reptileBrainNetwork.getCenterNeuronX(), reptileBrainNetwork.getCenterNeuronY()), false);
			reptileBrainNetwork.calcAllOutputValues();
			reptileBrainNetwork.printOutputColors(coloringBoard);
			return myRes.getCoord();
		}
	}
	
	private void resetStimuliInNetwork(boolean addNoise) throws Exception{
		//First - reset the network input stimuli as free...
		for (int x = 0; x < gameBoard.getBoardXHeigt(); x++){
			for (int y = 0; y < gameBoard.getBoardYWidth(); y++){
				double val = convertBoardCharacterToFloat(CHARACTER.FREE, false, false);
				//if(addNoise){
				//	val = (double)RandomNumbers.getInstance().getDouble(6) - 3f;
				//}
				reptileBrainNetwork.setStimuli(val, new OUTPUTNEURON(x, y), addNoise);
			}
		}
	}
	
	private AnalyzisResult getBestPosition(List<COORDINATE> interestingPositions, boolean myPosition) throws Exception{

		Iterator<COORDINATE> it = interestingPositions.iterator();
		LinkedList<AnalyzisResult> analyzeResult = new LinkedList<AnalyzisResult>();
		
		while(it.hasNext()){
			COORDINATE next = it.next();
			if(myPosition){
				setStimuliOffsetCenter(next, false, false, false, false); //todo - must not invert
				reptileBrainNetwork.setStimuli(convertBoardCharacterToFloat(myCharacter, false, false), new OUTPUTNEURON(reptileBrainNetwork.getCenterNeuronX(), reptileBrainNetwork.getCenterNeuronY()), false);
			}else{
				setStimuliOffsetCenter(next, true, false, false, false); //todo - must not invert
				reptileBrainNetwork.setStimuli(convertBoardCharacterToFloat(myCharacter, false, false), new OUTPUTNEURON(reptileBrainNetwork.getCenterNeuronX(), reptileBrainNetwork.getCenterNeuronY()), false);				
			}

			AnalyzisResult res = reptileBrainNetwork.calcAllOutputValues();
			res.setCoord(next);
			analyzeResult.add(res);
			
			/*
			double brainsResult = brainsNetworkWinningMoves.calcOutputValue(false);
			if(supportClasses.MathFunctions.inRange(brainsResult, NeuronNetwork.getTrainvalue(), 0.003f)){
				//System.out.println("The brain recognized a pattern (out "+brainsResult + ") at " + next.toString());
				res = new AnalyzisResult(next, brainsResult, 100);
				analyzeResult.add(res);
			}
			
			double brainsBeforeWinResult = brainsNetworkBeforeWinningMoves.calcOutputValue(false);
			if(supportClasses.MathFunctions.inRange(brainsBeforeWinResult, NeuronNetwork.getTrainvalue(), 0.003f)){
				//System.out.println("The brain recognized a pattern (out "+brainsResult + ") at " + next.toString());
				res = new AnalyzisResult(next, brainsBeforeWinResult, 99);
				analyzeResult.add(res);
			}
			*/
		}
		return analyzeTheResult2(analyzeResult);
	}
	
	private AnalyzisResult analyzeTheResult2(LinkedList<AnalyzisResult> list){
		
		Iterator<AnalyzisResult> it = list.iterator();
		double smallestDiff = Double.MIN_VALUE;
		AnalyzisResult bestAnalyzis = null;
		
		while(it.hasNext()){
			AnalyzisResult next = it.next();
			
			if(next.getScore() > smallestDiff){
				smallestDiff = next.getScore();
				bestAnalyzis = next;
			}
		}
		
		return bestAnalyzis;
	}
			
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private AnalyzisResult analyzeTheResult(LinkedList<AnalyzisResult> list){
		
		Iterator<AnalyzisResult> it = list.iterator();
		double smallestDiff = Double.MAX_VALUE;
		int maxHitCount = -1;
		AnalyzisResult bestAnalyzis = null;
		
		while(it.hasNext()){
			AnalyzisResult next = it.next();
			
			if(next.getHitCount() > maxHitCount){
				maxHitCount = next.getHitCount();
				smallestDiff = next.getSmallestDiff();
				bestAnalyzis = next;
			}else{
				if(next.getHitCount() == maxHitCount){
					if(next.getSmallestDiff() < smallestDiff){
						maxHitCount = next.getHitCount();
						smallestDiff = next.getSmallestDiff();
						bestAnalyzis = next;
					}
				}
			}
		}
		
		/*
		if(bestAnalyzis.getHitCount() == 100){
			System.out.println(" --- Using brain to make a move!! " + this.myCharacter);
		}
		if(bestAnalyzis.getHitCount() == 99){
			System.out.println(" --- Using brain ONE BEFORE WIN to make a move!! " + this.myCharacter);
		}
		*/
		
		return bestAnalyzis;
	}
	
	/**
	 * Make the neural network "look" at a particular coordinate in the game board
	 * @throws Exception
	 */
	private void setStimuliOffsetCenter(COORDINATE centerCoord, boolean invert, boolean clearMine, boolean clearOpponent, boolean addNoise) throws Exception{

		int offsetX = reptileBrainNetwork.getCenterNeuronX() - centerCoord.getX();
		int offsetY = reptileBrainNetwork.getCenterNeuronY() - centerCoord.getY();
		
		resetStimuliInNetwork(addNoise);
		
		// Now set the stimuli in the center of the network, centered around the specific coordinate
		
		for (int x = 0; x < gameBoard.getBoardXHeigt(); x++){
			for (int y = 0; y < gameBoard.getBoardYWidth(); y++){
				try{
					if(invert){
						reptileBrainNetwork.setStimuli(
								invertOponentCharacter(
										gameBoard.getCharacter(
												new COORDINATE(
														x, y, gameBoard)
												),clearMine, clearOpponent
										), new OUTPUTNEURON(x + offsetX, y + offsetY), addNoise);
					}
					else
					{
						reptileBrainNetwork.setStimuli(
								convertBoardCharacterToFloat(
										gameBoard.getCharacter(
												new COORDINATE(
														x, y, gameBoard)
												),clearMine, clearOpponent
										), new OUTPUTNEURON(x + offsetX, y + offsetY), addNoise);
					}
				}catch (Exception ex){}
			}
		}
	}

	/**
	 * takes a character and returns the inverted value.. Unless "clearMine" is set, then the character that 
	 * is mine, before it is converted, is set as free.  same for clearOpponent.
	 * @param character
	 * @param clearMine
	 * @param clearOpponent
	 * @return
	 */
	private float invertOponentCharacter(CHARACTER character, boolean clearMine, boolean clearOpponent){

		
		if (character == CHARACTER.FREE)
			return Freevalue;
		
		if(character == myCharacter){
			if(clearMine){
				return Freevalue;
			}
			else{
				return opponentValue;
			}
		}else{
			if(clearOpponent){
				return Freevalue;
			}
			else{
				return myValue;
			}
		}
	}
	
	private float convertBoardCharacterToFloat(CHARACTER character, boolean clearMine, boolean clearOpponent) throws Exception{
		
		if (character == CHARACTER.FREE)
			return Freevalue;
		
		if (myCharacter == character){
			if(clearMine)
				return Freevalue;
			else
				return myValue;
		}else{
			if(clearOpponent)
				return Freevalue;
			else
				return opponentValue;
		}
	}
	
	public void printReptileOutput(COORDINATE centerCoord) throws Exception{
		setStimuliOffsetCenter(centerCoord, false, false, false, false);
		reptileBrainNetwork.calcAllOutputValues();
		reptileBrainNetwork.printOutputColors(coloringBoard);
	}

	public int trainWithNoise(COORDINATE centercoord, OUTPUTNEURON reptileoutputNeuron, int numberOfTimes, boolean noise) throws Exception{
		
		for(int a = 0; a < numberOfTimes; a++){
			setStimuliOffsetCenter(centercoord, false, false, false, noise);	
			reptileBrainNetwork.train(1, learningCycle++, reptileoutputNeuron, false, 0.5d);
			
		}
		return 0;
	}
}
