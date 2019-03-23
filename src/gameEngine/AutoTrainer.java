package gameEngine;

import java.util.Iterator;
import java.util.LinkedList;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.PlayBoard.MARKER;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.DrawBoard;
import classesFromOldGame.GameEngine;
import classesFromOldGame.Player;
import supportClasses.OUTPUTNEURON;


public class AutoTrainer {
	
	public static void trainPlayersC(LinkedList<Player> players, PlayBoard theBoard,PlayBoard colorBoardX, PlayBoard colorBoardO, DrawBoard drawBoard) throws Exception{

		Player pX; 
		Player pO;
		int nextFreeX=0;
		
		if(players.size() < 2){
			System.out.println("you need two players to verify");
			return;
		}
		Iterator<Player> it = players.iterator();

		pX = it.next();
		pO = it.next();
		
		for(int u = 0 ; u < 5000 ; u++){
			theBoard.resetBoard();
			nextFreeX=0;
			
			// Draw five X
			for(int a = -5; a < 5 ; a ++){
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(5, 5+a, theBoard));
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(6, 5+a, theBoard));
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(4, 5+a, theBoard));
			}
			
			// Train it for all 5 positions
			
			for(int a = 0; a < 1 ; a ++){
				train2(pX, new COORDINATE(5, 5 +a, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(0, a));
				train2(pO, new COORDINATE(5, 5 +a, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(0, a));
				train2(pO, new COORDINATE(5, 5 +a, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(1, a));
			}

			

		}
		System.out.println("Training session complete" );
	}
	
	public static void trainPlayersB(LinkedList<Player> players, PlayBoard theBoard,PlayBoard colorBoardX, PlayBoard colorBoardO, DrawBoard drawBoard) throws Exception{

		Player pX; 
		Player pO;
		int nextFreeX=0;
		
		if(players.size() < 2){
			System.out.println("you need two players to verify");
			return;
		}
		Iterator<Player> it = players.iterator();

		pX = it.next();
		pO = it.next();
		
		for(int u = 0 ; u < 100 ; u++){
			theBoard.resetBoard();
			nextFreeX=0;
			for(int a = 0; a < 5 ; a ++){
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(5, 5 + a, theBoard));
				train2(pX, new COORDINATE(5, 5, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(0, nextFreeX++));
				//for(int b = 0 ; b <= a; b++){
				//	train2(pO, new COORDINATE(5, 5+b, theBoard), theBoard, colorBoardO, drawBoard, new OUTPUTNEURON(0, 0));
				//}
				train2(pO, new COORDINATE(5, 5, theBoard), theBoard, colorBoardO, drawBoard, new OUTPUTNEURON(0, nextFreeX));
			}
			

			///////
			theBoard.resetBoard();
			nextFreeX=0;
			for(int a = 0; a < 5 ; a ++){
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(5 + a, 5, theBoard));
				train2(pX, new COORDINATE(5, 5, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(1, nextFreeX++));
				train2(pO, new COORDINATE(5, 5, theBoard), theBoard, colorBoardO, drawBoard, new OUTPUTNEURON(1, nextFreeX));
			}
			
			///////
			theBoard.resetBoard();
			nextFreeX=0;
			for(int a = 0; a < 5 ; a ++){
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(5 + a, 5 + a, theBoard));
				train2(pX, new COORDINATE(5, 5, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(2,nextFreeX++));
				train2(pO, new COORDINATE(5, 5, theBoard), theBoard, colorBoardO, drawBoard, new OUTPUTNEURON(2, nextFreeX));
			}
			
			theBoard.resetBoard();
			nextFreeX=0;
			for(int a = 0; a < 5 ; a ++){
				theBoard.setCharacter(CHARACTER.X, new COORDINATE(5 + a, 5 - a, theBoard));
				train2(pX, new COORDINATE(5, 5, theBoard), theBoard, colorBoardX, drawBoard, new OUTPUTNEURON(3, nextFreeX++));
				train2(pO, new COORDINATE(5, 5, theBoard), theBoard, colorBoardO, drawBoard, new OUTPUTNEURON(3, nextFreeX));
			}
		}
		System.out.println("Training session complete" );
	}
	
	
	private static int train2(Player player, COORDINATE coord, PlayBoard theBoard, PlayBoard coloringBoard, DrawBoard drawBoard,OUTPUTNEURON outpuneuron) throws Exception{
		theBoard.setMarker(MARKER.RED, coord);
	
		GameEngine g = player.getGameEngine();
		Neuron2GameEngine g2 = (Neuron2GameEngine)g;

		g2.trainWithNoise(coord, outpuneuron,1, true);

		return 0;
	}
}
