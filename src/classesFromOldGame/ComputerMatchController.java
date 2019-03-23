package classesFromOldGame;

import java.util.LinkedList;
import java.util.List;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.GameEngine;
import classesFromOldGame.Player;
import classesFromOldGame.PlayBoard.CHARACTER;

public class ComputerMatchController {
	private PlayBoard theBoard = null;// = new Board();
	private BoardOperations boardOperations = null;
	private GameEngine gameEngineO;
	private GameEngine gameEngineX;
	private Player playerO;
	private Player playerX;
	private List<COORDINATE> busyPositions = new LinkedList<COORDINATE>();
	DrawBoard drawBoard = null;
	
	public ComputerMatchController(DrawBoard drawBoard, PlayBoard colorBoardX, PlayBoard colorBoardO, Player playerO, Player playerX) throws Exception{
		this.drawBoard = drawBoard;
		this.theBoard = drawBoard.getTheBoardToDraw();
		this.boardOperations = new BoardOperations(theBoard);
		this.playerO = playerO;
		this.playerX = playerX;
		gameEngineO = playerO.getAndInitGameEngine(theBoard, colorBoardO, boardOperations, PlayBoard.CHARACTER.O);
		gameEngineX = playerX.getAndInitGameEngine(theBoard, colorBoardX, boardOperations, PlayBoard.CHARACTER.X);
	}
	
	public void runGame(PlayBoard.CHARACTER startingPlayer){
		GameEngine nextGameEngine;
		COORDINATE nextCoordinate = null;
		COORDINATE XSecondLastCoord = null;
		COORDINATE OSecondLastCoord = null;
		
		if(startingPlayer == PlayBoard.CHARACTER.X){
			nextGameEngine = gameEngineX;
		}
		else{
			nextGameEngine = gameEngineO;
		}

		try{
			for(;;){
				if(drawBoard != null){
					drawBoard.printBoard();
				}
				nextCoordinate = nextGameEngine.DoNextMove(boardOperations.getInterestingFreePositions(), nextCoordinate);
				busyPositions.add(nextCoordinate);
				theBoard.setCharacter(nextGameEngine.getMyCharacter(), nextCoordinate);
				CHARACTER winner = boardOperations.lookForWinner();

				if(winner == CHARACTER.X){
					playerX.SumUpAndResetAfterGame(true);
					playerO.SumUpAndResetAfterGame(false);
					if(drawBoard != null){
						drawBoard.printBoard();
					}
					gameEngineO.learn(nextCoordinate, XSecondLastCoord, true, false, null);
					System.out.println("WINNER: "+winner);
					return;
				}
				else if(winner == CHARACTER.O){
					playerX.SumUpAndResetAfterGame(false);
					playerO.SumUpAndResetAfterGame(true);
					if(drawBoard != null){
						drawBoard.printBoard();
					}
					gameEngineX.learn(nextCoordinate, OSecondLastCoord, true, false, null);
					System.out.println("WINNER: "+winner);
					return;
				}
				
				if(nextGameEngine == gameEngineX){
					nextGameEngine = gameEngineO;
					XSecondLastCoord = nextCoordinate;
				}
				else{
					nextGameEngine = gameEngineX;
					OSecondLastCoord = nextCoordinate;
				}
			}
		}catch (Exception ex){
			playerX.SumUpAndResetAfterGame(false);
			playerO.SumUpAndResetAfterGame(false);
			System.out.println("EXCEPTION " + ex.toString());
			return;
		}
	}
}
