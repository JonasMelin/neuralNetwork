package gameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.PlayBoard.MARKER;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.ComputerMatchController;
import classesFromOldGame.DrawBoard;
import classesFromOldGame.GameEngine;
import classesFromOldGame.HumanMatchController;
import classesFromOldGame.Player;
import supportClasses.OUTPUTNEURON;

public class HumanTrainer implements MouseListener, KeyListener, Runnable{
	private LinkedList<Player> players;
	private PlayBoard mainBoard;
	private PlayBoard coloringBoardX;
	private PlayBoard coloringBoardO;
	private DrawBoard drawBoard;
	
	private enum STATE {INIT,IDLE, ADDING, REPTILE_TRAINING, VERIFYING, QUITTING, AUTO_PLAYING, PLAYING_HUMAN};
	private STATE state = STATE.INIT;
	private OUTPUTNEURON outputNeuronToTrainX = null;
	private OUTPUTNEURON outputNeuronToTrainO = null;
	private boolean abortThread = false;
	private HumanMatchController hmc = null;
	

	/**************************************************************
	 * 
	 *************************************************************/
	public HumanTrainer(LinkedList<Player> players, PlayBoard mainBoard, PlayBoard coloringBoardX, PlayBoard coloringBoardO, DrawBoard drawBoard) {
		super();
		this.players = players;
		this.mainBoard = mainBoard;
		this.drawBoard = drawBoard;
		this.coloringBoardX = coloringBoardX;
		this.coloringBoardO = coloringBoardO;
		drawBoard.registerMouseListener(this);
		drawBoard.registerKeyListener(this);
	}
	
	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("mouse clicked");
		
		if(state == STATE.PLAYING_HUMAN){
			return;
		}
		
