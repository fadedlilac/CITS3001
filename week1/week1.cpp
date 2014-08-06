#include <iostream>
#include <cstdint>
#include <cstdlib>
#include <sys/time.h>

using namespace std;

#define printit() do{\
cout << T << endl;\
for(int i = 0; i < t-p; i++) cout << " ";\
cout << P << endl;\
for(int i = 0; i < t; i++) cout << " ";\
cout << "x" << endl;\
}while(0);



void naive(string &T, string &P)
{
	
	for(int i = 0; i < T.length() - P.length() + 1; i++)
	{
		bool match = true;
		for(int j = 0; j < P.length(); j++)
		{
			
			if (T[i+j] != P[j])
			{
				match = false;
				break;
			}
		}
		if (match)
			;//cout << "matched: " << i << endl;
	}
	
}

void kmp(string &T, string &P)
{
	
	int tab[P.length()];
	tab[0] = 0;
	int i = 1;
	int cur = 0;
	//create table
	while(i < P.length())
	{
		
		//a longer match
		if (P[i] == P[cur])
		{
			tab[i] = ++cur;			
			i++;
		}
		//if we had a match, next check the previous one
		else if (cur > 0)
			cur = tab[cur];
		//no match
		else
		{
			tab[i] = 0;
			i++;
		}	
	}
	
	//for( int j = 0;  j < P.length(); j++)	cout << tab[j] << endl;
			
	
	//i index into P, s index into T
	int p=0;
	for (int t = 0; t < T.length(); t++)
	{
		
		//while no match, use the table
		while( p > 0 && P[p] != T[t] )
		{			
			
			p = tab[p-1];			
		}
		
		//if match, inc both (t in for loop)
		if (P[p] == T[t])
			p++;
		if (p == P.length())
		{
			;//cout << "matched: " << t - p + 1 << endl;
			p = tab[p-1];
		}
	}
	
}

void bm (string &T, string &P)
{
	int bad_char[1<<8];
	for(int i = 0; i < 1<<8; ++i)
	{
		//if no chars, skip whole word
		
		bad_char[i] = -1;
	}
	for (int i=0; i < P.length(); ++i)
	{
		
		bad_char[P[i]] = i;
	}
	int good_suff[P.length() + 1];
	for (int i=0; i < P.length() + 1; ++i)
	{
		
		good_suff[i] = 0;
	}
	//i place in table (suffix pos)
	//j next rightmost match
	//last used once there are no more matches (j=-1)
	int j = P.length() - 2;
	int last = P.length();
	for (int i = P.length()-1; i >=0; --i)
	{
		
		while(j >= 0 && P[j] != P[i])
			--j;
		if (j < 0)
			good_suff[i] = last;
		else if (P[j] == P[i])
		{
			good_suff[i] = i-j;
			last = i-j;
			--j;
		}	
	}
//	for( int j = 0;  j < P.length(); j++)	cout << bad_char[P[j]] << ","; cout << endl;
//	for( int j = 0;  j < P.length(); j++)	cout << good_suff[j] << ","; cout << endl;
	//t is index in T
	//p is index in P
	int p = P.length() - 1;
	int t = 0;
	while( t + p < T.length())
	{
		
		//scroll through until fail or match
		while( p >= 0 && P[p] == T[t+p])
		{
			
			--p;
		}	
//		printit();
//		cout << good_suff[p+1] << " " << p - bad_char[T[t+p]] << endl;
		if (p == -1)
		{
			;//cout << "matched: " << t << endl;
			t += good_suff[0]; //correct? best?
			p = P.length() - 1;
		}
		else
		{
			t += max(1, max ( good_suff[p+1], p - bad_char[T[t+p]]));
			p = P.length() - 1;
		}		
	}
	
}

bool checkmatch(string &T, string &P, int t)
{
	for(int p = 0; p < P.length(); p++)		
		if (T[t+p] != P[p]) return false;
	return true;
}

void rb(string &T, string &P)
{
	
	const uint32_t PRIME = 101;
	uint32_t Pd = 0;
	uint32_t Td = 0;
	uint32_t T_remover = 1;
	if (T.length() < P.length()) return;
	for (int i = 0; i < P.length(); ++i)
	{
		
		Pd = Pd * PRIME + P[i];
		Td = Td * PRIME + T[i];
		if (i) T_remover *= PRIME;
	}
	if (Pd == Td && checkmatch(T, P, 0)) ; //cout << "matched: " << 0 << endl; 
	for (int t = 1; t < T.length() - P.length() + 1; ++t)
	{
		
		Td -= T_remover * T[t-1];
		Td *= PRIME;
		Td += T[t + P.length() - 1];
//		cout << Pd << " " << Td << endl;
		if (Pd == Td && checkmatch(T, P, t)) ;//cout << "matched: " << t << endl; 
		else if (Pd == Td) cout << "collision" << endl;
	}
	
}

#define ALPH 3
#define PLEN 10
#define TLEN 10000000

int main()
{
	string T, P;
	/*while (!(cin>>ws).eof())
	{
		getline(cin,T);
		getline(cin,P);
		naive(T,P);
		rb(T,P);
		kmp(T,P);
		bm(T,P);
	}
	
	*/
	
	/*T.clear(); T.append("ABC ABCDAB ABCDABCDABDE");
	P.clear(); P.append("abbabaa");
	kmp(T,P);
	return 0;*/
	
	/* random chars test
	for(int i = 0; i < TLEN; i++) T.append(1,(char)(rand()%ALPH + 'a'));
	for(int i = 0; i < PLEN; i++) P.append(1,(char)(rand()%ALPH + 'a'));
	
	struct timeval then,now;
	
	gettimeofday(&then, NULL);
	naive(T,P);
	gettimeofday(&now, NULL);
	cout << endl;
	fprintf(stderr, "%.2f ms\n",
          1000 * (now.tv_sec - then.tv_sec)
            + 1e-3 * (now.tv_usec - then.tv_usec));

	
	gettimeofday(&then, NULL);
	kmp(T,P);
	gettimeofday(&now, NULL);
	cout << endl;
	fprintf(stderr, "%.2f ms\n",
          1000 * (now.tv_sec - then.tv_sec)
            + 1e-3 * (now.tv_usec - then.tv_usec));
            
    gettimeofday(&then, NULL);
	bm(T,P);
	gettimeofday(&now, NULL);
	cout << endl;
	fprintf(stderr, "%.2f ms\n",
          1000 * (now.tv_sec - then.tv_sec)
            + 1e-3 * (now.tv_usec - then.tv_usec));
    
    gettimeofday(&then, NULL);
	rb(T,P);
	gettimeofday(&now, NULL);
	cout << endl;
	fprintf(stderr, "%.2f ms\n",
          1000 * (now.tv_sec - then.tv_sec)
            + 1e-3 * (now.tv_usec - then.tv_usec));
 */
            
    
}
