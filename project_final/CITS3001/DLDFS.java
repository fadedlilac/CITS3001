package CITS3001;

import java.util.*;


class DLDFS extends Searcher{	
	final int exploreDepth;
	final Heuristic heuristic;
	
	public String toString()
	{
		return "DLDFS " + exploreDepth + "\t" + heuristic;
	}
	
	DLDFS()
	{
		this(new LinearWeightedSum(), 9);
	}
	
	DLDFS( Heuristic h, int depth)
	{
		this.exploreDepth = depth;
		this.heuristic = h;
	}
	
	//returns the single move to make given a board, depth, piecelist, heuristic
	//whill choose the best terminal board at the max depth, 
	//or if none exists, the best board with no children (inevitable death)
	public Board.Direction nextMove(Board start, List<Integer> nextPiece)
	{			
		int maxDepth = Math.min(exploreDepth, nextPiece.size());
		
		double bestLiveScore = -1;
		double bestDeadScore = -1;
		Board.Direction bestLiveDirection = null; //cus why not?
		Board.Direction bestDeadDirection = null; //cus why not?
		
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
			//if (stack.size() > 35) System.out.println(stack.size());
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
				double curScore = heuristic.useHeuristic(cur.b);
				if (curScore > bestLiveScore)
				{
					bestLiveScore = curScore;
					bestLiveDirection = cur.move;
					//System.err.println(cur.b);
					//System.err.println(curScore);
				}				
			}
			//update dead, only if we have no children
			else if (!added)
			{
				double curScore = heuristic.useHeuristic(cur.b);
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

class StackItem
{
	Board b;
	int d;
	Board.Direction move;
	
	public StackItem(Board b, int d, Board.Direction move)
	{
		this.b = b;
		this.d = d;
		this.move = move;
	}
}

