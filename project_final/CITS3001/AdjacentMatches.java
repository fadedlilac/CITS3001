package CITS3001;

import java.util.*;

//count adjacent squares that can combine. 
//will be useful as a short lookahead heuristic. 
//good to have some score for having these ready to combine. 
//a more general approach could be to measure distance between combinable squares
//to adjust to 1.0 scaling we are using sqrt(count)/sqrt(const). 
//so there is a max benefit.
class AdjacentMatches
 	implements Heuristic{	
 	
	public double useHeuristic(Board b)
	{	
		return countAdjacents(b);
	}

	public static double countAdjacents(Board b)
	{
		int counter = 0;
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				if ( j < 3 && Board.canCombine(b.boardState[i][j], b.boardState[i][j+1])) {
					counter++;
				}
				if ( i < 3 && Board.canCombine(b.boardState[i][j], b.boardState[i+1][j])) {
					counter++;
				}
			}
		}
		return adjToScore(counter);
	}
	
	//currently sqrt up to 6 cells
	static double conv[] = 
	{
		0.0,
		0.4082482905,
		0.5773502692,
		0.7071067812,
		0.8164965809,
		0.9128709292,
		1.0,
	};

	
	
	private static double adjToScore(int count)
	{
		if (count < conv.length)
			return conv[count];
		else
			return 1.0;			
	}	
	
	public String toString()
	{
		return "AdjacentMatches";
	}
}
	
