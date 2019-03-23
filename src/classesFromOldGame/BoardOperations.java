package classesFromOldGame;

import java.util.Iterator;
import java.util.LinkedList;
import classesFromOldGame.PlayBoard.CHARACTER;
import classesFromOldGame.PlayBoard.MARKER;
import classesFromOldGame.COORDINATE;
import classesFromOldGame.CoordinateAnalyzis;
import classesFromOldGame.PatternResult;
import classesFromOldGame.direction.DIRECTION;


public class BoardOperations {

	private PlayBoard gameBoard;
	
	public BoardOperations(PlayBoard gameBoard){
		this.gameBoard = gameBoard;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public LinkedList<COORDINATE> getInterestingFreePositions() throws Exception{
		LinkedList<COORDINATE> list = new LinkedList<COORDINATE>(); 
				
		for(int x = 0; x < gameBoard.getBoardXHeigt(); x ++){
			for(int y = 0; y < gameBoard.getBoardYWidth(); y++){
				COORDINATE c = new COORDINATE(x,y,gameBoard);

				if(gameBoard.isCoordinateFree(c) && IsCoordinateNearAnotherCharacter(c)){
					list.add(c);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @param character
	 * @return
	 * @throws Exception
	 */
	public LinkedList<COORDINATE> getAllMyPositions(CHARACTER character) throws Exception{
		LinkedList<COORDINATE> list = new LinkedList<COORDINATE>(); 
				
		for(int x = 0; x < gameBoard.getBoardXHeigt(); x ++){
			for(int y = 0; y < gameBoard.getBoardYWidth(); y++){
				COORDINATE c = new COORDINATE(x,y,gameBoard);

				if(gameBoard.isCoordinateFree(c)){
					continue;
				}
				if(gameBoard.getCharacter(c) == character){
					list.add(c);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param character
	 * @return
	 * @throws Exception
	 */
	public LinkedList<COORDINATE> getAllMarkers() throws Exception{
		LinkedList<COORDINATE> list = new LinkedList<COORDINATE>(); 
				
		for(int x = 0; x < gameBoard.getBoardXHeigt(); x ++){
			for(int y = 0; y < gameBoard.getBoardYWidth(); y++){
				COORDINATE c = new COORDINATE(x,y,gameBoard);

				if(gameBoard.getMarker(c) == MARKER.FREE){
					continue;
				}
				list.add(c);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param character
	 * @return
	 * @throws Exception
	 */
	public LinkedList<COORDINATE> getAllColors() throws Exception{
		LinkedList<COORDINATE> list = new LinkedList<COORDINATE>(); 
				
		for(int x = 0; x < gameBoard.getBoardXHeigt(); x ++){
			for(int y = 0; y < gameBoard.getBoardYWidth(); y++){
				COORDINATE c = new COORDINATE(x,y,gameBoard);

				if(gameBoard.getColor(c).isNaN()){
					continue;
				}
				list.add(c);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param coord
	 * @param character
	 * @param result
	 */
	public void analyzeCoordinate(CHARACTER theCharacter, CoordinateAnalyzis result){
	
		
		try{
			result.setAnalysisNorth(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.NORTH)); 
		}catch (Exception ex){}
		try{
			result.setAnalysisSouth(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.SOUTH)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisNorthEast(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.NORTHEAST)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisSouthWest(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.SOUTHWEST)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisEast(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.EAST)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisWest(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.WEST)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisSouthEast(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.SOUTHEAST)) ;
		}catch (Exception ex){}
		try{
			result.setAnalysisNorthWest(scanPatternInDirection(result.getCoordinate(), theCharacter, DIRECTION.NORTHWEST));
		}catch (Exception ex){}
		
		result.setDensity(GetDensityNearCoordinate(result.getCoordinate(), theCharacter, 2));
	}
	
	/**
	 * 
	 * @param coord
	 * @param character
	 * @param depth
	 * @return
	 */
	public int GetDensityNearCoordinate(COORDINATE coord, PlayBoard.CHARACTER character, int depth){
		int count = 0;
		
		for (int x = -depth; x < depth; x ++){
			for (int y = -depth; y < depth; y++){
				try{
					COORDINATE c = new COORDINATE(coord.getX() + x,coord.getY() + y,gameBoard);
					
					if(!gameBoard.isCoordinateFree(c) && (gameBoard.getCharacter(c) == character)){
						count ++;
					}
					
				}catch(Exception ex){}
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public CHARACTER lookForWinner() throws Exception{

		//
		//ToDo: Base function upon analyzeCoordinate()
		// 
		CHARACTER theCharacter = CHARACTER.FREE;
		
		for(int a = 0; a < 2; a ++){
		
			if(a == 1){
				theCharacter = CHARACTER.O;
			}
			else{
				theCharacter = CHARACTER.X;
			}
			
			LinkedList<COORDINATE> list = getAllMyPositions(theCharacter);
			Iterator<COORDINATE> it = list.iterator();
			
			while(it.hasNext()){
				COORDINATE coord = it.next();
				try{
					PatternResult patternResult1 = scanPatternInDirection(coord, theCharacter, DIRECTION.NORTH);
					PatternResult patternResult2 = scanPatternInDirection(coord, theCharacter, DIRECTION.SOUTH);
					
					if(patternResult1.getCountOfSubsequentCharacters() + patternResult2.getCountOfSubsequentCharacters() + 1 >= 5){
						return theCharacter;
					}
				}catch (Exception ex){}

				try{
					PatternResult patternResult1 = scanPatternInDirection(coord, theCharacter, DIRECTION.NORTHEAST);
					PatternResult patternResult2 = scanPatternInDirection(coord, theCharacter, DIRECTION.SOUTHWEST);
					
					if(patternResult1.getCountOfSubsequentCharacters() + patternResult2.getCountOfSubsequentCharacters() + 1 >= 5){
						return theCharacter;
					}
				}catch (Exception ex){}

				try{
					PatternResult patternResult1 = scanPatternInDirection(coord, theCharacter, DIRECTION.EAST);
					PatternResult patternResult2 = scanPatternInDirection(coord, theCharacter, DIRECTION.WEST);
					
					if(patternResult1.getCountOfSubsequentCharacters() + patternResult2.getCountOfSubsequentCharacters() + 1 >= 5){
						return theCharacter;
					}
				}catch (Exception ex){}

				try{
					PatternResult patternResult1 = scanPatternInDirection(coord, theCharacter, DIRECTION.SOUTHEAST);
					PatternResult patternResult2 = scanPatternInDirection(coord, theCharacter, DIRECTION.NORTHWEST);
					
					if(patternResult1.getCountOfSubsequentCharacters() + patternResult2.getCountOfSubsequentCharacters() + 1 >= 5){
						return theCharacter;
					}
				}catch (Exception ex){}
			}
		}		
		theCharacter = CHARACTER.FREE;
		return theCharacter;
	}
	
	/**
	 * 
	 * @param coord
	 * @return
	 */
	public PatternResult scanPatternInDirection(COORDINATE coord, CHARACTER character, DIRECTION direction) throws Exception{
		
		PatternResult result = new PatternResult();
		int x = coord.getX();
		int y = coord.getY();
		int xIncrement;
		int yIncrement;
		
		switch (direction){
		case NORTH:
			xIncrement = -1;
			yIncrement = 0;
			break;
		case NORTHEAST:
			xIncrement = -1;
			yIncrement = +1;			
			break;
		case EAST:
			xIncrement = 0;
			yIncrement = +1;			
			break;
		case SOUTHEAST:
			xIncrement = +1;
			yIncrement = +1;			
			break;
		case SOUTH:
			xIncrement = +1;
			yIncrement = 0;			
			break;
		case SOUTHWEST:
			xIncrement = +1;
			yIncrement = -1;			
			break;
		case WEST:
			xIncrement = 0;
			yIncrement = -1;			
			break;
		case NORTHWEST:
			xIncrement = -1;
			yIncrement = -1;			
		    break;
		    default:
		    	throw new Exception("error #32498567423865");
		}
		
		
		try{
			while(true){
				x += xIncrement;
				y += yIncrement;
				COORDINATE c = new COORDINATE(x, y, gameBoard);
							
				if(gameBoard.isCoordinateFree(c)){
					result.setIubsequenceOpen(true);
					break;
				}
				if(gameBoard.getCharacter(c) == character){
					result.setCountOfSubsequentCharacters(result.getCountOfSubsequentCharacters() + 1);
				}
				else{
					result.setIubsequenceOpen(false);
					break;
				}
				
			}
			
		}catch (Exception ex){
			result.setIubsequenceOpen(false);
		}
		
		
		return result;
	}
	
	/**
	 * 
	 * @param coordinate
	 * @return
	 */
	private boolean IsCoordinateNearAnotherCharacter(COORDINATE coordinate){
		
		for (int x = coordinate.getX() -1; x < coordinate.getX() + 2; x ++){
			for (int y = coordinate.getY() -1; y < coordinate.getY() + 2; y++){
				try{
					COORDINATE c = new COORDINATE( x, y, gameBoard);
					
					if(COORDINATE.isEqual(c, coordinate))
						continue;
					
					if(!gameBoard.isCoordinateFree(c)){
						return true;
					}
					
				}catch(Exception ex){}
			}
		}
		return false;
	}
	
	
}
