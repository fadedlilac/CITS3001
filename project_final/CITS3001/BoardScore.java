package CITS3001;

import java.util.*;


//simple BoardScore heuristic. 
//this works well for comparing similar depth boards, 
//it is hard to integrate this with the other measures however as the 
//boardscore increases exponentially with moves
//it can also vary quite a bit, will really favour combining big peices for example,
//need to think of a way to moderate this effect to maintain good board structure
class BoardScore
 	implements Heuristic{	
	public double useHeuristic(Board b)
	{
		
		return (double)b.scoreOfBoard();
	}
	
	public String toString()
	{
		return "BoardScore";
	}
}
	
