package CITS3001;

import java.util.*;


class Jogger extends Searcher{	
	final int exploreDepth;
	final int moveDepth;
	final Heuristic heuristic;
	
	public String toString()
	{
		return "Jogger\t" + exploreDepth + " "+ moveDepth+ "\t"  + heuristic;
	}
	
	Jogger()
	{
		this(new LinearWeightedSum(), 12, 6);
	}
	
	Jogger( Heuristic h, int exploreDepth, int moveDepth)
	{
		this.exploreDepth = exploreDepth;
		this.moveDepth = moveDepth;
		this.heuristic = h;
	}
	
	public BoardRec nextMoveRecurse(Board b, List<Integer> nextPiece, int depth)
	{
		if (depth == Math.min(exploreDepth, nextPiece.size()))
		{
			return new BoardRec(heuristic.useHeuristic(b));
		}			
		else{
			BoardRec ret = null;
			for (Board.Direction d : Board.Direction.values())
			{					
				Board next = b.move(d, nextPiece.get(depth));
				if (next != null){
					BoardRec temp = nextMoveRecurse(next, nextPiece, depth + 1);
					if (temp != null){
						if (ret == null)
							ret = temp.add(d);
						else if (temp.score > ret.score){
							ret = temp.add(d);
						}
					}
				}
			}
			return ret;
		}
	}
	
	public Board.Direction nextMove(Board start, List<Integer> nextPiece)
	{				
		List<Board.Direction> temp =  nextMoveMultiple(start, nextPiece);
		if (temp != null)
			return temp.get(0);
		return null;
	}
	
	//returns the single move to make given a board, depth, piecelist, heuristic
	//whill choose the best terminal board at the max depth, 
	//or if none exists, the best board with no children (inevitable death)
	public List<Board.Direction> nextMoveMultiple(Board start, List<Integer> nextPiece)
	{			
		int maxDepth = Math.min(exploreDepth, nextPiece.size());
		int maxMoves = Math.min(moveDepth, maxDepth);

		BoardRec best = nextMoveRecurse(start, nextPiece, 0);
		if (best != null)
		{
			return best.moveList.subList(0, Math.min(maxMoves, best.moveList.size()));
		}
		return null;
	}
	
	public long playGame(final Board start, List<Integer> nextPiece)
	{
		System.out.println(start);
		System.out.println("avail moves: " + nextPiece.size());
		
		Board curBoard = new Board(start);
		int moves = 0;
		long startTime = System.nanoTime();
		List<Board.Direction> moveList;
		do
		{
			moveList = this.nextMoveMultiple(curBoard, nextPiece);
			if (moveList != null)
			{
				for (Board.Direction d : moveList)
				{
					movePrint(d);
					curBoard = curBoard.move(d, nextPiece.get(0));	
					nextPiece.remove(0);
					moves++;
				}
			}		
		}while(moveList != null && !nextPiece.isEmpty());
		
		long duration = System.nanoTime() - startTime;
		System.err.print(this+"\t"+curBoard.scoreOfBoard()+"\t");
		System.err.format("%.3f\n", 1.0e9*moves/duration);
		/*
		System.out.println();
		System.out.print(curBoard);
		System.out.println("Searcher: " + this);
		System.out.println("Score: " + curBoard.scoreOfBoard());
		System.out.println("Played: " + moves);	
		System.out.format("TotalT: %.3f\n", 1.0e-9*duration);
		System.out.format("Mov/S: %.3f\n", 1.0e9*moves/duration);
		System.out.println("---------------");
		*/
		return curBoard.scoreOfBoard();
	}
}

class BoardRec
{
	final double score;
	List<Board.Direction>	moveList;
	public BoardRec(double score)
	{
		this.score = score;
		
	}
	
	BoardRec add(Board.Direction d)
	{
		if (moveList == null)
			moveList = new ArrayList<Board.Direction>();
		moveList.add(0,d);
		return this;
	}
}

