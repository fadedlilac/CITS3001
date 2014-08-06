package CITS3001;

import java.util.*;

//first demo of linear weighted sum.
//will be tedious adding more things, 
//probably need to implement somehow with a vector<heuristic> or something
//we use static heuristics, and instance weights to do stuff.
class LinearWeightedSum
 	implements Heuristic{	
 	
 	static Heuristic heuristics[] = {
 		new BoardScoreNormalised(),
 		new Empties(),
 		new AdjacentMatches(),
 		new AdjacentRuns(),
 	};
 	
 	double weights[];
 	
 	public LinearWeightedSum()
 	{
 		this.weights = new double[] {
	 		0.2,
	 		0.5,
	 		0.2,
	 		0.1,
 		};
 	}
 	
 	public LinearWeightedSum( double weights[])
 	{
 		this.weights = weights;
 	}
 	
	public double useHeuristic(Board b)
	{	
		//System.out.println(b);
		double ret = 0.0;
		for (int i = 0; i < heuristics.length; i++){
			ret += weights[i] * heuristics[i].useHeuristic(b);
			
			//System.out.println(t);			
		}
		//System.out.println(ret);
		return ret;
	}
	
	public String toString()
	{
		String ret = "LinearWeightedSum: ";
		for (double i : weights)
		{
			ret += "\t" + i;
		}
		return ret;		
	}
}
	
