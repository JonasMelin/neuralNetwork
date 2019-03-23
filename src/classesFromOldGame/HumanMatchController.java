package classesFromOldGame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import classesFromOldGame.GameEngine;
import classesFromOldGame.Player;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.COORDINATE;

public class HumanMatchController implements MouseListener{
	
	private LinkedList<Player> computerPlayer1;
	private int scoreComputer = 0;
	private int scoreHuman = 0;
	private DrawBoard drawBoard;
	private PlayBoard theBoard;
	private BoardOperations boardOperations;
	private GameEngine gameEngineO;
	private final CHARACTER COMPUTERCHARACTER = CHARACTER.O;
	public enum STATE {LEARN,PLAY,QUIT};
	private STATE state = STATE.PLAY;

	public HumanMatchController(LinkedList<Player> computerPlayer1, DrawBoard drawBoard){
		this.computerPlayer1 = computerPlayer1;
		this.drawBoard = drawBoard;
		this.drawBoard.registerMouseListener(this);
		
	}
	
	public void terminate(){
		this.drawBoard.unregisterListerners(this, null);
		theBoard.resetBoard();
		drawBoard.printBoard();
	}
	
	public void setState(STATE state){
		this.state = state;
		theBoard.resetBoard();
		drawBoard.printBoard();
		if(state ==STATE.QUIT){
			drawBoard.unregisterListerners(this, null);
		}
	}
	
	public void startGame() throws Exception{
		theBoard = drawBoard.getTheBoardToDraw();
		
		if(boardOperations == null)
			boardOperations = new BoardOperations(theBoard);
		
		Iterator<Player> it = computerPlayer1.iterator();
		while(it.hasNext()){
			gameEngineO = it.next().getAndInitGameEngine(theBoard, null, boardOperations, COMPUTERCHARACTER);
		}
		if(gameEngineO == null){
			System.out.println("provide one player if you want to play with him. More players if the shall learn..");
			return;
		}
		theBoard.resetBoard();
		drawBoard.printBoard();
		System.out.println("Human game started. Please click in GUI to make your next move...");
	}
	
	private boolean lookForWinner() throws Exception{
		CHARACTER winner = boardOperations.lookForWinner();

		if(winner == CHARACTER.FREE)
			return false;
		
		if(winner == CHARACTER.X){
			System.out.print("YOU WON!!! ");
			scoreHuman++;
			printScore();
		}else if(winner == COMPUTERCHARACTER){
			System.out.print("Computer Won!");
			scoreComputer++;
			printScore();
		}
		startGame();
		return true;
	}
	
	private void printScore(){
		System.out.println("Computer: " + scoreComputer + ". You: " + scoreHuman);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		try{
			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), theBoard);
			if(!theBoard.isCoordinateFree(coord)){
				return;
			}
			theBoard.setCharacter(CHARACTER.X, coord);
			drawBoard.printBoard();
			if((state == STATE.PLAY) && lookForWinner() ){
				gameEngineO.DoNextMove(boardOperations.getInterestingFreePositions(), coord);
				return;
			}
			
			if(state == STATE.PLAY){
				coord = gameEngineO.DoNextMove(boardOperations.getInterestingFreePositions(), coord);
				theBoard.setCharacter(CHARACTER.O, coord);
				drawBoard.printBoard();
				lookForWinner();
			} 
			if(state == STATE.LEARN){
				Iterator<Player> it = computerPlayer1.iterator();
				while(it.hasNext()){
					it.next().getAndInitGameEngine(theBoard, null, boardOperations, COMPUTERCHARACTER).learn(coord, null, false, true, null);
				}
			}
		}
		catch (Exception ex){}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	
}
