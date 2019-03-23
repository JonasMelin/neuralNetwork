package supportClasses;

public class MathFunctions {

	private final static double derivativeStep=0.1f;
	
	
	/**
	 * sigmoid function. Returns a value between -1 and 1
	 * @param x - input
	 * @return
	 */
	public static double sigmoid(double x) throws Exception{
		// Slow, accurate
		return 1.0f / (1.0f + (double) Math.exp(-x));
	}
	
	/** 
	 * Calc derivative for sigmoid function in x
	 * @param x
	 * @return
	 */
	public static double derivativeSigmoid(double x) throws Exception{
		return (sigmoid(x + derivativeStep) - sigmoid(x - derivativeStep))/(2*derivativeStep)   ;
	} 
	
	public static double calcNewWeight(double delta, double weight, double learnRateKonst) throws Exception{
		return weight - delta * learnRateKonst;
	}
	
	public static boolean inRange(double reference, double testValue, double allowedDiff){
		double lowerAllowed = reference - allowedDiff;
		double upperAllowed = reference + allowedDiff;
		
		if(Double.isNaN(reference) || Double.isNaN(testValue)){
			return false;
		}
		
		if(testValue < lowerAllowed)
			return false;
		
		if(testValue > upperAllowed)
			return false;
		
		return true;
	}
	
	public static double ConvertNaNInfinite(double number) throws Exception{
		
		if (Double.isInfinite(number) || Double.isNaN(number))
			return Double.MAX_VALUE;
		
		return number;			
	}
	
	public static boolean isNear(float target, float valueUnderTest, float diffAllowed){

		float lower = target - diffAllowed;
		float upper = target + diffAllowed;
		
		if((valueUnderTest >= lower) && (valueUnderTest <= upper)){
			return true;
		}		
		return false;
	}
	
}
