package supportClasses;

import classesFromOldGame.COORDINATE;

	public class AnalyzisResult{
		private COORDINATE coord;
		private double smallestDiff;
		private int hitCount;
		private float score;
		
		public AnalyzisResult(COORDINATE coord, double smallestDiff, int hitCount) {
			this.coord = coord;
			this.smallestDiff = smallestDiff;
			this.hitCount = hitCount;
			
		}

		public COORDINATE getCoord() {
			return coord;
		}

		public void setCoord(COORDINATE coord) {
			this.coord = coord;
		}

		public double getSmallestDiff() {
			return smallestDiff;
		}
		
		public int getHitCount(){
			return hitCount;
		}
		
		public float getScore(){
			return score;
		}

		public void setScore(float score) {
			this.score = score;
		}
}
