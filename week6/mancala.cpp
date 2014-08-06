#include <iostream>
#include <forward_list>
#include <vector>

using namespace std;
#define debug(a) do{cout << a << endl;}while(0)

struct game
{
	
	int house[2][6];
	int score[2];
	bool turn;
	vector<int> sequence;

	bool myHouse(int i)
	{
		return house[turn][i] > 0;
	}

	bool move(int i)
	{
		int x = house[turn][i];
		house[turn][i] = 0;
		int cur = i; 
		int side = turn;
		while (x)
		{
			cur--;
			if (cur < 0)//swap sides if neccesary
			{
				if (side != turn)
				{
					cur = 5;
					side = !side;
				}
				if (cur < -1)
				{
					cur = 5;
					side = !side;
				}
			}
			if (cur >= 0)//normal sow
			{
				house[side][cur]++;
				x--;
			}
			else//score sow
			{
				score[side]++;
				x--;
			}
		}
		if (cur < 0 && side == turn)
			return true;//we ended in our bin, more turns.
		if (side == turn && house[side][cur] == 1)//steal
		{
			score[turn] += house[!turn][5-cur];
			house[!turn][5-cur] = 0;
		}
		turn = !turn;
		return false;
	}

	int getValue()
	{
		for (int i = 0; i < 6; i++)
			score[turn] += house[turn][i];
		for (int i = 0; i < 6; i++)
			score[!turn] += house[!turn][i];
		return score[turn] - score[!turn];
	}

	bool finished()
	{
		int sum = 0;
		for(int i = 0; i < 6; i++)
			sum += house[turn][i];
		if (sum == 0)
			return true;
		sum = 0;
		for(int i = 0; i < 6; i++)
			sum += house[!turn][i];
		if (sum == 0)
			return true;
		return false;
	}
	forward_list<game> getMoves()
	{
		vector<int> temp = sequence;
		sequence.clear();
		forward_list<game> ret = __getMoves();
		sequence = temp;
		return ret;
	}

	forward_list<game> __getMoves()
	{
		forward_list<game> ret;
		for (int i = 0; i < 6; i++)
		{
			if (myHouse(i))
			{
				game temp = *this;
				temp.sequence.push_back(i);
				if (temp.move(i))
				{
					forward_list<game> tempGames = temp.__getMoves();
					ret.splice_after( ret.before_begin(), tempGames);
				}
				else
					ret.push_front(temp);
			}
		}	
		return ret;
	}
};

ostream& operator<<(ostream& os, game g)
{
	for (int i = 0; i < 6; i++)
		os << g.house[0][i] << " ";
	cout << endl;
	for (int i = 0; i < 6; i++)
		os << g.house[1][i] << " ";
	cout << endl;
	os << g.score[0] << " " << g.score[1] << " " << g.turn;
	os << endl;
	for (size_t i = 0; i < g.sequence.size(); i++)
		os << g.sequence[i] << " ";
	os << endl << endl;
}


int calcvalue(game& cur, bool isMax);

void minmax(game & cur)
{
	if (cur.finished())
	{
		cout << "game is done" << endl;
		return;
	}
	forward_list<game> moves = cur.getMoves();
	int best = -10000;
	game best_move;
	int count = 0;
	while(!moves.empty())
	{
		debug(++count);
		game top = moves.front();
		moves.pop_front();
		int score = calcvalue(top, true);
		if (score > best)
		{
			best_move = top;
			best = score;
		}
	}
	cout << "best move is \n" << best_move;
}

int calcvalue(game& cur, bool isMax)
{
	if (cur.finished())
	{
		if (isMax) return cur.getValue();
		return -cur.getValue();
	}
	forward_list<game> moves = cur.getMoves();
	int best = isMax? -10000 : 10000;
	while(!moves.empty())
	{
		game top = moves.front();
		moves.pop_front();
		int score = calcvalue(top, !isMax);
		if (isMax)
		{
			best = max(best, score);
		}
		else
			best = min(best, score);
	}
	return best;
}


int main()
{
	game start;
	start.turn = 0;
	for (int i = 0; i < 6; i++)
		cin >> start.house[0][i];
	for (int i = 0; i < 6; i++)
		cin >> start.house[1][i];
	cin >> start.score[0] >> start.score[1];
	
	debug(start.finished());
	
	forward_list<game> t = start.getMoves();
	while(!t.empty())
	{
		cout << t.front();
		t.pop_front();
	}
	cout << "end" << endl;

	minmax(start);
}

