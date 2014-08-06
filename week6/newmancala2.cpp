#include <iostream>
#include <forward_list>
#include <vector>
#include <signal.h>
#include <sstream>

using namespace std;
#define debug(a) do{cout << a << endl;}while(0)
#define for0(a, b) for( (typeof (b)) a; a < b; ++a)
#define RESERVE 20
#ifndef DEPTH
#define DEPTH 10
#warn depth not deffed
#endif

struct mancalaBoard
{
	int board[2][7];
	bool myTurn;
	vector<int> nextMoveCount;

	mancalaBoard(int n)
	{
		for(int i = 0; i < 6; ++i)
		{
			board[0][i] = board[1][i] = n;
		}
		board[0][6] = board[1][6] = 0;
		myTurn = true;
		nextMoveCount.push_back(0);
	}

	bool isFinished()
	{
		int sum = 0;
		for(int i = 0; i < 6; ++i)
		{
			sum += board[0][i];
		}
		if (sum == 0)
			return true;
		sum = 0;
		for(int i = 0; i < 6; ++i)
		{
			sum += board[1][i];
		}
		if (sum == 0)
			return true;
		return false;
	}

	bool playMove(int i)
	{
		nextMoveCount.clear();
		nextMoveCount.push_back(0);
		int seeds = board[myTurn][i];
		board[myTurn][i] = 0;
		int lastSow = i;
		int curSide = myTurn;
		while(seeds)
		{
			lastSow++;
			if (lastSow > 5)
			{
				if (curSide != myTurn)//dont sow in opponents house
				{
					lastSow = 0;
					curSide = !curSide;
				}
				else if (lastSow > 6)//skip our house after sowing in it
				{
					lastSow = 0;
					curSide = !curSide;
				}
			}
			board[curSide][lastSow]++;
			seeds--;
		}
		if (lastSow == 6)
			return true; //more sows coming
		if (curSide == myTurn 
			&& board[curSide][lastSow] == 1
			&& board[!curSide][5-lastSow] > 0) //steal
		{
			board[curSide][6] += board[!curSide][5-lastSow] + 1;
			board[!curSide][5-lastSow] = 0;
			board[curSide][lastSow] = 0;
		}
		myTurn = !myTurn; // change sides
		return false;
	}

	int numInHouse()
	{
		return board[1][6] - board[0][6];
	}

	int numOnSide()
	{
		int sumMax = 0, sumMin=0;
		for(int i = 0; i < 6; ++i)
		{
			sumMin += board[0][i];
			sumMax += board[1][i];
		}
		return sumMax - sumMin;
	}


	int getValue()
	{
		if (isFinished())
		{	
			int sumMax = board[1][6], sumMin=board[0][6];
			for(int i = 0; i < 6; ++i)
			{
				sumMin += board[0][i];
				sumMax += board[1][i];
			}
			return sumMax > sumMin ? 9999 : -9999;
		}
		else
		{
			double score = 0;
			score += 1.0 * numInHouse();
			score += 0.85 * numOnSide();
			return (int)score;
		}
	}

	bool nextMove(mancalaBoard &ret)
	{
	}	
};

ostream& operator<<(ostream& os, mancalaBoard g)
{
	cout << "player: " << g.myTurn << endl;
	for (int i = 6; i >= 0; i--)
		os << g.board[0][i] << " ";
	cout << endl << " ";
	for (int i = 0; i < 7; i++)
		os << " " << g.board[1][i];
	return os;
}

int calcMiniMax(mancalaBoard &curBoard, int depth, int alpha, int beta)
{
	bool isMax = curBoard.myTurn;
	if (depth == 0 || curBoard.isFinished() )
	{
		return curBoard.getValue();
	}

	mancalaBoard nextBoard = curBoard;

	int best = isMax? -10000 : 10000;
	while(curBoard.nextMove(nextBoard))
	{
		int score = calcMiniMax(nextBoard, depth-1, alpha, beta);
		if (isMax)
		{
			best = max(best, score);
			if (best >= beta)
				return best;
			alpha = max(alpha, best);
		}
		else
		{
			best = min(best, score);
			if (best <= alpha)
				return best;
			beta = min(beta, best);
		}
	}
	return best;
}


int minimax(mancalaBoard &curBoard)
{
	
	if (curBoard.isFinished()) cerr << "game is finished? minimax" << endl;
	
	mancalaBoard nextBoard = curBoard;
	int best = curBoard.myTurn ? -10000 : 10000;
	int besti = 0;
	int curi = 0;
	while(curBoard.nextMove(nextBoard))
	{
		int score = calcMiniMax( nextBoard, DEPTH, -10000, 10000);
		if (curBoard.myTurn)
		{
			if (score > best)
			{
				best = score;
				besti = curi;
			}
		}
		else
		{
			if (score < best)
			{
				best = score;
				besti = curi;
			}
		}
		curi++;
	}
	return besti;
}

int main()
{
	mancalaBoard start(3);
#ifdef TEST
	while(!start.isFinished())
	{
		cout << start << endl;
		int nextMove = minimax(start);
		start.playMove(nextMove);
		cout << "move chosen: " << nextMove << endl << endl;
	}
	cout << "done:" << endl;
	cout << start;
#endif
#ifndef TEST
	std::cerr << "STARTING FOR SERVER\n";
	int nbumps = 0;
	bool firstMove = true;
	while (1) 
	{
		std::string line;
		std::getline(std::cin, line);
		std::stringstream str(line);
		std::string cmd;
		if (!(str >> cmd)) {
			break;
		}
		std::cerr << "COMMAND: " << cmd << "\n";
		if (cmd == "WIN" || cmd == "LSE" || cmd == "DRW") {
			break;
		}
		if (cmd == "BMP") {
			std::cerr << "GOT BUMP\n";
			nbumps++;
			if (nbumps == 1) {
				int nextMove;
				nextMove = minimax(start);
				start.playMove(nextMove);
				cout << "MOV " << nextMove << endl;
				cerr << "MOV " << nextMove << endl;
				nbumps--;
			}
		}
		if (cmd == "MOV") {
			firstMove=false;
			std::cerr << "APPLYING MOVE\n";
			int mov;
			str >> mov;
			start.playMove(mov-7);
		}
	}
#endif
}
