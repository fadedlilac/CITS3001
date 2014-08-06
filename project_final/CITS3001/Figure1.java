package CITS3001;

import java.util.*;


class Figure1
{
	
	static void depth()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		
		
		System.err.println("Searcher\tHeuristic\tScore\tMov/S:\t");
		for (int i = 2; i <= 10; i++){
			Searcher s = new DLDFS(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i);
			s.playGame(b, new ArrayList<Integer>(nextPiece));		
		}
	}	
	
 		
	public static void main(String[] args){
		depth();
	}
	
	
}
