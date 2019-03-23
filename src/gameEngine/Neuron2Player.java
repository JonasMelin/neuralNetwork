package gameEngine;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.BoardOperations;
import classesFromOldGame.GameEngine;
import classesFromOldGame.Player;

public class Neuron2Player extends Player{
	
	private GameEngine gameEngine = null;
	
	public Neuron2Player(String name) {
		super(name);
	}

	

	@Override
	public Player mate(Player otherPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameEngine getAndInitGameEngine(
			PlayBoard theBoard,
			PlayBoard coloringBoard,
			BoardOperations boardOperations, 
			CHARACTER myCharacter) throws Exception{
		if(gameEngine == null){
			gameEngine = new Neuron2GameEngine(theBoard, coloringBoard, boardOperations, myCharacter);
		}else
		{
			gameEngine.setBoardOperations(boardOperations);
			gameEngine.setGameBoard(theBoard);
			gameEngine.setColoringBoard(coloringBoard);
			gameEngine.setMyCharacter(myCharacter);
		}
		gameEngineInitOk = true;
		return gameEngine;
	}
	
	public GameEngine getGameEngine() throws Exception{
		if(!gameEngineInitOk){
			System.out.println("internal error. dont call getGameEngine without init first..");
			throw new Exception("getGameEngine without initializing it first...");
		}
		return gameEngine;
	}
}
