package classesFromOldGame;

public class PatternResult {
	private int countOfSubsequentCharacters = 0;
	private boolean isSubsequenceOpen = false;
	
	public PatternResult(){
	
	}
	
	public PatternResult(int countOfSubsequentCharacters, boolean isIubsequenceOpen){
		this.countOfSubsequentCharacters = countOfSubsequentCharacters;
		this.isSubsequenceOpen = isIubsequenceOpen;
	}

	public int getCountOfSubsequentCharacters() {
		return countOfSubsequentCharacters;
	}

	public void setCountOfSubsequentCharacters(int countOfSubsequentCharacters) {
		this.countOfSubsequentCharacters = countOfSubsequentCharacters;
	}

	public boolean isIubsequenceOpen() {
		return isSubsequenceOpen;
	}

	public void setIubsequenceOpen(boolean isIubsequenceOpen) {
		this.isSubsequenceOpen = isIubsequenceOpen;
	}
	
	
}
