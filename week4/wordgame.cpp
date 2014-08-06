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

using namespace std;

#define m_p make_pair

#define debug(a) do{cout << a << endl;}while(0)

vector<string> dict;

int add_words(string &cur, int cost, stack<pair<int, string> > & s, unordered_map<string, pair<int,string> > & vis)
{
	int added = 0;
	for(int i = 0; i < cur.length(); i++)
	{
		string temp = cur;
		//replace with each letter
		for(char rep = 'a'; rep <= 'z'; rep++)
		{
			temp[i] = rep;
			//if the new word is in dict, add it
			if (binary_search(dict.begin(), dict.end(), temp))
			{
				//add if new word, or old word but lower cost
				if (!vis.count(temp) || cost+1 < vis[temp].first)
				{
					s.push(m_p( cost + 1, temp));
					vis[temp] = m_p(cost+1,cur);
					added++;
				}
			}
		}
	}
	return added;
}


//bfs, exchange chars left to right, a-z until reaching to
//branching factor = 26*strlen ~= 104
int bfs(string &from, string &to)
{	
	queue<pair<int, string> > q;
	unordered_map<string, string > vis;
	
	q.push(m_p(0,from));
	vis[from] = from;
	
	int maxmem = 1;
	while(!q.empty())
	{
		maxmem = max((int)q.size(), maxmem);
		string cur = q.front().second;
		
		if (cur == to) //success
			break;
		
		int cost = q.front().first;
		q.pop();
		
		
		//for each char in word
		//cant use add_words as q != stack :(
		for(int i = 0; i < cur.length(); i++)
		{
			string temp = cur;
			//replace with each letter
			for(char rep = 'a'; rep <= 'z'; rep++)
			{
				temp[i] = rep;
				//if the new word is in dict, add it
				//unless we saw it already
				if (binary_search(dict.begin(), dict.end(), temp)
					&& !vis.count(temp))
				{
					q.push(m_p( cost + 1, temp));
					vis[temp] = cur;
				}
			}
		}
	}	
	if (q.empty())
		return -1;
	cout << "BFS: " << "cost:" << q.front().first << " vis:" << vis.size() << " mem:" << maxmem << endl;
	string temp = to;
	while(temp != from)
	{
		cout << temp << " ";
		temp = vis[temp];
	}
	cout << endl;
	return q.front().first;
}

int dfs(string &from, string &to, int depth)
{
	stack<pair<int, string> > s;
	unordered_map<string,pair<int, string>  > vis;
	
	s.push(m_p(0,from));
	vis[from] = m_p(0,from);
	int maxmem = 1;
	while(!s.empty())
	{
		maxmem = max((int)s.size(), maxmem);
		
		string cur = s.top().second;
		if (cur == to)
			break;
			
		int cost = s.top().first;
		s.pop();
		
		if (cost > depth)
			continue;
		add_words(cur, cost, s, vis);
	}
	if (s.empty())
		return -1;
	cout << "DFS: " << "cost:" << s.top().first << " vis:" << vis.size() << " mem:" << maxmem << endl;
	string temp = to;
	while(temp != from)
	{
		cout << temp << " ";
		temp = vis[temp].second;
	}
	cout << endl;
	return s.top().first;
}

int it_deep_dfs(string &from, string &to)
{
	for(int i = 0; i < 20; i++)
	{
		int c = dfs(from, to, i);
		if (c != -1)
			return c;
	}
	cout << "DFS: failed to find after 20 steps" << endl;
	return -1;
}

int bi_di_bfs(string &from, string &to)
{
	stack<pair<int, string> > s1,s2;
	unordered_map<string, pair<int, string> > vis1,vis2;
	int maxmem1 = 1,maxmem2 =1;
	int depth = 0;
	for(depth = 0; depth < 20; depth++)
	{
		s1=stack<pair<int, string> >();
		s2=stack<pair<int, string> >();
		vis1.clear();vis2.clear();

		s1.push(m_p(0,from));
		s2.push(m_p(0,to));
		vis1[from] = m_p(0, from);
		vis2[to] = m_p(0, to);
		while(!s1.empty())
		{
			maxmem1 = max(maxmem1, (int)s1.size());
			string cur = s1.top().second;

			
			int cost = s1.top().first;
			s1.pop();
			if (cost > depth/2 + depth%2)
				continue;
			add_words(cur, cost, s1, vis1);
		}			
		while(!s2.empty())
		{
			maxmem2 = max(maxmem2, (int)s2.size());
			string cur = s2.top().second;			
			if (vis1.count(cur))
				break; //success
			
			int cost = s2.top().first;
			s2.pop();
		
			if (cost > depth/2)
				continue;
			add_words(cur,cost,s2,vis2);
		}
		if (!s2.empty() && vis1.count(s2.top().second))
			break;
	}	
	if (s2.empty())
		return -1;
	if (vis1.count(s2.top().second))
	{
		cout << "BI_DFS: " << "cost:" << vis2[s2.top().second].first + vis1[s2.top().second].first 
			<< " vis:" << vis1.size() + vis2.size() << " mem:" << maxmem1 + maxmem2 << endl;
		string temp = s2.top().second;
		while(temp != from)
		{
			cout << temp << " ";
			temp = vis1[temp].second;
		}
		cout << endl;
		temp = s2.top().second;
		while(temp != to)
		{
			cout << temp << " ";
			temp = vis2[temp].second;
		}
		cout << endl;
		return 1000;
	}
	return -1;
}

int main()
{	
	while(!cin.eof())
	{
		string t;
		cin >> t;
		if (t.size() == 4)
			dict.push_back(t);
	}
	sort(dict.begin(), dict.end());
	cout << "dict size: " << dict.size() << endl;
	
	string from, to;
	srand(time(NULL));
	from = dict[rand()%dict.size()];
	to = dict[rand()%dict.size()];
	
	cout << "from: " << from
		<< " to: " << to << endl;

	bfs(from, to);
	it_deep_dfs(from, to);
	bi_di_bfs(from, to);
}
