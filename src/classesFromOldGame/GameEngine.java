package classesFromOldGame;

import java.util.List;

import classesFromOldGame.COORDINATE;
import supportClasses.OUTPUTNEURON;
import classesFromOldGame.PlayBoard;
import classesFromOldGame.BoardOperations;
import classesFromOldGame.PlayBoard.CHARACTER;

public abstract class GameEngine {
	
	protected PlayBoard gameBoard;
	protected PlayBoard coloringBoard;
	protected BoardOperations boardOperations;

	protected PlayBoard.CHARACTER myCharacter;
	protected PlayBoard.CHARACTER opponentCharacter;

	/**
	 * 
	 * @param gameBoard
	 * @param boardOperations
	 * @param myCharacter
	 * @param thePlayer
	 */
	public  GameEngine(PlayBoard gameBoard, PlayBoard coloringBoard, BoardOperations boardOperations, PlayBoard.CHARACTER myCharacter){
		this.gameBoard = gameBoard;
		this.coloringBoard = coloringBoard;
		this.boardOperations = boardOperations;
		this.myCharacter = myCharacter;

		if(myCharacter == CHARACTER.O){
			opponentCharacter = CHARACTER.X;
		}
		else{
			opponentCharacter = CHARACTER.O;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public PlayBoard.CHARACTER getMyCharacter(){
		return myCharacter;
	}
	

	public PlayBoard.CHARACTER getOpponentCharacter() {
		return opponentCharacter;
	}
	
	/**
	 * 
	 * @param interestingPositions
	 * @return
	 * @throws Exception
	 */
	public abstract COORDINATE DoNextMove(List<COORDINATE> interestingPositions, 
			COORDINATE opponentLastMove) throws Exception;
	
	/**
	 * 
	 * @param opponentLastMove
	 * @param opponentSecondLastMove
	 * @param winningMove  True if winning move
	 * @param learnRules  true if we are training the reptile brain to see patterns
	 * @param reptileoutputNeuron   Leave as null to get next free. Else points to what reptile out to train
	 * @throws Exception
	 */
	public abstract void learn(
			COORDINATE opponentLastMove, 
			COORDINATE opponentSecondLastMove, 
			boolean winningMove, 
			boolean learnRules, 
			OUTPUTNEURON reptileoutputNeuron) throws Exception;

	public void setGameBoard(PlayBoard gameBoard) {
		this.gameBoard = gameBoard;
	}
	
	public void setColoringBoard(PlayBoard coloringBoard) {
		this.coloringBoard = coloringBoard;
	}

	public void setBoardOperations(BoardOperations boardOperations) {
		this.boardOperations = boardOperations;
	}

	public void setMyCharacter(PlayBoard.CHARACTER myCharacter) {
		this.myCharacter = myCharacter;
		
		if(myCharacter == CHARACTER.O){
			opponentCharacter = CHARACTER.X;
		}
		else{
			opponentCharacter = CHARACTER.O;
		}
	}
}
