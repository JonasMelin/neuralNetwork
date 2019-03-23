package neuralNetwork2;

public class Cluster {

	private int clusterSizeX;
	private int clusterSizeY;
	private int clusterSizeZ;
	private int nwSizeinputX;
	private int nwSizeinputY;
	private int hiddenLayers;
	
	private NeuronNetwork[][][] cluster;
	
	public Cluster(int clusterSizeX, int clusterSizeY, int clusterSizeZ,
			int nwSizeinputX, int nwSizeinputY, int hiddenLayers) throws Exception {

		this.clusterSizeX = clusterSizeX;
		this.clusterSizeY = clusterSizeY;
		this.clusterSizeZ = clusterSizeZ;
		this.nwSizeinputX = nwSizeinputX;
		this.nwSizeinputY = nwSizeinputY;
		this.hiddenLayers = hiddenLayers;
		
		cluster = new NeuronNetwork [clusterSizeX][clusterSizeY][clusterSizeZ];
		createCluster();
	}

	private void createCluster() throws Exception{
		
		for (int x = 0; x < clusterSizeX ; x ++){
			for (int y = 0; y < clusterSizeY ; y ++){
				for (int z = 0; z < clusterSizeZ ; z ++){
										
					cluster[x][y][z] = new NeuronNetwork(nwSizeinputX, nwSizeinputY, hiddenLayers);
				}	
			}	
		}
	}
	
	public void setStimuli(float stimuliIn, int X, int Y) throws Exception{
		if (!rangeCheckXY(X, Y))
			throw new Exception("bad range for addressing in neuron network");
		
		//todo-- cluster[X][Y][0].setStimuliInForInputNeuron(stimuliIn);
	}
	
	public void setTrainingValue(float training, int X, int Y) throws Exception{
		if (!rangeCheckXY(X, Y))
			throw new Exception("bad range (training) for addressing in neuron network");
		
		//todo--network[X][Y][nwSizeZ-1].setTrainingValue(training);
	}
	
	public float calcOutputValue(int X, int Y) throws Exception{
		if (!rangeCheckXY(X, Y))
			throw new Exception("bad range (getoutput) for addressing in neuron network");
		return 0f;
		//todo --return network[X][Y][nwSizeZ-1].calculateOutput(clockCycle++);
	}
	
	public void train() throws Exception{
		//train(autoMaxTrainAttempts, centerOutputNeuronX, centerOutputNeuronY);
	}
	
	
	private boolean rangeCheckXY(int X, int Y){
		if(X >= nwSizeinputX || Y >= nwSizeinputY){
			return false;			
		}else{
			return true;
		}
	}
}
