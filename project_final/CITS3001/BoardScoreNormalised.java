package CITS3001;

import java.util.*;


//Boardscore with 0.0 - 1.0 scaling
//implemented to enable easy linear weighted sum comparison with other heuristics
class BoardScoreNormalised implements Heuristic 
{	

	public double useHeuristic(Board b)
	{
		return normaliseScore(b);
	}
	

	public String toString()
	{
		return "BoardScoreNormalised";
	}
	
	//attempt to normalise the score so it is comparable to the other heuristics
	//current approach: divide the score by the max possible score of the board
	//find the max score by summing possible 2 * 3 ** k scores
	private double normaliseScore(Board b)
	{
		int count = 0;
		//sum all the squares
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				//if (b.boardState[i][j] >= 3 )
					count += b.boardState[i][j];
			}
		}
		//store the max possible score in maxscore
		//tileS goes from the highest single cell size down
		int maxscore = 0;
		int tileS = 786432;
		while (tileS > count)
		{
				tileS /= 2;
		}					
		while (count > 3)
		{
			
			maxscore += Board.scoreOfTile(tileS);
			count -= tileS;
			tileS /= 2;
		}
		maxscore += Board.scoreOfTile(count);
		
		//System.err.println((double) maxscore);
		return (double)b.scoreOfBoard() / (double) maxscore;
	}
}
	
