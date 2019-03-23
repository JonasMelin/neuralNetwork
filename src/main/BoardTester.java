package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.PlayBoard.MARKER;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.DrawBoard;

public class BoardTester implements MouseListener{

	private PlayBoard mainBoard = new PlayBoard("mainboardTester");
	private PlayBoard coloringBoard = new PlayBoard(250,250, "coloringBoardTester");
	private PlayBoard coloringBoard2 = new PlayBoard(250,20, "coloringBoardTester");
	private DrawBoard drawBoard = new DrawBoard(mainBoard);
	
	public BoardTester(){
		drawBoard.registerMouseListener(this);
	}

	private void sleep(int millis){
		try{Thread.sleep(millis);}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void run(boolean quick) throws Exception{
		drawBoard.addBoard(coloringBoard);
		drawBoard.addBoard(coloringBoard2);
		
		
		coloringBoard.setColor(0.0, new COORDINATE(6, 0, coloringBoard));
		coloringBoard.setColor(0.1, new COORDINATE(6, 1, coloringBoard));
		coloringBoard.setColor(0.2, new COORDINATE(6, 2, coloringBoard));
		coloringBoard.setColor(0.3, new COORDINATE(6, 3, coloringBoard));
		coloringBoard.setColor(0.4, new COORDINATE(6, 4, coloringBoard));
		coloringBoard.setColor(0.5, new COORDINATE(6, 5, coloringBoard));
		coloringBoard.setColor(0.6, new COORDINATE(6, 6, coloringBoard));
		coloringBoard.setColor(0.7, new COORDINATE(6, 7, coloringBoard));
		coloringBoard.setColor(0.8, new COORDINATE(6, 8, coloringBoard));
		coloringBoard.setColor(0.9, new COORDINATE(6, 9, coloringBoard));
		coloringBoard.setColor(1.0, new COORDINATE(6, 10, coloringBoard));
		
		coloringBoard2.setColor(0.0, new COORDINATE(6, 0, coloringBoard));
		coloringBoard2.setColor(0.1, new COORDINATE(6, 1, coloringBoard));
		coloringBoard2.setColor(0.2, new COORDINATE(2, 2, coloringBoard));
		coloringBoard2.setColor(0.3, new COORDINATE(4, 3, coloringBoard));
		coloringBoard2.setColor(0.4, new COORDINATE(6, 4, coloringBoard));
		coloringBoard2.setColor(0.5, new COORDINATE(9, 5, coloringBoard));
		coloringBoard2.setColor(0.6, new COORDINATE(6, 6, coloringBoard));
		coloringBoard2.setColor(0.1, new COORDINATE(6, 7, coloringBoard));
		coloringBoard2.setColor(0.8, new COORDINATE(7, 8, coloringBoard));
		coloringBoard2.setColor(0.9, new COORDINATE(5, 9, coloringBoard));
		coloringBoard2.setColor(1.0, new COORDINATE(6, 10, coloringBoard));
		
		drawBoard.printBoard();
		this.sleep(100);
		mainBoard.setCharacter(CHARACTER.X, new COORDINATE(1, 1, mainBoard));
		mainBoard.setMarker(MARKER.RED, new COORDINATE(2, 2, mainBoard));
		coloringBoard.setCharacter(CHARACTER.X, new COORDINATE(5, 1, coloringBoard));
		coloringBoard.setMarker(MARKER.GREEN, new COORDINATE(6, 2, coloringBoard));
		coloringBoard2.setCharacter(CHARACTER.X, new COORDINATE(5, 1, coloringBoard));
		coloringBoard2.setMarker(MARKER.YELLOW, new COORDINATE(6, 2, coloringBoard));
		drawBoard.printBoard();
		this.sleep(100);
		mainBoard.setCharacter(CHARACTER.O, new COORDINATE(1, 2, mainBoard));
		mainBoard.setCharacter(CHARACTER.X, new COORDINATE(2, 2, mainBoard));
		mainBoard.setCharacter(CHARACTER.O, new COORDINATE(3, 3, mainBoard));
		drawBoard.printBoard();
		this.sleep(100);
		mainBoard.clearMarkers();
		drawBoard.printBoard();
		this.sleep(100);
		mainBoard.setCharacter(CHARACTER.O, new COORDINATE(4, 3, mainBoard));
		drawBoard.printBoard();
		this.sleep(100);
		mainBoard.setMarker(MARKER.RED, new COORDINATE(1, 2, mainBoard));
		mainBoard.setMarker(MARKER.YELLOW, new COORDINATE(0, 2, mainBoard));
		drawBoard.printBoard();
		
		if(quick){
			mainBoard.resetBoard();
			coloringBoard.resetBoard();
			coloringBoard2.resetBoard();
			drawBoard.printBoard();
			return;
		}
		this.sleep(4000);
		mainBoard.resetBoard();
		coloringBoard.resetBoard();
		coloringBoard2.resetBoard();
		drawBoard.printBoard();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("mouse clicked");
		try{
			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), mainBoard);
			System.out.println("board1: "+coord.toString());
		}catch (Exception ex){}
		try{
			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), coloringBoard);
			System.out.println("board2: "+coord.toString());
		}catch (Exception ex){}
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
