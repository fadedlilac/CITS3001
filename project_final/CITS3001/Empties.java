package CITS3001;

import java.util.*;


//this heuristic counts the number of empty squares on the board
//more empty squares means more move choices, which is good
//it is also similar to measuring the board score
//this is because for given depth, more empties means more combines have been done,
//meaning higher score. 
//currently rating linearly up to 7 empties
class Empties
 	implements Heuristic{	
	public double useHeuristic(Board b )
	{
		int counter = 0;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if (b.boardState[i][j] == 0)
					counter++;
			}
		}
		return adjust(counter);
	}
	
	static double conv[] = 
	{
		0,
		.16,
		.32,
		.48,
		.64,
		.8,
		.96,
		1.0,
	};
	private double adjust(int count)
	{
		if (count < conv.length)
			return conv[count];
		else
			return 1.0;
	}
	
	public String toString()
	{
		return "Empties";
	}
}
	
