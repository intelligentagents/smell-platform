import random

SOURCE = 'fe.txt'
SAMPLE = 20
DEST = 'selected-lm.txt'

for i in range(3):
    src = open(SOURCE, 'r')
    lines = src.readlines()
    src.close()

    random.shuffle(lines)
    lines = lines[:SAMPLE]
    target = open("selected-fe-%s.txt" % i, 'w')
    for line in lines:
        target.write(line)
    target.close()

