package supportClasses;

import java.util.Random;

public class RandomNumbers {
	
	private Random generator = new Random();
	private static RandomNumbers theInstance;

	/**
	 * 
	 */
	private RandomNumbers(){
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 */
	public double adjustSlightlyRandomly(double in){
		if(trueOrFalse()){
			return in - in*(getDouble(1) / 50);
		}
		else{
			return in + in*(getDouble(1) / 50);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static RandomNumbers getInstance(){
		if (theInstance == null){
			theInstance = new RandomNumbers();
		}
		return theInstance;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean trueOrFalse(){
		return generator.nextBoolean();
	}
	
	/**
	 * 
	 * @param max
	 * @return
	 */
	public double getDouble(double max){
		double result = generator.nextDouble() * max;
		if (result == 0.0)
			result = 0.1;
		
		return result;

	}
	
	/**
	 * return random double centered around zero, but less than +/- max
	 * @param max
	 * @return
	 */
	public double getDoubleZeroCentered(double max){
		return getDouble(max * 2) - (max);
	}
	
	/**
	 * 
	 * @param max
	 * @return
	 */
	public int getInt(int max){
		return generator.nextInt(max);
	}
	
	/**
	 * return either a or b randomly
	 * @param a
	 * @param b
	 * @return
	 */
	public double pickRandom(double a, double b){
		
		if(trueOrFalse()){
			return a;
		}
		else{
			return b;
		}
	}
}
