package CITS3001;

import java.util.*;

//attempt to explore deeper via pruning
class Pruner extends Searcher {		
	private final int exploreDepth;
	private final int toExplore;
	private final Heuristic heuristic;
	
	public String toString()
	{
		return "Pruner " + exploreDepth + "\t" + toExplore + "\t" + heuristic;
	}
	
	Pruner()
	{
		this(new BoardScoreNormalised(), 12, 1.0/1000);
	}
	
	Pruner( Heuristic h, int depth, double exploreFraction)
	{
		this.exploreDepth = depth;
		this.toExplore = (int) (Math.pow(4, depth)*exploreFraction);
		this.heuristic = h;
	}
	
	public Board.Direction nextMove(Board start, List<Integer> nextPiece)
	{
		int maxDepth = Math.min(exploreDepth, nextPiece.size());
		
		double bestLiveScore = -1;
		double bestDeadScore = -1;
		Board.Direction bestLiveDirection = null; //cus why not?
		Board.Direction bestDeadDirection = null; //cus why not?
		
		PriorityQueue<PQBoard> pq = new PriorityQueue<PQBoard>(4,new PQBoardComparator());
		//add the first round seperately so we know which move to return
		for (Board.Direction d : Board.Direction.values())
		{					
			Board next = start.move(d, nextPiece.get(0));
			if (next != null){
				pq.add(new PQBoard(next, 1, d, heuristic));
			}
		}
		
		int exploredAtDepth = 0;
		//PFS
		while(pq.size() != 0
			&& exploredAtDepth < toExplore){
			PQBoard cur = pq.poll();
			
			
			//add more moves if not beyond max depth
			boolean added = false;
			if (cur.depth < maxDepth){
				for (Board.Direction d : Board.Direction.values())
				{					
					Board next = cur.board.move(d, nextPiece.get(cur.depth));
					if (next != null){
						pq.add(new PQBoard(next, cur.depth + 1, cur.move, heuristic));
						added = true;
					}
				}
			}
			//update live only at the bottom of the tree
			if (cur.depth == maxDepth)
			{
				exploredAtDepth++;
				double curScore = cur.hScore;
				if (curScore > bestLiveScore)
				{
					bestLiveScore = curScore;
					bestLiveDirection = cur.move;
				}				
			}
			//update dead, only if we have no children
			else if (!added)
			{
				double curScore = cur.hScore;
				if (curScore > bestDeadScore)
				{
					bestDeadScore = curScore;
					bestDeadDirection = cur.move;
				}				
			}		
		}
		if (bestLiveDirection != null)
			return bestLiveDirection;
		return bestDeadDirection;
	}
}
	
class PQBoardComparator implements Comparator<PQBoard>
{
	public int compare(PQBoard a, PQBoard b)
	{
		if (a.hScore > b.hScore)
			return -1;
		return 1;
	}
}
	
class PQBoard
{
	Board board;
	int depth;
	Board.Direction move;
	double hScore;
	
	public PQBoard(Board board, int depth, Board.Direction move, Heuristic h)
	{
		this.board = board;
		this.depth = depth;
		this.move = move;
		this.hScore = h.useHeuristic(board);
	}
}
