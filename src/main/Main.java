package main;

import java.util.LinkedList;

import gameEngine.HumanTrainer;
import gameEngine.Neuron2Player;
import classesFromOldGame.PlayBoard;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.BoardOperations;
import classesFromOldGame.DrawBoard;
import classesFromOldGame.Player;

public class Main {

	@SuppressWarnings("unused")
	private static void testBoard() throws Exception {
		BoardTester b = new BoardTester();
		b.run(true);
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {

		//testBoard();

		PlayBoard playBoard = new PlayBoard("mainBoard");
		PlayBoard colorBoardX = new PlayBoard(250, 250, "colorBoard X");
		PlayBoard colorBoardO = new PlayBoard(250, 20, "colorBoard O");
		DrawBoard drawBoard = new DrawBoard(playBoard);
		drawBoard.addBoard(colorBoardX);
		drawBoard.addBoard(colorBoardO);
		Neuron2Player playerX = new Neuron2Player("player1");
		Neuron2Player playerO = new Neuron2Player("player2");
		LinkedList<Player> players = new LinkedList<Player>();
		players.add(playerX);
		players.add(playerO);
		drawBoard.printBoard();
		
		playerX.getAndInitGameEngine(playBoard, colorBoardX, new BoardOperations(colorBoardX), CHARACTER.X);
		playerO.getAndInitGameEngine(playBoard, colorBoardO, new BoardOperations(colorBoardO), CHARACTER.X);

		HumanTrainer humanTrainer = new HumanTrainer(players, playBoard, colorBoardX, colorBoardO, drawBoard);
		humanTrainer.start();
		System.exit(0);
	}
}
