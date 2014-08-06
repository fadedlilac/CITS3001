package CITS3001;

import java.util.*;

//complex strategies
class Strategy extends Searcher {		
	boolean first = true;
	int deathDepth = 0;
	int curDepth = 0;
	
	ArrayList<Integer> bestFill;
	
	final int hardDepth;
	final int hardCutoff;
	final int deathCutoff;
	Searcher deathSearch;
	Searcher hardSearch;
	Searcher easySearch;
	
	public String toString()
	{
		return "Strategy:\n" 
			+ String.format("\thardDepth: %d\n\thardCutoff: %d\n\tdeathCutoff: %d\n\tdeathDepth: %d",
						hardDepth,hardCutoff,deathCutoff,deathDepth);
	}
	
	Strategy()
	{
		this(15, 12, 20);
	}	
	
	//hardDepth is how far we lookahead for hard points
	//hardCutoff is how many full cells (worst case) constitute that hard point (eg ~14)
	//deathCutoff is when to kick into maximise score mode (this many moves from certain death)
	Strategy(int hardDepth, int hardCutoff, int deathCutoff)
	{
		this.hardDepth = hardDepth;
		this.hardCutoff = hardCutoff;
		this.deathCutoff = deathCutoff;
		deathSearch = new Pruner(new BoardScoreNormalised(), 13, 1.0/16);//~4M
		hardSearch = new Pruner(new LinearWeightedSum(), 12, 1.0/32);//~1M
		easySearch = new Pruner(new LinearWeightedSum(), 9, 1.0/64);//~16k
	}
		
	public Board.Direction nextMove(Board start, List<Integer> nextPiece)
	{
		if (first)
		{
			first = false;
			predict(start, nextPiece);
		}
		curDepth++;
		int hardness = lookAheadHard();
		if (deathDepth - curDepth < deathCutoff) // we are soon to die, prioritise score
		{
			return deathSearch.nextMove(start, nextPiece);
		}
		else if (hardness >= hardCutoff) // there is a hard bit ahead, do more work
		{
			return hardSearch.nextMove(start, nextPiece);
		}
		else	// must be easy, do something else smart.
		{
			return easySearch.nextMove(start, nextPiece);
		}		
	}
	
	//find how hard the next hardDepth moves are (max)
	private int lookAheadHard()
	{
		int ret = 0;
		for (int j = curDepth; j < curDepth + hardDepth && j < bestFill.size(); j++)
		{
			ret = Math.max(bestFill.get(j), ret);
		}
		return ret;	
	}
	
	//fill our bestFill array and predict certain death
	private void predict(Board start, List<Integer> nextPiece)
	{
		int count1 = 0;
		int count2 = 0;
		int countx = 0;
		bestFill = new ArrayList<Integer>();
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				if (start.boardState[i][j] >= 3 )
					countx += start.boardState[i][j];
				else if (start.boardState[i][j] == 1)
					count1++;
				else if (start.boardState[i][j] == 2)
					count2++;
			}
		}
		for (int i = 0; i < nextPiece.size(); i++)
		{
			int curFill = fillCount(count1, count2, countx);
			bestFill.add(curFill);
			if (curFill > 15) // if 16 cells are needed, we cant even combine.
			{
				deathDepth = i;
				break;
			}			
				
			if (nextPiece.get(i) >= 3 )
				countx += nextPiece.get(i);
			else if (nextPiece.get(i) == 1)
				count1++;
			else if (nextPiece.get(i) == 2)
				count2++;
			//combine our 1s and 2s
			while (count1 > 0 && count2 > 0)
			{
				count1--; count2--;
				countx += 3;
			}
		}
		if (deathDepth == 0)
			deathDepth = Integer.MAX_VALUE;
	}
	
	//return minimum number cells for given input cells
	private static final int fillCount(int count1, int count2, int countx)
	{
		int ret = count1 + count2;
		int tileS = 786432;
		while (countx > 3)
		{
			while (tileS > countx)
				tileS /= 2;
			ret++;
			countx -= tileS;
		}
		return ret;
	}
}
