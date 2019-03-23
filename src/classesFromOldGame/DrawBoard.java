package classesFromOldGame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.COORDINATE;
import neuralNetwork2.NeuronNetwork;

public class DrawBoard extends Thread{
	
	private int xSpacing = 20;
	private final int ySpacing = xSpacing;
	private final int characterSize = 7;
	private final int markerSize = 3;
	
	private boolean graphicsReady = false;
	private Shell shell = null;
	private Composite composite = null;
	private Display display = null;
	private Frame frame = null;
	private Canvas canvas = null;
	
	private String textToDraw1 = "";
	private String textToDraw2 = "";
	private String textToDraw3 = "";

	LinkedList<PlayBoard> allBoards = new LinkedList<PlayBoard>();
	
	

	public DrawBoard(PlayBoard theBoardToDraw) {
		super();
		this.allBoards.add(theBoardToDraw);
		this.start();
		  //primitive sync to allow thread to start...
		while(graphicsRedy() == false){
			System.out.println("GRAPHICS NOT READY");
			try{Thread.sleep(100);}catch(Exception ex){}
		  }
	}

	public void run() {

		display = new Display();
		shell = new Shell(display);
		FillLayout gridLayout = new FillLayout();
		shell.setLayout(gridLayout);
		shell.setSize(480, 600);
		composite = new Composite(shell, SWT.EMBEDDED);

		frame = SWT_AWT.new_Frame(composite);

		canvas = new Canvas() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				try{
					reDrawAll(g);
				}catch (Exception ex){}
			}
		};
		frame.add(canvas);
		shell.open();
		
		try{
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()){
					graphicsReady = true;
					display.sleep();
				}
			}
		}catch(Exception ex){}
		graphicsReady = false;
		display.dispose();
		System.exit(0);
	}
	
	public boolean graphicsRedy(){
		return graphicsReady;
	}
	
	public void registerMouseListener(MouseListener listener){
		if(canvas != null)
			canvas.addMouseListener(listener);
			//canvas.addKeyListener(l);
		
	}
	
	public void registerKeyListener(KeyListener listener){
		if(canvas != null)
			canvas.addKeyListener(listener);
	}
	
	public void unregisterListerners(MouseListener mlistener, KeyListener klistener){
		if(canvas == null)
			return;
		
		if(mlistener != null){
			canvas.removeMouseListener(mlistener);
		}
		
		if(klistener != null){
			canvas.removeKeyListener(klistener);
		}
	}
	
	public synchronized void addBoard(PlayBoard b){
		allBoards.add(b);
	}

	public void printBoard(){
		if(canvas == null)
			return;

		canvas.repaint();
		
	}

	public void setText1(String text){
		textToDraw1 = text;
	}
	public void setText2(String text){
		textToDraw2 = text;
	}
	public void setText3(String text){
		textToDraw3 = text;
	}
	public void resetText(){
		textToDraw1 = "";
		textToDraw2 = "";
		textToDraw3 = "";
	}
	
	private synchronized void reDrawAll(Graphics g) throws Exception{

		drawText(g);
		drawColors(g);
		drawGrid(g);
		drawCharacters(g);
	}
	
	private void drawText(Graphics g) throws Exception{
		g.drawString(textToDraw1, 20, 480);
		g.drawString(textToDraw2, 20, 510);
		g.drawString(textToDraw3, 20, 540);
	}
	
	private void drawColors(Graphics g) throws Exception{
		Iterator<PlayBoard> itx = allBoards.iterator();
		
		while(itx.hasNext()){
			PlayBoard theBoardToDraw = itx.next();

			int xOffset = theBoardToDraw.getOffsetx();
			int yOffset = theBoardToDraw.getOffsety();
			
			BoardOperations operatins = new BoardOperations(theBoardToDraw);
			
			List<COORDINATE> list = operatins.getAllColors();
			Iterator<COORDINATE> it = list.iterator();

			while (it.hasNext()){
				COORDINATE coord = it.next();
				Double finalColor = NeuronNetwork.trainValue;
				Double rawColor = theBoardToDraw.getColor(coord);
				
				if(rawColor < NeuronNetwork.trainValue){
					finalColor = NeuronNetwork.trainValue - (NeuronNetwork.trainValue - rawColor);
				}
				if(rawColor > NeuronNetwork.trainValue){
					finalColor = NeuronNetwork.trainValue - (rawColor - NeuronNetwork.trainValue);
				}
				finalColor *=2;
				g.setColor(new Color(finalColor.floatValue(), finalColor.floatValue(), finalColor.floatValue()));
				for (int p = 0; p < xSpacing; p ++){
					g.drawLine(xOffset + coord.getX()* xSpacing - characterSize + p, yOffset + coord.getY()* ySpacing - characterSize, xOffset + coord.getX()* xSpacing - characterSize + p, yOffset + coord.getY()* ySpacing + characterSize);
				}
			}
			g.setColor(Color.BLACK);
		}
	}
	
	private void drawCharacters(Graphics g) throws Exception{
		Iterator<PlayBoard> itx = allBoards.iterator();

		while(itx.hasNext()){
			PlayBoard theBoardToDraw = itx.next();

			int xOffset = theBoardToDraw.getOffsetx();
			int yOffset = theBoardToDraw.getOffsety();

			BoardOperations operatins = new BoardOperations(theBoardToDraw);
			List<COORDINATE> list = operatins.getAllMyPositions(CHARACTER.X);
			Iterator<COORDINATE> it = list.iterator();

			while (it.hasNext()){
				COORDINATE coord = it.next();
				g.drawLine(xOffset + coord.getX()* xSpacing - characterSize, yOffset + coord.getY()* ySpacing - characterSize, xOffset + coord.getX()* xSpacing + characterSize, yOffset + coord.getY()* ySpacing + characterSize);
				g.drawLine(xOffset + coord.getX() * xSpacing + characterSize, yOffset + coord.getY()* ySpacing - characterSize, xOffset + coord.getX()* xSpacing - characterSize, yOffset + coord.getY()* ySpacing + characterSize);

			}

			list = operatins.getAllMyPositions(CHARACTER.O);
			it = list.iterator();

			while (it.hasNext()){
				COORDINATE coord = it.next();
				g.drawLine(xOffset + coord.getX()* xSpacing - characterSize, yOffset + coord.getY()* ySpacing - characterSize, xOffset + coord.getX()* xSpacing - characterSize, yOffset + coord.getY()* ySpacing + characterSize);
				g.drawLine(xOffset + coord.getX() * xSpacing - characterSize, yOffset + coord.getY()* ySpacing + characterSize, xOffset + coord.getX()* xSpacing + characterSize, yOffset + coord.getY()* ySpacing + characterSize);
				g.drawLine(xOffset + coord.getX()* xSpacing + characterSize, yOffset + coord.getY()* ySpacing + characterSize, xOffset + coord.getX()* xSpacing + characterSize, yOffset + coord.getY()* ySpacing - characterSize);
				g.drawLine(xOffset + coord.getX() * xSpacing + characterSize, yOffset + coord.getY()* ySpacing - characterSize, xOffset + coord.getX()* xSpacing - characterSize, yOffset + coord.getY()* ySpacing - characterSize);
			}

			// Draw Markers
			list = operatins.getAllMarkers();
			it = list.iterator();

			while (it.hasNext()){
				COORDINATE coord = it.next();
				PlayBoard.MARKER marker = theBoardToDraw.getMarker(coord);
				
				switch(marker){
				case RED:
					g.setColor(Color.RED);
					break;
				case GREEN:
					g.setColor(Color.GREEN);
					break;
				case YELLOW:
					g.setColor(Color.YELLOW);
					break;
				case FREE:
					continue;
					default:
						g.setColor(Color.RED);
				}
				
				g.drawLine(xOffset + coord.getX()* xSpacing - markerSize, yOffset + coord.getY()* ySpacing - markerSize, xOffset + coord.getX()* xSpacing - markerSize, yOffset + coord.getY()* ySpacing + markerSize);
				g.drawLine(xOffset + coord.getX() * xSpacing - markerSize, yOffset + coord.getY()* ySpacing + markerSize, xOffset + coord.getX()* xSpacing + markerSize, yOffset + coord.getY()* ySpacing + markerSize);
				g.drawLine(xOffset + coord.getX()* xSpacing + markerSize, yOffset + coord.getY()* ySpacing + markerSize, xOffset + coord.getX()* xSpacing + markerSize, yOffset + coord.getY()* ySpacing - markerSize);
				g.drawLine(xOffset + coord.getX() * xSpacing + markerSize, yOffset + coord.getY()* ySpacing - markerSize, xOffset + coord.getX()* xSpacing - markerSize, yOffset + coord.getY()* ySpacing - markerSize);
				g.setColor(Color.BLACK);
			}
		}
		
		g.setColor(Color.BLACK);
	}
	
	private void drawGrid(Graphics g){
		g.setColor(Color.LIGHT_GRAY);
		
		Iterator<PlayBoard> it = allBoards.iterator();
		
		while(it.hasNext()){
			PlayBoard theBoardToDraw = it.next();

			int xOffset = theBoardToDraw.getOffsetx();
			int yOffset = theBoardToDraw.getOffsety();

			char[] text = {'a'};
			g.drawChars(text, 0, 1, 0, 0);

			for(int y = 0; y <= theBoardToDraw.getBoardYWidth(); y ++){

				int x1 = xOffset - 2 + y* xSpacing - characterSize;
				int y1 = yOffset - 2 - characterSize;
				int x2 = xOffset - 2+ y* xSpacing - characterSize;
				int y2 = yOffset - 2+  theBoardToDraw.getBoardYWidth()*ySpacing - characterSize;

				g.drawLine(x1, y1, x2, y2);
			}
			
			for(int y = 0; y <= theBoardToDraw.getBoardXHeigt(); y ++){

				int y1 = yOffset - 2+ y* xSpacing - characterSize;
				int x1 = xOffset - 2 - characterSize;
				int y2 = yOffset - 2+ y* xSpacing - characterSize;
				int x2 = xOffset - 2+  theBoardToDraw.getBoardYWidth()*ySpacing - characterSize;

				g.drawLine(x1, y1, x2, y2);
			}
			/*
			for(int y = 0; y <= theBoardToDraw.getBoardXHeigt(); y ++){

				int y1 = xOffset - 2+ y* xSpacing - characterSize;
				int x1 = yOffset - 2 - characterSize;
				int y2 = xOffset - 2+ y* xSpacing - characterSize;
				int x2 = yOffset - 2+  theBoardToDraw.getBoardYWidth()*ySpacing - characterSize;

				g.drawLine(x1, y1, x2, y2);
			}*/
		}
		g.setColor(Color.BLACK);
	}
	
	public COORDINATE convertPixelToCoordinate(int x, int y, PlayBoard theBoard) throws Exception{
		if(((x - theBoard.getOffsetx()) < 0) || ((y - theBoard.getOffsety()) < 0)){
			throw new Exception("convert out of bounds");
		}
		return new COORDINATE((x - theBoard.getOffsetx() + characterSize) / xSpacing, (y - theBoard.getOffsety() + characterSize) / ySpacing, theBoard);
	}

	/**
	 * 
	 * @return the main board, the first board added during construcion
	 */
	public PlayBoard getTheBoardToDraw() {
		return allBoards.get(0);
	}
	
	
}
