package CITS3001;

import java.util.*;


class Figure2
{
	
	static void DVP()
	{
	
	
		Scanner in = new Scanner(System.in);
		final Board b = Board.readBoard(in);
		
		ArrayList<Integer> nextPiece = new ArrayList<Integer>();
		while(in.hasNextInt())
			nextPiece.add(in.nextInt());
		Searcher s;
		for (int i = 2; i < 7; i++){
			s = new DLDFS(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i);
			s.playGame(b, new ArrayList<Integer>(nextPiece));	
		}
		for (int i = 2; i < 7; i++){
			s = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i, 1);
			s.playGame(b, new ArrayList<Integer>(nextPiece));	
			}
		for (int i = 2; i < 7; i++){
			s = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i, 1.0/2);
			s.playGame(b, new ArrayList<Integer>(nextPiece));
		}
		for (int i = 2; i < 7; i++){	
			s = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i, 1.0/4);
			s.playGame(b, new ArrayList<Integer>(nextPiece));	
			}
			for (int i = 2; i < 7; i++){
			s = new Pruner(new LinearWeightedSum(new double[] { 0.3, 0.5, 0.2, 0.0}), i, 1.0/16);
			s.playGame(b, new ArrayList<Integer>(nextPiece));	
		}
	
	}

	public static void main(String[] args){
		DVP();
	}
	
}
