"""
This program creates input files for the 2014 CITS3001 project.
It creates a file filename with roughly noOfPieces tiles whose
relative proportions are specified by the list proportions.
This version sets up a standard initial board. 

v1.1, Lyndon While, 24/4/14 
"""
#shamelessly hacked by Evan to clamp the maximum run of 1s or 2s, to ensure longer games.


import random


#this is the output file name
#filename = "test.in"
#this is the total number of pieces
noOfPieces = 16000
#these are the proportions of 1, 2, 3, 6, 12, etc.
#the proportions are summed and rounded, so the number of pieces returned won't always be exact
#the easiest way to make it exact is for proportions to contain integers that sum to noOfPieces
proportions = [12, 12, 3, 1, 1, 1]
sumprop = sum(proportions)
#max excess of pieces allowed (2s v 1s)
maxrun = 4
#1s count up, 2s down
currun = 0

def normaliseProportions():
#normaliseProportions() replaces the values on proportions with the corresponding numbers of pieces
    z = sum(proportions)
    for k in range(len(proportions)):
        proportions[k] = round(proportions[k] * noOfPieces / z)


def getpiece():
    rand = random.randint(0, sumprop-1)
    a = 0
    for i in range(len(proportions)):
    	a += proportions[i];
    	if (a > rand):
    		return piece(i)
	
#ensure we swap 1/2 choice if we have a bad run
def getpiece2():  
	global currun 
	num = getpiece()
	if (num < 3):
		if (currun < -1 * maxrun):
			num = 1
		if (currun > maxrun):
			num = 2
		
		currun += 1 if num == 1 else -1
	return num

def piece(k):
#piece(k) returns the kth smallest tile 
    if k <= 1:
        return k + 1
    else:
        return 3 * 2 ** (k - 2)


if any([x < 0 for x in proportions]):
    print("Proportions must contain only non-negative numbers")
elif sum(proportions) <= 0:
    print("Proportions must contain at least one positive number")
else:
	pieces = [getpiece2() for k in range(noOfPieces)]
    #pieces = [x for k in range(len(proportions)) for x in [piece(k)] * int(proportions[k])]
	maxPiecesPerLine = 20
	ps = [pieces[k:k + maxPiecesPerLine] for k in range(0, len(pieces), maxPiecesPerLine)]
	#f = open(filename, 'w')
	print("\n".join([str(len(pieces)) + " pieces; " + str(proportions)[1:-1],
		"maxrun " + str(maxrun), "0 0 0 0", "0 1 2 0", "0 2 1 0", "0 0 0 0", ""] + [" ".join([str(x) for x in l]) for l in ps]))
	

