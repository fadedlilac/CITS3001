package CITS3001;

import java.util.*;

abstract class Searcher{

	public abstract Board.Direction nextMove(Board start, List<Integer> nextPiece);
	
	public void printStats(Board curBoard, int moves, long duration)
	{
		//System.err.println("Searcher\tHeuristic\tScore\tMov/S:\t");
		System.err.print(this+"\t"+curBoard.scoreOfBoard()+"\t");
		System.err.format("%.3f\n", 1.0e9*moves/duration);
		//System.err.println("Searcher:\t" + this);
		//System.err.println("Score:\t" + curBoard.scoreOfBoard());
		//System.err.println("Played:\t" + moves);	
		//System.err.format("TotalT:\t%.3f\n", 1.0e-9*duration);
		//System.err.format("Mov/S:\t%.3f\n", 1.0e9*moves/duration);
		//System.err.println("\n\n---------------\n\n");
	
	}
	
	public long playGame(final Board start, List<Integer> nextPiece)
	{
		//System.out.println(start);
		//System.out.println("avail moves: " + nextPiece.size());
		
		Board curBoard = new Board(start);
		Board.Direction nextDirection;
		int moves = 0;
		long startTime = System.nanoTime();
		do
		{
			nextDirection = this.nextMove(curBoard, nextPiece);
			if (nextDirection != null)
			{
				movePrint(nextDirection);
				curBoard = curBoard.move(nextDirection, nextPiece.get(0));
				nextPiece.remove(0);
				moves++;
			}		
		}while(nextDirection != null && !nextPiece.isEmpty());
		
		long duration = System.nanoTime() - startTime;
		System.out.println();
		System.out.print(curBoard);
		printStats(curBoard, moves, duration);
		return curBoard.scoreOfBoard();
	}
	
	int printCount = 1;
	public void movePrint( CITS3001.Board.Direction nextDirection)
	{		
		if (printCount > 75){
			System.out.println(nextDirection);
			printCount = 0;
		}
		else
			System.out.print(nextDirection);
		printCount++;
	}
}
