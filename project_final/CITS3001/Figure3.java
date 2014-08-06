package CITS3001;

import java.util.*;


class Figure3
{

	static void testHigh()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		Searcher s;
		for (int i = 9; i <13; i++){
			for(int j= 5; j < i; j++){
				s = new Jogger(new LinearWeightedSum(), i, j);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
				}
		}
	}
	
	static void testLow()
	{
		Scanner in = new Scanner(System.in);
		Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		Searcher s;
		for (int i = 6; i <9; i++){
			for(int j= 1; j < i; j++){
				s = new Jogger(new LinearWeightedSum(), i, j);
				s.playGame(b, new ArrayList<Integer>(nextPiece));
				}
		}
	}

	public static void main(String[] args){
		testLow();
	}
	

}