		//Check for the first boards
		try{

			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), mainBoard);
			System.out.println("board1: "+coord.toString());
			
			mainBoard.clearMarkers();
			
			if(state == STATE.ADDING){
				mainBoard.setCharacter(CHARACTER.X, coord);
				drawBoard.setText3(CHARACTER.X.toString()+ " "+coord.toString());
			}
			if(state == STATE.REPTILE_TRAINING){
				if(outputNeuronToTrainX == null || outputNeuronToTrainO == null){
					System.out.println("You did not chose output neuron to train!!");
					drawBoard.setText3("First set out neuron in lower frame");
				}else{
					
					Player pX; 
					Player pO;
					mainBoard.setMarker(MARKER.RED, coord);	
					
					if(players.size() < 2){
						System.out.println("you need two players to train");
						return;
					}
					Iterator<Player> it = players.iterator();

					pX = it.next();
					pO = it.next();
					OUTPUTNEURON o = new OUTPUTNEURON(10, 10);

					Neuron2GameEngine g2 = (Neuron2GameEngine) pX.getGameEngine();

					g2.trainWithNoise(coord, outputNeuronToTrainX, 5, true);
					
					
					g2 = (Neuron2GameEngine) pO.getGameEngine();

					g2.trainWithNoise(coord, outputNeuronToTrainO, 30, true);
					
					

				}
				
			}
			if(state == STATE.VERIFYING){
				Player pX; 
				Player pO;
				mainBoard.setMarker(MARKER.RED, coord);	
				
				if(players.size() < 2){
					System.out.println("you need two players to verify");
					return;
				}
				Iterator<Player> it = players.iterator();

				pX = it.next();
				pO = it.next();
				GameEngine g = pX.getGameEngine();
				Neuron2GameEngine g2 = (Neuron2GameEngine)g;
				g2.printReptileOutput(coord);
				drawBoard.setText3(coord.toString());
				
				g = pO.getGameEngine();
				g2 = (Neuron2GameEngine)g;
				g2.printReptileOutput(coord);
				drawBoard.setText3(coord.toString());
			}
			
		}catch (Exception ex){}
		
		// Check for the other board
		try{
			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), coloringBoardX);
			System.out.println("board X: "+coord.toString());
			
			
			if(state == STATE.REPTILE_TRAINING){
				coloringBoardX.clearMarkers();
				coloringBoardX.setMarker(MARKER.GREEN, coord);
				outputNeuronToTrainX = new OUTPUTNEURON(coord.getX(), coord.getY())	;
				drawBoard.setText3("set out neuron to train: "+ outputNeuronToTrainX.toString());
				
			}
		}catch (Exception ex){}
		
		// Check for the other board
		try{
			COORDINATE coord = drawBoard.convertPixelToCoordinate(arg0.getX(), arg0.getY(), coloringBoardO);
			System.out.println("board O: "+coord.toString());
			
			
			if(state == STATE.REPTILE_TRAINING){
				coloringBoardO.clearMarkers();
				coloringBoardO.setMarker(MARKER.YELLOW, coord);
				outputNeuronToTrainO = new OUTPUTNEURON(coord.getX(), coord.getY())	;
				drawBoard.setText3("set out neuron to train O: "+ outputNeuronToTrainO.toString());
				
			}
		}catch (Exception ex){}
		
		drawBoard.printBoard();
	}
	
	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("key pressed: " + e.getKeyChar());
		
		if(state == STATE.AUTO_PLAYING){
			abortThread = true;
			System.out.println("aborting game");
			drawBoard.setText3("aborting game");
			
			return;
		}
		
		if(state == STATE.PLAYING_HUMAN){
			if(hmc != null){
				hmc.terminate();
				hmc = null;
				System.out.println("aborting human game");
				drawBoard.setText3("aborting human game");
			}else{
				System.out.println("internal error 67868687");
			}
			state = STATE.IDLE;
			return;
		}

		byte[] buf = new byte[10];
		try{
			switch (e.getKeyChar()){
			case 'a': // 'a' add
				System.out.println("ADD");
				outputNeuronToTrainX = null; outputNeuronToTrainO = null;
				coloringBoardX.resetBoard();coloringBoardO.resetBoard();
				state = STATE.ADDING;
				drawBoard.setText2("ADDING... (t to train)");
				break;
			case 'c':  // 'c' clear
				System.out.println("CLEAR");
				mainBoard.resetBoard();
				coloringBoardX.resetBoard();coloringBoardO.resetBoard();
				outputNeuronToTrainX = null; outputNeuronToTrainO = null;
				state = STATE.IDLE;
				drawBoard.setText2("IDLE... (a to add)");
				drawBoard.setText3("");
				break;
			case 't': // 't' train
				System.out.println("TRAIN");
				coloringBoardX.resetBoard();coloringBoardO.resetBoard();
				outputNeuronToTrainX = null; outputNeuronToTrainO = null;
				state = STATE.REPTILE_TRAINING;
				drawBoard.setText2("REPTILE TRAINING... (v to verify)");
				break;
			case 'v': // 'v' verify
				System.out.println("VERIFY");
				outputNeuronToTrainX = null; outputNeuronToTrainO = null;
				coloringBoardX.resetBoard();coloringBoardO.resetBoard();
				state = STATE.VERIFYING;
				drawBoard.setText2("VERIFYING... (c to reset)");
				break;
			case 'q': // 'q' quit
				System.out.println("QUIT TRAINER");
				outputNeuronToTrainX = null; outputNeuronToTrainO = null;
				coloringBoardX.resetBoard();coloringBoardO.resetBoard();
				state = STATE.QUITTING;
				drawBoard.setText2("QUITTING...");
				return;
			case 'm':
				System.out.println("AUTOTRAIN");
				state = STATE.IDLE;
				drawBoard.setText2("TRAINING... ");
				drawBoard.printBoard();
				AutoTrainer.trainPlayersC(players, mainBoard, coloringBoardX, coloringBoardO, drawBoard);
				drawBoard.setText2("IDLE... (a to add)");
				drawBoard.setText3("");
				break;
			case 's':
				abortThread = false;
				state = STATE.AUTO_PLAYING;
				new Thread(this).start();
				break;
			case 'p':
				state = STATE.PLAYING_HUMAN;
				hmc = new HumanMatchController(players, drawBoard);
				hmc.startGame();
				break;

			default:
				System.out.println("unhandled key stroke: "+buf[0]);
			}
		}catch (Exception ex){}
		drawBoard.printBoard();
	}
	
	/**************************************************************
	 * 
	 *************************************************************/
	public int start() throws Exception{
		
		boolean userActivity = false;
		
		drawBoard.setText1(" -- Trainer mode -- (q to quit)");
		drawBoard.setText2("INIT... (a to add)");
		drawBoard.printBoard();
		
		for(int a = 0; a < 10 ; a++){
			
			if(state != STATE.INIT){
				userActivity = true;
				break;
			}
			
			try{
				Thread.sleep(1000);
			}catch (Exception ex){}
		}
		
		if(!userActivity){
			drawBoard.setText2("TRAINING... user inactive for 10 seconds ");
			drawBoard.printBoard();
			AutoTrainer.trainPlayersB(players, mainBoard, coloringBoardX, coloringBoardO, drawBoard);
			abortThread = false;
			state = STATE.AUTO_PLAYING;
			drawBoard.setText2("PLAYING... any key to stop (after next game is complete)");
			new Thread(this).start();
		}

		for(;;){
			
			if(state == STATE.QUITTING){
				drawBoard.setText1("---");
				drawBoard.setText2("Left the trainer");
				drawBoard.setText3("");
				drawBoard.printBoard();
				drawBoard.unregisterListerners(this, this);
				return 0;
			}
			
			try{
				Thread.sleep(100);
			}catch (Exception ex){}
		}
	}
	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**************************************************************
	 * 
	 *************************************************************/
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void run(){
		System.out.println("starting thread to run game...");


		drawBoard.setText2("PLAYING COMPUTER vs COMPUTER... any key to stop (after next game is complete)");
		mainBoard.resetBoard();

		if (players.size() < 2) {
			System.out.println("you need two players to verify");
			return;
		}
		Iterator<Player> it = players.iterator();

		Player pX = it.next();
		Player pO = it.next();

		while (!abortThread) {
			try {
				ComputerMatchController cmc = new ComputerMatchController(drawBoard, coloringBoardX, coloringBoardO, pX, pO);

				cmc.runGame(PlayBoard.CHARACTER.X);
			} catch (Exception ex) {
				break;
			}
			System.out.println(pX.toString());
			System.out.println(pO.toString());
			drawBoard.getTheBoardToDraw().resetBoard();
		}

		state = STATE.IDLE;
	}
}
