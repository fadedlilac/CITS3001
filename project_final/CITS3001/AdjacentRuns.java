package CITS3001;

import java.util.*;

//count adjacent squares that are one off combining. 
//is a measure of how ordered the board is, ordered is good for long runs of combines.
//a better measure would weight 2vs3vs4 runs differently, 
//not sure if it is good to add logic about runs finishing in corners and stuff here?
//to adjust to 1.0 scaling we are using sqrt(count)/sqrt(const). 
//so there is a max benefit, and nonlinear scaling.
class AdjacentRuns
 	implements Heuristic{	
 	
	public double useHeuristic(Board b)
	{	
		return countRuns(b);
	}

	public static double countRuns(Board b)
	{
		int run = 0;
		//LEFT/RIGHT
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 3; j++){
				if (nextNum(b.boardState[i][j], b.boardState[i][j+1])){
					run++;	
				}
				if (nextNum(b.boardState[i][j+1], b.boardState[i][j])){
					run++;	
				}
			}
		}
		//UP/DOWN
		for (int j = 0; j < 4; j++){
			for (int i = 0; i < 3; i++){
				if (nextNum(b.boardState[i][j], b.boardState[i+1][j])){
					run++;	
				}
				if (nextNum(b.boardState[i+1][j], b.boardState[i][j])){
					run++;	
				}
			}
		}
		return adjToScore(run);
	}
	
	//true when b follows a in the sequence.
	public static boolean nextNum(int a, int b)
	{
		return ((b == 3) && (a == 1 || a == 2))
			|| b == a+a;

	}
	
	//currently sqrt up to 12 runs
	static double conv[] = 
	{
		0	,
		0.2886751346	,
		0.4082482905	,
		0.5	,
		0.5773502692	,
		0.6454972244	,
		0.7071067812	,
		0.7637626158	,
		0.8164965809	,
		0.8660254038	,
		0.9128709292	,
		0.9574271078	,
		1	,
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
		return "AdjacentRuns";
	}
}
	
