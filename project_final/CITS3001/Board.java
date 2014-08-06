package CITS3001;

import java.util.*;
import java.lang.StringBuilder;


class Board {	
	//ROWS, COLUMNS
	//[0][3] = top row, rightmost column
	public int boardState[][] = new int[4][4];
	
	
	public static final int scoreOfTile(int x)
	{
		switch(x)
		{
			case 	1	: return	1	;
			case 	2	: return	1	;
			case 	3	: return	3	;
			case 	6	: return	9	;
			case 	12	: return	27	;
			case 	24	: return	81	;
			case 	48	: return	243	;
			case 	96	: return	729	;
			case 	192	: return	2187	;
			case 	384	: return	6561	;
			case 	768	: return	19683	;
			case 	1536	: return	59049	;
			case 	3072	: return	177147	;
			case 	6144	: return	531441	;
			case 	12288	: return	1594323	;
			case 	24576	: return	4782969	;
			case 	49152	: return	14348907	;
			case 	98304	: return	43046721	;
			case 	196608	: return	129140163	;
			case 	393216	: return	387420489	;
			case 	786432	: return	1162261467	;
			default : return 0;
		}
	}
	
	public Board(int[] tiles){
		int counter = 0;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				boardState[i][j]= tiles[counter];
				counter++;
			}
		}
	}
	
	public Board(Board toCopy)
	{	
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				boardState[i][j]= toCopy.boardState[i][j];
			}
		}
	}
	
	public static Board readBoard(Scanner in)
	{	
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());				
		return new Board(Board.reader(in));
	}
	
	
	
	
	public long scoreOfBoard(){
		long score = 0; 
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				score += scoreOfTile(boardState[i][j]);
			}
		}
		return score;
	}
	
	public static enum Direction
	{
		L,
		R,
		U,
		D,
	}
	
	public Board move(Direction dir, int nextPiece)
	{
		switch(dir){
			case L: 	return this.moveLeft(nextPiece); 
			case R: return this.moveRight(nextPiece);
			case U: 		return this.moveUp(nextPiece);
			case D: 	return this.moveDown(nextPiece);
		}
		return null;
	}
	
	//return new board after a down move.
	private Board moveDown(int nextPiece){
		Board newBoard = new Board(this);
		
		//true for the cells that did move, used for nextPiece
		boolean moved[] = {false, false, false, false};
		
		//each column
		for (int j = 0; j < 4; j++)
		{
			//always points to the previous inspected cell
			int last = newBoard.boardState[3][j];	
			//for the 3 cells that can move
			for (int i = 2; i >= 0; i--){
				int cur = newBoard.boardState[i][j];
				if (cur == 0) 
				{	
					last = newBoard.boardState[i][j];
					continue;
				}
				if (last == 0){ //empty cell, slide all remaining
					for (int slide = i + 1; slide >= 1; slide--)
					{
						newBoard.boardState[slide][j] = newBoard.boardState[slide - 1][j];
					}
					newBoard.boardState[0][j] = 0;
					moved[j] = true;
					break;
				}
				else if (canCombine(last, cur)) //combine these two and continue
				{
					newBoard.boardState[i+1][j] += cur;
					newBoard.boardState[i][j] = 0;
					moved[j] = true;
				}
				last = newBoard.boardState[i][j];
			}
		}
		
		boolean any = false;
		for (boolean t : moved) // check if any rows were moved
		{
			any |= t;
		}
		if (any)
		{
			newBoard.boardState[0][newBoard.insertRowDown(moved)] = nextPiece;
			return newBoard;
		}
		return null;
	}
	
	//return the index of where to place the next piece after a down move
	private int insertRowDown(boolean moved[])
	{
		int best = -1;
		for (int j = 3; j >= 0; j--)
		{
			if (!moved[j])
				continue;
			if (best == -1)
				best = j;
			else 
			{ 
				//find the first non matching cell i
				int i = 0;
				while( i < 4 
					&& boardState[i][j] == boardState[i][best])
				{
					i++;
				}
				//update best if, when equal we want to keep the old best
				if (i < 4 
					&& boardState[i][j] < boardState[i][best])
					best = j;
			}
		}
		return best;
	}
	
	//return new board after a down move.
	private Board moveLeft(int nextPiece){
		Board newBoard = new Board(this);
		
		//true for the cells that did move, used for nextPiece
		boolean moved[] = {false, false, false, false};
		
		//each row
		for (int i = 0; i < 4; i++)
		{
			//always points to the previous inspected cell
			int last = newBoard.boardState[i][0];	
			//for the 3 cells that can move
			for (int j = 1; j < 4; j++){
				int cur = newBoard.boardState[i][j];
				if (cur == 0) 
				{	
					last = newBoard.boardState[i][j];
					continue;
				}
				if (last == 0){ //empty cell, slide all remaining
					for (int slide = j - 1; slide < 3; slide++)
					{
						newBoard.boardState[i][slide] = newBoard.boardState[i][slide+1];
					}
					newBoard.boardState[i][3] = 0;
					moved[i] = true;
					break;
				}
				else if (canCombine(last, cur)) //combine these two and continue
				{
					newBoard.boardState[i][j-1] += cur;
					newBoard.boardState[i][j] = 0;
					moved[i] = true;
				}
				last = newBoard.boardState[i][j];
			}
		}
		
		boolean any = false;
		for (boolean t : moved) // check if any rows were moved
		{
			any |= t;
		}
		if (any)
		{
			newBoard.boardState[newBoard.insertRowLeft(moved)][3] = nextPiece;
			return newBoard;
		}
		return null;
	}
	
	//return the index of where to place the next piece after a down move
	private int insertRowLeft(boolean moved[])
	{
		int best = -1;
		for (int i = 3; i >= 0; i--)
		{
			if (!moved[i])
				continue;
			if (best == -1)
				best = i;
			else 
			{ 
				//find the first non matching cell i
				int j = 3;
				while( j >= 0 
					&& boardState[i][j] == boardState[best][j])
				{
					j--;
				}
				//update best if, when equal we want to keep the old best
				if (j >= 0 
					&& boardState[i][j] < boardState[best][j])
					best = i;
			}
		}
		return best;
	}
	
	//return new board after a down move.
	private Board moveRight(int nextPiece){
		Board newBoard = new Board(this);
		
		//true for the cells that did move, used for nextPiece
		boolean moved[] = {false, false, false, false};
		
		//each row
		for (int i = 0; i < 4; i++)
		{
			//always points to the previous inspected cell
			int last = newBoard.boardState[i][3];	
			//for the 3 cells that can move
			for (int j = 2; j >=0; j--){
				int cur = newBoard.boardState[i][j];
				if (cur == 0) 
				{	
					last = newBoard.boardState[i][j];
					continue;
				}
				if (last == 0){ //empty cell, slide all remaining
					for (int slide = j + 1; slide >= 1; slide--)
					{
						newBoard.boardState[i][slide] = newBoard.boardState[i][slide-1];
					}
					newBoard.boardState[i][0] = 0;
					moved[i] = true;
					break;
				}
				else if (canCombine(last, cur)) //combine these two and continue
				{
					newBoard.boardState[i][j+1] += cur;
					newBoard.boardState[i][j] = 0;
					moved[i] = true;
				}
				last = newBoard.boardState[i][j];
			}
		}
		
		boolean any = false;
		for (boolean t : moved) // check if any rows were moved
		{
			any |= t;
		}
		if (any)
		{
			newBoard.boardState[newBoard.insertRowRight(moved)][0] = nextPiece;
			return newBoard;
		}
		return null;
	}
	
	//return the index of where to place the next piece after a down move
	private int insertRowRight(boolean moved[])
	{
		int best = -1;
		for (int i = 0; i < 4; i++)
		{
			if (!moved[i])
				continue;
			if (best == -1)
				best = i;
			else 
			{ 
				//find the first non matching cell i
				int j = 0;
				while( j < 4 
					&& boardState[i][j] == boardState[best][j])
				{
					j++;
				}
				//update best if, when equal we want to keep the old best
				if (j < 4 
					&& boardState[i][j] < boardState[best][j])
					best = i;
			}
		}
		return best;
	}
	
	//return new board after a down move.
	private Board moveUp(int nextPiece){
		Board newBoard = new Board(this);
		
		//true for the cells that did move, used for nextPiece
		boolean moved[] = {false, false, false, false};
		
		//each column
		for (int j = 0; j < 4; j++)
		{
			//always points to the previous inspected cell
			int last = newBoard.boardState[0][j];	
			//for the 3 cells that can move
			for (int i = 1; i < 4; i++){
				int cur = newBoard.boardState[i][j];
				if (cur == 0) 
				{	
					last = newBoard.boardState[i][j];
					continue;
				}
				if (last == 0){ //empty cell, slide all remaining
					for (int slide = i - 1; slide < 3; slide++)
					{
						newBoard.boardState[slide][j] = newBoard.boardState[slide+1][j];
					}
					newBoard.boardState[3][j] = 0;
					moved[j] = true;
					break;
				}
				else if (canCombine(last, cur)) //combine these two and continue
				{
					newBoard.boardState[i-1][j] += cur;
					newBoard.boardState[i][j] = 0;
					moved[j] = true;
				}
				last = newBoard.boardState[i][j];
			}
		}
		
		boolean any = false;
		for (boolean t : moved) // check if any rows were moved
		{
			any |= t;
		}
		if (any)
		{
			newBoard.boardState[3][newBoard.insertRowUp(moved)] = nextPiece;
			return newBoard;
		}
		return null;
	}
	
	//return the index of where to place the next piece after a down move
	private int insertRowUp(boolean moved[])
	{
		int best = -1;
		for (int j = 0; j < 4; j++)
		{
			if (!moved[j])
				continue;
			if (best == -1)
				best = j;
			else 
			{ 
				//find the first non matching cell i
				int i = 3;
				while( i >= 0 
					&& boardState[i][j] == boardState[i][best])
				{
					i--;
				}
				//update best if, when equal we want to keep the old best
				if (i >= 0 
					&& boardState[i][j] < boardState[i][best])
					best = j;
			}
		}
		return best;
	}
	
	//test if A and B can be combined (1 and 2, or >3 and equal)
	public static boolean canCombine(int A, int B){
		return ((A+B == 3) || (A>2 && A==B));
	}
	
	//read 16 ints from scanner to create a board
	public static int [] reader (Scanner in)
	{
		int start[] = new int[16];
		for(int i = 0; i< 16; i++){
			start[i] = in.nextInt();
			
		}
		return start;
	}

	public String toString(){
		StringBuilder tmp = new StringBuilder();
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				tmp.append(boardState[i][j] + "\t");
			}
			tmp.append("\n\n");
		}
		return tmp.toString();
	}


	//test Up left down right correctness
	public static void testUDLR(){
		Scanner in = new Scanner(System.in);
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());
				
		Board bo = new Board(Board.reader(in));
		System.out.print(bo);
		
		Board bo2 = bo.move(Direction.D, in.nextInt());
		System.out.println("move down:");
		System.out.print(bo2);
		
		bo2 = bo.move(Direction.U, in.nextInt());
		System.out.println("move up:");
		System.out.print(bo2);
		
		bo2 = bo.move(Direction.L, in.nextInt());
		System.out.println("move left:");
		System.out.print(bo2);
		
		bo2 = bo.move(Direction.R, in.nextInt());
		System.out.println("move right:");
		System.out.print(bo2);
	}
	
	//test that we can run moves on the input
	public static void testMoves(){
		Scanner in = new Scanner(System.in);
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());
				
		Board bo = new Board(Board.reader(in));
		System.out.print(bo);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		Board next;
		boolean moved = true;
		while(moved && !nextPiece.isEmpty())
		{
			moved = false;
			for (Direction d : Direction.values())
			{
				next = bo.move(d, nextPiece.get(0));
				if (next != null)
				{
					nextPiece.remove(0);
					bo = next;
					moved = true;
					System.out.println( d + " moved");
					break;
				}
			}
			System.out.print(bo);
		}
		
			
	}
	
	public static void testScore(){	
		Scanner in = new Scanner(System.in);
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());
				
		Board bo = new Board(Board.reader(in));
		System.out.print(bo);
		System.out.println("score: " + bo.scoreOfBoard());
	}	
		
	//see how deep / how fast we can calculate boards
	//uses iterative deepening to the depth of the movelist (might be off by 1)
	public static void testSpeed(){
		Scanner in = new Scanner(System.in);
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());
				
		Board bo = new Board(Board.reader(in));
		System.out.print(bo);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		
		
		for (int maxD = 0; maxD < nextPiece.size(); maxD++){
			System.out.print("Doing depth: " + maxD);
			int vis = 0;
			long startTime = System.nanoTime();
			long bestScore = 0;
						
			Deque<Pair> stack = new ArrayDeque<Pair>();
			stack.push(new Pair(bo,0));
			
			while(!stack.isEmpty()){
				Pair cur = stack.pop();
				vis++;
				long curScore = cur.b.scoreOfBoard();
				if (curScore > bestScore){
					bestScore = curScore;
				}
				if (cur.i >= maxD)
						continue;
				else{
					for (Direction d : Direction.values())
					{					
						Board next = cur.b.move(d, nextPiece.get(cur.i));
						if (next != null){
							stack.push(new Pair(next,cur.i+1, cur.moves + d));
						}
					}
				}		
			}
			long duration = System.nanoTime() - startTime;
			System.out.print(" visited: " + vis);
			System.out.print(" v/s: ");
			System.out.format("%.0f", 1.0e9*vis/duration);
			System.out.println(" bestScore: " + bestScore);
		}
	}
	
	//see how deep / how fast we can calculate boards
	//uses iterative deepening to the depth of the movelist (might be off by 1)
	public static void testSpeedMoves(){
		Scanner in = new Scanner(System.in);
		//print the junk
		System.out.println(in.nextLine());
		System.out.println(in.nextLine());
				
		Board bo = new Board(Board.reader(in));
		System.out.print(bo);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		
		
		
		for (int maxD = 0; maxD < nextPiece.size(); maxD++){
			System.out.print("Doing depth: " + maxD);
			int vis = 0;
			long startTime = System.nanoTime();
			long bestScore = 0;
			String bestMove = "";
			
			Deque<Pair> stack = new ArrayDeque<Pair>();
			stack.push(new Pair(bo,0,""));
			
			while(!stack.isEmpty()){
				Pair cur = stack.pop();
				vis++;
				long curScore = cur.b.scoreOfBoard();
				if (curScore > bestScore){
					bestScore = curScore;
					bestMove = cur.moves;
				}
				
				if (cur.i >= maxD)
						continue;
				else{
					for (Direction d : Direction.values())
					{					
						Board next = cur.b.move(d, nextPiece.get(cur.i));
						if (next != null){
							stack.push(new Pair(next,cur.i+1, cur.moves + d));
						}
					}
				}		
			}
			long duration = System.nanoTime() - startTime;
			System.out.print(" visited: " + vis);
			System.out.print(" v/s: ");
			System.out.format("%.0f", 1.0e9*vis/duration);
			System.out.println(" bestScore: " + bestScore);
			System.out.println("moves: " + bestMove);
		}
	}
	
	private static void testMem()
	{
		Queue<Board> q = new ArrayDeque<Board>();
		int count = 0;
		int arr[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		Board a = new Board(arr);
		while (true)
		{
			q.offer(a);
			q.offer(a);
			q.offer(a);
			q.offer(a);
			q.remove();
			q.remove();
			q.remove();
			count++;
			if (count % 100000 == 0)
				System.out.println("count: " + count);
		}
	}
	
	public static void main(String[] args){
		//testUDLR();
		//testMoves();
		//testSpeed();
		//testScore();
		//testSpeedMoves();
		testMem();
	}
	
	

}

//used for stack, something like this for priority queue?
class Pair
{
	Board b;
	int i;
	
	public Pair(Board B, int I)
	{
		b = B;
		i = I;
	}
	String moves;
	public Pair(Board B, int I, String moves)
	{
		b = B;
		i = I;
		this.moves = moves;
	}
}
