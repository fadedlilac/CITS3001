package CITS3001;

import java.util.*;

//complex strategies
class StrategyBT extends Searcher {		
	final int backTrackAmount;
	Searcher hardSearch;
	Searcher easySearch;
	
	int backtrackcount = 0;
	
	public String toString()
	{
		return "StrategyBT:\n" 
			+ String.format("\tbackTrackAmount: %d\n\thardSearch: %s\n\teasySearch: %s\n\tbacktracked: %d",
						backTrackAmount,hardSearch,easySearch, backtrackcount);
	}
	
	StrategyBT()
	{
		this(30);
	}	
	
	//backTrackAmount is how far to roll back after a death, 
	//we then do harder searching until we have made 2* backtrack moves
	StrategyBT(int backTrackAmount)
	{
		this.backTrackAmount = backTrackAmount;
		hardSearch = new Pruner(new LinearWeightedSum(), 13, 0.01);//~1M
		easySearch = new Pruner(new LinearWeightedSum(), 8, 1.0/2);//~16k
	}
	
	StrategyBT(int backTrackAmount, Searcher hardSearch, Searcher easySearch)
	{
		this.backTrackAmount = backTrackAmount;
		this.hardSearch = hardSearch;
		this.easySearch = easySearch;
	}
		
	
	//play ez moves until death, then backtrack and play hard moves for a bit, then go back to ezs
	public long playGame(final Board start, List<Integer> nextPiece)
	{
		System.out.println(start);
		System.out.println("avail moves: " + nextPiece.size());
		
		Board.Direction nextDirection;
		int moves = 0;
		long startTime = System.nanoTime();
		
		ArrayList<Board.Direction> moveSequence = new ArrayList<Board.Direction>();
		ArrayList<Board> boardSequence = new ArrayList<Board>();
		boardSequence.add(new Board(start));
		
		do
		{
			
			System.err.format("Mov/S: %.3f\r", 1.0e9*moves/(System.nanoTime() - startTime));
			if (moves%50 == 0)
				System.err.format("\n");
			
			nextDirection = nextMove( 
										boardSequence.get(moves), 
										nextPiece.subList(moves, nextPiece.size()));
			//didnt die, move along
			if (nextDirection != null)
			{
				moveSequence.add(nextDirection);
				boardSequence.add(boardSequence.get(moves).move(nextDirection, nextPiece.get(moves)));
				moves++;
			}
			else //did die, backtrack and do slow moves
			{
				System.err.println();
				System.err.println("bt: " + moves);
				
				backtrackcount++;
				int backtrack = Math.min(moves, backTrackAmount);
				moveSequence.subList(moveSequence.size() - backtrack, moveSequence.size()).clear();
				boardSequence.subList(boardSequence.size() - backtrack, boardSequence.size()).clear();
				moves -= backtrack;
				//do 2 * as many slow moves as we deleted TODO
				backtrack *= 2;				
				do
				{
					System.err.format("Mov/S: %.3f\r", 1.0e9*moves/(System.nanoTime() - startTime));
					if (moves%50 == 0)
						System.err.format("\n");
					nextDirection = nextMoveHard( 
												boardSequence.get(moves), 
												nextPiece.subList(moves, nextPiece.size()));
					//didnt die, move along
					if (nextDirection != null)
					{
						moveSequence.add(nextDirection);
						boardSequence.add(boardSequence.get(moves).move(nextDirection, nextPiece.get(moves)));
						moves++;
					}								
				}while(nextDirection != null && moves < nextPiece.size() && backtrack-- > 0 );
			}				
		}while(nextDirection != null && moves < nextPiece.size());
		
		
		for (Board.Direction d: moveSequence)
			movePrint(d);
		
		long duration = System.nanoTime() - startTime;
		System.err.println();
		System.err.print(boardSequence.get(boardSequence.size()-1));
		System.err.print(this + "\t");
		System.err.print(boardSequence.get(boardSequence.size()-1).scoreOfBoard() + "\t");
		System.err.print("Played: " + moves + "\t");	
		System.err.format("TotalT: %.3f\n", 1.0e-9*duration);
		System.err.format("Mov/S: %.3f\t", 1.0e9*moves/duration);
		System.err.println("---------------");
		return boardSequence.get(boardSequence.size()-1).scoreOfBoard();
	}
		
	public Board.Direction nextMove(Board start, List<Integer> nextPiece)
	{		
		return easySearch.nextMove(start, nextPiece);		
	}	
	
	public Board.Direction nextMoveHard(Board start, List<Integer> nextPiece)
	{
		return hardSearch.nextMove(start, nextPiece);	
	}	
}
