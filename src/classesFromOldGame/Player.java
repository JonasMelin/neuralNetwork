package classesFromOldGame;

import java.text.DecimalFormat;

import classesFromOldGame.PlayBoard;
import classesFromOldGame.BoardOperations;

public abstract class Player {

	//protected ParameterGenes genes;
	
	protected int gamesWon = 0;
	protected int gamesPlayed = 0;
	protected int averageMatchLengthForWinningGames = 0;
	protected int charCountForLastMatch = 0;
	protected static int globalPlayerId = 0;
	protected int myPlayerId;
	protected int generationsAge = 0;
	protected int iAmAnImmigrant = 0;
	protected String myName = "";
	protected boolean gameEngineInitOk = false;
	
	/**
	 * Inherited constructor must create genes and set local variable!!
	 */
	public Player(String name){
		 this.myName = name;
		 globalPlayerId++;
		 myPlayerId = globalPlayerId;
	}
	

	public abstract Player mate(Player otherPlayer);
	
	public abstract GameEngine getAndInitGameEngine(PlayBoard theBoard, PlayBoard coloringBoard, BoardOperations boardOperations, PlayBoard.CHARACTER myCharacter) throws Exception;
	
	public abstract GameEngine getGameEngine()  throws Exception;
	
	protected void setImmigrantGeneration(Player ChildPlayer, Player otherPlayer){
		
		if((iAmAnImmigrant == 0) && (otherPlayer.amIAnImmigrant() == 0))
			return;
		
		if(iAmAnImmigrant < otherPlayer.amIAnImmigrant())
			ChildPlayer.setiAmAnImmigrant(iAmAnImmigrant + 1);
		else
			ChildPlayer.setiAmAnImmigrant(otherPlayer.amIAnImmigrant() + 1);
		
		return;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public int getAverageMatchLengthForWinningGames() {
		return averageMatchLengthForWinningGames;
	}

	public void incrementSetCharCount() {
		charCountForLastMatch++;
	}

	public double getWinningPercent(){
		if(gamesPlayed == 0)
			return 0.001f;
		return 100* ((double)gamesWon / (double)gamesPlayed);
	}
	
	public void SumUpAndResetAfterGame(boolean gameWon){
		if(gameWon){
			gamesWon++;
			averageMatchLengthForWinningGames = ((averageMatchLengthForWinningGames * gamesWon) + charCountForLastMatch) / gamesWon;
		}
		gamesPlayed++;
		charCountForLastMatch = 0;
	}
	
	public int getPlayerId(){
		return myPlayerId;
	}
	
	public void incrementGenerationsAge() {
		generationsAge++;
		gamesWon = 0;
		gamesPlayed = 0;
	    averageMatchLengthForWinningGames = 0;
	    charCountForLastMatch = 0;
	}
	
	public int getGenerationsAge(){
		return generationsAge;
	}

	public int amIAnImmigrant() {
		return iAmAnImmigrant;
	}

	public void setiAmAnImmigrant(int immigrantGeneration) {
		this.iAmAnImmigrant = immigrantGeneration;
	}

	public String toString(){
		DecimalFormat df = new DecimalFormat("#.##");

		String result = new String();
		result = "-- Name: "+myName+". Age: "+generationsAge+". GamesPlayed: "+gamesPlayed+". PercentWin: " + df.format(getWinningPercent()) + "\n";
		return result;
	}


	public String getMyName() {
		return myName;
	}
	
	
}
