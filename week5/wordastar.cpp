#include <iostream>
#include <utility>
#include <cstdlib>
#include <cstdio>
#include <vector>
#include <queue>
#include <map>
#include <algorithm>
#include <stack>
#include <ctime>
#include <unordered_map>
#include <unordered_set>

using namespace std;

#define m_p make_pair

#define debug(a) do{cout << a << endl;}while(0)

unordered_map<string, int> dict;

struct word
{
	string w;
	int cost; //return of return of f = h + g
	int depth; //is g, just dist from start;

	word(const string &w, int cost, int depth)
	: cost(cost), w(w), depth(depth)
	{}
	word(const string &w, int cost)
		: word(w,cost,0)
	{}

	bool operator<(const word &l) const
	{
		return cost > l.cost;
	}
};

class averager
{
	private :bool first = true;
	int count = 1;
	public :double av= -1;
	double push(double val)
	{
		if (first)
		{
			first = false;
			av = val;
			return val;
		}
		av = av + (val - av)/(++count);
		return av;
	}
};

int h(const string & from, const string & to)
{
	if (from.length() != to.length())
		return 1<<20;
	int ret = 0;
	for (int i = 0; i < from.length(); i++)
		ret += (from[i] != to[i]);
	return ret;
}

int add_words(const word & cur, priority_queue<word> & pq, unordered_map<string, string> & vis, const string & to)
{
	int added = 0;
	for(int i = 0; i < cur.w.length(); i++)
	{
		string temp = cur.w;
		//replace with each letter
		for(char rep = 'a'; rep <= 'z'; rep++)
		{
			temp[i] = rep;
			//if the new word is in dict, add it
			if (dict.count(temp) && !vis.count(temp))
			{
				pq.push(word(temp, cur.depth + 1 +  h(temp,to), cur.depth + 1));
				added++;
			}
		}
	}
	return added;
}

double calc_branch(int D, int N)
{
	double low=0, high = 1E3;
	while(high-low > 1E-3)
	{
		double mid = (high + low)/2;
		double accum = 0;
		double bn = 1;
		for(int i = 0; i <= D; i++)
		{
			accum += bn;
			bn *= mid;
		}
		if (accum < N)
			low = mid;
		else
			high = mid;
	}
	return low;
}


int astar(const string & from, const string & to)
{
	priority_queue<word> pq;
	int maxmem = 0;
	unordered_map<string, string> vis;

	pq.push(word(from, 0, 0));

	while(!pq.empty())
	{
		word cur = pq.top();
		maxmem = max(maxmem, (int)pq.size());
		if (cur.w == to)
			break;
		//debug(cur.w);
		
		pq.pop();
		if (vis.count(cur.w))
			continue;
		add_words(cur, pq, vis, to);
		vis[cur.w] = "fail";
	}

	if (pq.empty())
	{
		cout << "AST: no path" << endl;
		return -1;
	}
	cout << "AST: " << "cost:" << pq.top().depth 
		<< " vis:" << vis.size() 
		<< " mem:" << maxmem 
		<< " bra:" << calc_branch(pq.top().depth, vis.size())
		<< endl;
	return pq.top().cost;
}
int bfs(string &from, string &to)
{	
	queue<word> q;
	unordered_map<string, string > vis;
	
	q.push(word(from, 0));
	vis[from] = from;
	averager av;	
	int maxmem = 1;
	while(!q.empty())
	{
		maxmem = max((int)q.size(), maxmem);
		word cur = q.front();	
		if (cur.w == to) //success
			break;
		
		q.pop();
		
		int count = 0;
		//for each char in word
		//cant use add_words as q != stack :(
		for(int i = 0; i < cur.w.length(); i++)
		{
			string temp = cur.w;
			//replace with each letter
			for(char rep = 'a'; rep <= 'z'; rep++)
			{
				temp[i] = rep;
				//if the new word is in dict, add it
				//unless we saw it already
				if (dict.count(temp)
					&& !vis.count(temp))
				{
					q.push(word(temp, cur.cost + 1));
					vis[temp] = cur.w;
					count++;
				}
			}
		}
		av.push(count);
	}	
	if (q.empty())
		return -1;
	cout << "BFS: " 
		<< "cost:" << q.front().cost 
		<< " vis:" << vis.size() 
		<< " mem:" << maxmem 
		<< " bra:" << av.av
		<< endl;
	string temp = to;
	while(temp != from)
	{
		cout << temp << " ";
		temp = vis[temp];
	}
	cout << endl;
	return q.front().cost;
}

int main()
{	
	while(!cin.eof())
	{
		string t;
		cin >> t;
		if (t.size() == 4)
			dict[t]++;
	}
	cout << "dict size: " << dict.size() << endl;
	
	string from, to;
	srand(time(NULL));
	int a = rand()%dict.size(), b=rand()%dict.size();
	auto it = dict.begin();
	for(auto it = dict.begin(); it != dict.end(); ++it)
	{
		if (!a)
			from = it->first;
		if (!b)
			to = it->first;
		a--; b--;
	}
	cout << "from: " << from
		<< " to: " << to << endl;
	astar(from, to);
	bfs(from, to);
}
