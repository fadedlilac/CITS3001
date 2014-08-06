#include <iostream>
#include <forward_list>
#include <vector>
#include <signal.h>
#include <sstream>

using namespace std;
#define debug(a) do{cout << a << endl;}while(0)
#define for0(a, b) for( (typeof (b)) a; a < b; ++a)

struct mancalaBoard
{
	int board[2][7];
	bool myTurn;
	vector<int> moveList;
	mancalaBoard(int n)
	{
		for(int i = 0; i < 6; ++i)
		{
			board[0][i] = board[1][i] = n;
		}
		board[0][6] = board[1][6] = 0;
		myTurn = true;
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

	forward_list<mancalaBoard> getMoves()
	{
		vector<int> temp = moveList;
		moveList.clear(); //to allow easy copy
		forward_list<mancalaBoard> ret = __getMoves();
		moveList = temp;
		return ret;
	}

	forward_list<mancalaBoard> __getMoves()
	{
		forward_list<mancalaBoard> ret;
		for(int i = 0; i < 6; ++i)
		{
			if (board[myTurn][i])
			{
				mancalaBoard thisMove = *this; //copy current board
				thisMove.moveList.push_back(i); //append move to get there
				if (thisMove.playMove(i))
				{
					ret.splice_after(ret.before_begin(), thisMove.__getMoves());
				}
				else
					ret.push_front(thisMove);
			}
		}
		return ret;
	}


	

	
};

ostream& operator<<(ostream& os, mancalaBoard g)
{
	for (int i = 6; i >= 0; i--)
		os << g.board[0][i] << " ";
	cout << endl << " ";
	for (int i = 0; i < 7; i++)
		os << " " << g.board[1][i];
	cout << endl << g.myTurn << endl;
	for (size_t i = 0; i < g.moveList.size(); i++)
		os << g.moveList[i] << " ";
	os << endl << endl;
	return os;
}

volatile bool BREAK_NOW = false;
int calcMiniMax(mancalaBoard &curGame, int depth, int depthLimit, int alpha, int beta)
{
	if (BREAK_NOW)
		return 0;
	bool isMax = curGame.myTurn;
	if (curGame.isFinished() || depth > depthLimit)
	{
		return curGame.getValue();
	}
	forward_list<mancalaBoard> moves = curGame.getMoves();
	int best = isMax? -10000 : 10000;
	while(!moves.empty())
	{
		mancalaBoard top = moves.front();
		moves.pop_front();
		int score = calcMiniMax(top, depth+1,depthLimit, alpha, beta);
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


vector<int> minimax(mancalaBoard &curGame, int depthLimit )
{
	
	if (curGame.isFinished()) cerr << "game is finished? minimax" << endl;
	forward_list<mancalaBoard> moves = curGame.getMoves();
	int best = curGame.myTurn ? -10000 : 10000;
	mancalaBoard bestBoard(0);
	while(!moves.empty())
	{
		mancalaBoard cur = moves.front();
		moves.pop_front();
		int score = calcMiniMax( cur, 1, depthLimit, -10000, 10000);
		if (curGame.myTurn)
		{
			if (score > best)
			{
				best = score;
				bestBoard = cur;
			}
		}
		else
		{
			if (score < best)
			{
				best = score;
				bestBoard = cur;
			}
		}
		
	}
	return bestBoard.moveList;
}

vector<int> it_deep(mancalaBoard & curGame)
{
	BREAK_NOW = false;
	vector<int> nextMove;
	for (int d = 0; d < 50; d++)
	{
		nextMove = minimax(curGame, d);
		if (BREAK_NOW)
			break;
		for(size_t i = 0; i < nextMove.size(); ++i)
		{
			cout << nextMove[i] << " ";
		}
		cout <<endl;
	}
	return nextMove;
}
		

int main()
{
	mancalaBoard start(3);
#ifdef TEST
	while(!start.isFinished())
	{
		cout << start;
		vector<int> nextMove = minimax(start, 10);
		for(size_t i = 0; i < nextMove.size(); ++i)
		{
			start.playMove(nextMove[i]);
			cout << nextMove[i] << " ";
		}
		cout << endl;
	}
	cout << "done:" << endl;
	cout << start;
#endif
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
				vector<int> nextMove;
				if (firstMove)
				{
					nextMove.push_back(3); 
					nextMove.push_back(0); 
				}
				else
					nextMove = minimax(start, 10);
				for(size_t i = 0; i < nextMove.size(); ++i)
				{
					start.playMove(nextMove[i]);
					cout << "MOV " << nextMove[i] << endl;
					cerr << "MOV " << nextMove[i] << endl;
					nbumps--;
				}
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

}
