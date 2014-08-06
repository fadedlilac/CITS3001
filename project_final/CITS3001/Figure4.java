package CITS3001;

import java.util.*;


class Figure4
{
	
	static void scoreAndEmpties()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Heuristic hAll [] = 
		{
			//new BoardScore(),
			new BoardScoreNormalised(),
			new Empties(),
			//new AdjacentMatches(),
			//new AdjacentRuns(),
			//new LinearWeightedSum(),
		};
		
		System.err.println("Searcher\tHeuristic\tScore\tMov/S:\t");
		for (Heuristic h : hAll)
		{
			for (int i = 10; i <= 10; i++){
				Searcher s = new DLDFS(h, i);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
			}			
		}
	}	
	
	static void LWS()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Heuristic hAll [] = 
		{
				new LinearWeightedSum(new double[] {0.3,0.1,0.6,0.0,}),
				new LinearWeightedSum(new double[] {0.7,0.2,0.1,0.0,}),
				new LinearWeightedSum(new double[] {0.2,0.5,0.3,0.0,}),
				new LinearWeightedSum(new double[] {0.3,0.5,0.2,0.0,}),
		};
		
		System.err.println("Searcher\tHeuristic\tScore\tMov/S:\t");
		for (Heuristic h : hAll)
		{
			for (int i = 5; i <= 8; i++){
				Searcher s = new DLDFS(h, i);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
			}			
		}
	}
	
 		
	public static void main(String[] args){
		//scoreAndEmpties();
		LWS();
	}
	
	
}
