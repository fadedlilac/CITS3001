package CITS3001;

import java.util.*;


//averages first move,
//chooses best move with best average (horizon effect stuff)
class WideSearch extends Searcher{	
	final int exploreDepth;
	final Heuristic heuristic;
	
	public String toString()
	{
		return "WideSearch " + exploreDepth + "\t"  + heuristic;
	}
	
	WideSearch()
	{
		this(new BoardScoreNormalised(), 12);
	}
	
	WideSearch( Heuristic h, int depth)
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
		Board.Direction bestLiveDirection = null; //cus why not?
				
		
		//add the first round seperately so we know which move to return
		for (Board.Direction d : Board.Direction.values())
		{					
			Board next = start.move(d, nextPiece.get(0));
			if (next != null){
				PriorityQueue<Double> pq = new PriorityQueue<Double>();
				
				Deque<StackItem> stack = new ArrayDeque<StackItem>();
				stack.push(new StackItem(next,1, d));
				//DFS
				while(!stack.isEmpty()){
					StackItem cur = stack.pop();

					//add more moves if not beyond max depth
					if (cur.d < maxDepth){
						for (Board.Direction d2 : Board.Direction.values())
						{					
							Board next2 = cur.b.move(d2, nextPiece.get(cur.d));
							if (next2 != null){
								stack.push(new StackItem(next2,cur.d+1, cur.move));
							}
						}
					}
					//update live only at the bottom of the tree
					if (cur.d == maxDepth)
					{
						pq.add(heuristic.useHeuristic(cur.b));
						if (pq.size() > 10)
							pq.poll();								
					}
				}
				double sum = 0;
				int count = 0;
				count = pq.size();
				while (!pq.isEmpty())
					sum += pq.poll();
				if (count > 0 && sum/count > bestLiveScore){
					bestLiveScore = sum/count;	
					bestLiveDirection = d;
				}	
			}
		}	
		return bestLiveDirection;
	}
}


