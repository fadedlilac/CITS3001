package CITS3001;

import java.util.*;


class Tester
{
	//for given board, emit the terminal board the heuristic has targeted
	static void testSingleMove()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		/*
		double min = 0.7;
		double max = 1.5;
		int num = 10;
		double ratioInc = Math.pow(max/min, 1.0/num);
		
		double weight1 = 1;
		double weight2 = min;
		
		while(weight2 < max)
		{			
			singleMoveTester(b, 12, new ArrayList<Integer>(nextPiece), 
				new LinearWeightedSum(weight1, weight2));
			weight2 *= ratioInc;			
		}
		*/
		Heuristic h1 = new BoardScore();
		Heuristic h3 = new BoardScoreNormalised();
		Heuristic h2 = new Empties();
		Heuristic h4 = new AdjacentMatches();
		Heuristic h5 = new AdjacentRuns();
		Heuristic h6 = new LinearWeightedSum();
		for (int i = 10; i <= 10; i++){
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h1);
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h2);
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h3);
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h4);
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h5);
			singleMoveTester(b, i, new ArrayList<Integer>(nextPiece), h6);
		}
	}
	
	//exclusively for inspecting the heuristics choice.
	static void singleMoveTester(Board start, int maxDepth, ArrayList<Integer> nextPiece, Heuristic h)
	{			
		maxDepth = Math.min(maxDepth, nextPiece.size());
		
		double bestLiveScore = -1;
		double bestDeadScore = -1;
		Board.Direction bestLiveDirection = null; //cus why not?
		Board.Direction bestDeadDirection = null; //cus why not?
		Board bestLiveBoard = null;
		Board bestDeadBoard = null;
		
		Deque<StackItem> stack = new ArrayDeque<StackItem>();
		//add the first round seperately so we know which move to return
		for (Board.Direction d : Board.Direction.values())
		{					
			Board next = start.move(d, nextPiece.get(0));
			if (next != null){
				stack.push(new StackItem(next,1, d));
			}
		}
		//DFS
		while(!stack.isEmpty()){
			StackItem cur = stack.pop();			
			
			//add more moves if not beyond max depth
			boolean added = false;
			if (cur.d < maxDepth){
				for (Board.Direction d : Board.Direction.values())
				{					
					Board next = cur.b.move(d, nextPiece.get(cur.d));
					if (next != null){
						stack.push(new StackItem(next,cur.d+1, cur.move));
						added = true;
					}
				}
			}
			//update live only at the bottom of the tree
			if (cur.d == maxDepth)
			{
				double curScore = h.useHeuristic(cur.b);
				if (curScore > bestLiveScore)
				{
					bestLiveScore = curScore;
					bestLiveDirection = cur.move;
					bestLiveBoard = cur.b;
				}				
			}
			//update dead, only if we have no children
			else if (!added)
			{
				double curScore = h.useHeuristic(cur.b);
				if (curScore > bestDeadScore)
				{
					bestDeadScore = curScore;
					bestDeadDirection = cur.move;
					bestDeadBoard = cur.b;
				}				
			}
			
			
		}
		if (bestLiveDirection != null)
			System.out.println("Live\n" + bestLiveBoard);
		else
		 	System.out.println("Dead\n" + bestDeadBoard);
		System.out.println("SingleMove: " + maxDepth);
		System.out.println("Heuristic: " + h);
		
		if (bestLiveDirection != null)
		{
			System.out.println("ScoreH: " + bestLiveScore);
			System.out.println("ScoreR: " + bestLiveBoard.scoreOfBoard());
		}
		else
		{
			System.out.println("ScoreH: " + bestDeadScore);
			System.out.println("ScoreR: " + bestDeadBoard.scoreOfBoard());
		}
		System.out.println("----------------------");
	}
	
	//compare increasing depth, similar total leaves (time)
	static void testPruner1()
	{
		Scanner in = new Scanner(System.in);
		final Board start = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		//benchmark 11 depth DLDLFS (~4 mov/s)
		Searcher s = new DLDFS(new BoardScoreNormalised(), 11);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 11, 1.0/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 12, 1.0/4/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 13, 1.0/4/4/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 14, 1.0/4/4/4/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 15, 1.0/4/4/4/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new BoardScoreNormalised(), 20, 1.0/4/4/4/4/4/4/4/4/4/4);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
	}
	
	//compare decreasing exploration (increase speed)
	static void testPruner2()
	{
		Scanner in = new Scanner(System.in);
		final Board start = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		//final int DEPTH = 11;
		for (int i = 3; i < 10; i++){
			Searcher s = new Pruner( new LinearWeightedSum(), 10, 1/Math.pow(4,(i)));
			s.playGame(start, new ArrayList<Integer>(nextPiece));
		}
		//benchmark 11 depth DLDLFS (~4 mov/s);

		/*
		Searcher s = new DLDFS( new LinearWeightedSum(), DEPTH);
		s.playGame(start, new ArrayList<Integer>(nextPiece));

		s = new Pruner( new LinearWeightedSum(), DEPTH, 1);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.75);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.5);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.25);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.2);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		*/
		/*
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.15);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.1);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.05);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.025);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		
		s = new Pruner( new LinearWeightedSum(), DEPTH, 0.02);
		s.playGame(start, new ArrayList<Integer>(nextPiece));
		*/
		
	}
	
	static void testHeuristicValue()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Heuristic hAll [] = 
		{
			new BoardScore(),
			new BoardScoreNormalised(),
			//new Empties(),
			//new AdjacentMatches(),
			//new AdjacentRuns(),
			//new LinearWeightedSum(),
		};
		
		System.err.println(b);
		for (Heuristic h : hAll)
		{
			System.err.println(h.useHeuristic(b));
		}
	}
	
	//compare all the heuristics by themselves
	static void testAllH()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Heuristic hAll [] = 
		{
			//new BoardScore(),
			//new BoardScoreNormalised(),
			//new Empties(),
			//new AdjacentMatches(),
			//new AdjacentRuns(),
			new LinearWeightedSum(),
		};
		
		System.err.println("Searcher\tHeuristic\tScore\tMov/S:\t");
		for (Heuristic h : hAll)
		{
			for (int i = 4; i <= 30; i++){
				Searcher s = new DLDFS(h, i);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
			}			
		}
	}	
	
	//sweep some weights in the lin weighted sum
	static void testWeights()
	{
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		double min = 0.7;
		double max = 1.5;
		int num = 10;
		//amount to mult by each time to get ~ num trials
		double ratioInc = Math.pow(max/min, 1.0/(num*1.01));
		
		double weight1 = 1;
		double weight2 = min;		
		while(weight2 < max)
		{
			Searcher s = new DLDFS(new LinearWeightedSum(), 10);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
			weight2 *= ratioInc;			
		}	
	}
	
	
	
	static void testStrategy()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Searcher s = new Strategy();
		s.playGame(b, new ArrayList<Integer>(nextPiece));
		
	}
	
	static void testStrategyBT()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
			
		Searcher hard = new Pruner(new LinearWeightedSum(), 13, 0.01);//~1M
		Searcher easy = new Jogger(new LinearWeightedSum(), 10, 5);
		Searcher s = new StrategyBT(30, hard, easy);
		s.playGame(b, new ArrayList<Integer>(nextPiece));
	}
	
	
	
	static void testStrategyBT2()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		for (int i = 4; i < 10; i++)
		{
			Searcher s = new StrategyBT( 30, 
				new Pruner(new LinearWeightedSum(), 13, 1.0/64),
				new Pruner(new LinearWeightedSum(), i, 1.0/2));
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
	}
	
	static void testWide()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Searcher s;
		for (int i = 3; i < 9; i++){
			s = new WideSearch( new LinearWeightedSum(), i);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
	
	}
	
	static void testJogger()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		Searcher s;
		for (int i = 2; i <7; i++){
			for(int j= 1; j < i; j++){
				s = new Jogger(new LinearWeightedSum(), i, j);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
				}
		}
	}
	
	static void testBSN()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		for (int i = 6; i < 20; i++){
			Searcher s = new DLDFS(new BoardScoreNormalised(), i); //
			s.playGame(b, new ArrayList<Integer>(nextPiece));		
		}
	}
	
	
	
	public static void main(String[] args){
		//testPruner2();
		//testAll();
		//testStrategy();
		//testStrategyBT();
		//testStrategyBT2();
		//testAllH();
		//testWide();
		testJogger();
		//testHeuristicValue();
		//testBSN();
	}
	
		
}
