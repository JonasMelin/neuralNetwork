package classesFromOldGame;

import java.util.HashMap;
import classesFromOldGame.direction.DIRECTION;

public class CoordinateAnalyzis {

	private COORDINATE coordinate;
	private double density = 0;
	private double totalScore = 0;
	
	private HashMap<DIRECTION, PatternResult> patternResultList = new HashMap<DIRECTION, PatternResult>();
	
	
	public CoordinateAnalyzis(COORDINATE coordinate){
		this.coordinate = coordinate;
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<DIRECTION, PatternResult> getPatternResultList(){
		
		return patternResultList;
	}

	public void setAnalysisNorth(PatternResult analysisNorth) {
		patternResultList.put(DIRECTION.NORTH, analysisNorth);
	}
	
	public void setAnalysisNorthEast(PatternResult analysisNorthEast) {
		patternResultList.put(DIRECTION.NORTHEAST, analysisNorthEast);
	}

	public void setAnalysisEast(PatternResult analysisEast) {
		patternResultList.put(DIRECTION.EAST, analysisEast);
	}

	public void setAnalysisSouthEast(PatternResult analysisSouthEast) {
		patternResultList.put(DIRECTION.SOUTHEAST, analysisSouthEast);
	}

	public void setAnalysisSouth(PatternResult analysisSouth) {
		patternResultList.put(DIRECTION.SOUTH, analysisSouth);
	}

	public void setAnalysisSouthWest(PatternResult analysisSouthWest) {
		patternResultList.put(DIRECTION.SOUTHWEST, analysisSouthWest);
	}

	public void setAnalysisWest(PatternResult analysisWest) {
		patternResultList.put(DIRECTION.WEST, analysisWest);
	}

	public void setAnalysisNorthWest(PatternResult analysisNorthWest) {
		patternResultList.put(DIRECTION.NORTHWEST, analysisNorthWest);
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}

	public COORDINATE getCoordinate() {
		return coordinate;
	}
}
