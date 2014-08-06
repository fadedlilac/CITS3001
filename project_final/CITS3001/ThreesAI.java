package CITS3001;

import java.util.*;


class ThreesAI
{
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
	
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
	
	
		Jogger easy1 = new Jogger(new BoardScore(), 6, 4);
		Searcher easy2 = new Pruner(new Empties(), 9, 1.0/1024);
		Searcher hard2 = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), 15, 1.0/1024/1024);		
	
		Searcher s;
		
		if (args.length > 0)
		{
			//Mu
			s = new StrategyBT(30, hard2, easy2);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
		else
		{
			//beta
			s = new StrategyBTJogger(30, hard2, easy1);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
	}
}
