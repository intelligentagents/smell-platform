import random

SOURCE = 'lm.txt'
SAMPLE = 20
DEST = 'selected-lm.txt'

src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

random.shuffle(lines)
lines = lines[:SAMPLE]

target = open(DEST, 'w')
for line in lines:
    target.write(line)
target.close()
