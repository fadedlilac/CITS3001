#include <iostream>
#include <cstdlib>
#include <cstdio>
#include <vector>
using namespace std;


int dumb(string &a, int ap, string &b, int bp)
{
	if (ap < 0 || bp < 0)
		return 0;
	if (a[ap] == b[bp])
		return dumb(a, ap-1, b, bp-1) + 1;
	else
		return max( dumb(a, ap, b, bp-1),
					dumb(a, ap-1, b, bp));
}

int smart(string &a, int ap, string &b, int bp, vector<vector < int > > &mems)
{
	if (ap < 0 || bp < 0)
		return 0;
	if (mems[ap][bp] != -1)
		;
	else if (a[ap] == b[bp])
		mems[ap][bp] =  smart(a, ap-1, b, bp-1, mems) + 1;
	else
		mems[ap][bp] =  max( smart(a, ap, b, bp-1, mems),
					smart(a, ap-1, b, bp, mems));
	return mems[ap][bp];
}



int main()
{
	string a,b;
	cin >> a >> b;
	vector<vector < int > > mems = vector<vector < int > >(a.length(),vector<int>(b.length(),-1));
	cout << smart(a, a.length()-1, b, b.length()-1, mems) << endl;		
	cout << dumb(a, a.length()-1, b, b.length()-1) << endl;		
}
