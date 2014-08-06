package CITS3001;

import java.util.*;


class FigureStrat
{
	

	static void testStrategies()
		{
			Scanner in = new Scanner(System.in);
			Board b = Board.readBoard(in);
		
			ArrayList<Integer> nextPiece = new ArrayList<Integer>();
			while(in.hasNextInt())
				nextPiece.add(in.nextInt());
		
		
			Jogger easy1 = new Jogger(new BoardScore(), 6, 4);
			Searcher easy2 = new Pruner(new Empties(), 9, 1.0/1024);
			
			Searcher hard1 = new DLDFS(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), 12);
			Searcher hard2 = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), 15, 1.0/1024/1024);		
		
			Searcher s;
		
			//alpha
			s = new StrategyBTJogger(30, hard1, easy1);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		
			//beta
			s = new StrategyBTJogger(30, hard2, easy1);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
			
			//gamma
			s = new StrategyBT(30, hard1, easy2);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
			
			//Mu
			s = new StrategyBT(30, hard2, easy2);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
		
		public static void main(String[] args){
		//scoreAndEmpties();
		testStrategies();
	}
	
	
}
