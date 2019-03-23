package classesFromOldGame;

import classesFromOldGame.PlayBoard;


	public class COORDINATE{
		private int x;
		private int y;

		public COORDINATE(int x, int y, PlayBoard board) throws Exception{
			if(     (x < 0) || 
					(y < 0) || 
					(x >= board.getBoardXHeigt()) || 
					(y >= board.getBoardYWidth())){
				throw new Exception("coordinate out of bounds");
			}
			this.x = x; this.y = y;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}

		public static boolean isEqual(COORDINATE a, COORDINATE b){
			return((a.getX() == b.getX()) && (a.getY() == b.getY()));
		}
		
		public String toString(){
			return "x:" + x + ",y:" + y;
		}
	}	