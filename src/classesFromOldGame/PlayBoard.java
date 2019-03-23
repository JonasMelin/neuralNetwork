package classesFromOldGame;


/**
 * 
 * @author LyckligaGatan
 * Holds the data for a game board
 */
public class PlayBoard {

	public enum CHARACTER{X, O, FREE};
	public enum MARKER{GREEN, RED, YELLOW, FREE};
	public String name = "";
	
	// board size x/y
	public static final int boardLength = 11;
	private CHARACTER[][] theBoard = new CHARACTER[boardLength][boardLength];
	private MARKER[][] theMarkers = new MARKER[boardLength][boardLength];
	private Double[][] colorList = new Double[boardLength][boardLength];
	
	private int offsetx = 20;
	private int offsety = 20;
	
	public int getOffsetx() {
		return offsetx;
	}

	public int getOffsety() {
		return offsety;
	}
	
	public PlayBoard (String name){
		this.name = name;
		resetBoard();
	}
	
	public PlayBoard(int offsetx, int offsety, String name){
		this.offsetx = offsetx;
		this.offsety = offsety;
		this.name = name;
		resetBoard();
	}
	
	
	public int getBoardXHeigt() {
		return boardLength;
	}

	public int getBoardYWidth() {
		return boardLength;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public COORDINATE getCenterPosition() throws Exception{
		return new COORDINATE(boardLength / 2, boardLength / 2, this);
	}
	
	/**
	 * 
	 * @param coordinate
	 * @return CHARACTER
	 */
	public CHARACTER getCharacter(COORDINATE coordinate){
		return theBoard[coordinate.getX()][coordinate.getY()];
	}
	
	/**
	 * 
	 * @param coordinate
	 * @return MARKER
	 */
	public MARKER getMarker(COORDINATE coordinate){
		return theMarkers[coordinate.getX()][coordinate.getY()];
	}
	
	/**
	 * 
	 * @param coordinate
	 * @return Float
	 */
	public Double getColor(COORDINATE coordinate){
		return colorList[coordinate.getX()][coordinate.getY()];
	}
	
	/**
	 * 
	 * @param theCharacter
	 * @param coordinate
	 * @return
	 */
	public boolean setCharacter(CHARACTER theCharacter, COORDINATE coordinate){
		
		if(isCoordinateFree(coordinate)){
			theBoard[coordinate.getX()][coordinate.getY()] = theCharacter;
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param theCharacter
	 * @param coordinate
	 * @return
	 */
	public void setMarker(MARKER marker, COORDINATE coordinate){
		
		theMarkers[coordinate.getX()][coordinate.getY()] = marker;
	}
	
	/**
	 * 
	 * @param theCharacter
	 * @param coordinate
	 * @return
	 */
	public void setColor(Double color, COORDINATE coordinate){
		
		colorList[coordinate.getX()][coordinate.getY()] = color;
	}
	
	public void clearMarkers(){
		for(int x = 0; x < boardLength; x++){
			for(int y = 0; y < boardLength; y++){
				theMarkers[x][y] = MARKER.FREE;
			}
		}
	}
	
	 
	public void removeCharacter(COORDINATE coordinate){
		
		theBoard[coordinate.getX()][coordinate.getY()] = CHARACTER.FREE;
	}
	
	/**
	 * 
	 * @param coord
	 * @return
	 */
	public boolean isCoordinateFree(COORDINATE coord){
		return (theBoard[coord.getX()][coord.getY()] == CHARACTER.FREE);
	}
	
	public void resetBoard(){
		for (int a = 0; a < boardLength ; a ++){
			for(int b = 0 ; b < boardLength; b ++){
				theBoard[a][b]=CHARACTER.FREE;
				theMarkers[a][b] = MARKER.FREE;
				colorList[a][b]=Double.NaN;
			}
		}
	}
}
