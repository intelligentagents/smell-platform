import re

SOURCE = 'gc-gantt.txt'
src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

DEST = 'pmd-gantt.csv'
dest = open(DEST, 'w')

regex = re.compile("WMC=(?P<WMC>\d+),\s*ATFD=(?P<ATFD>\d+),\s*TCC=(?P<TCC>\d+.\d+)")
WMC_VERY_HIGH = 47.0
FEW_THRESHOLD = 5.0
ONE_THIRD_THRESHOLD = 1.0/3.0

c = 0
for line in lines:
    print line
    r = regex.search(line)
    dic = r.groupdict()
    parts = line.split('.java')
    name = parts[0]
    wmc = dic['WMC']
    atfd = dic['ATFD']
    tcc = dic['TCC']
    gc = "0"
    if (float(wmc) >= WMC_VERY_HIGH) and (float(atfd) > FEW_THRESHOLD) and (float(tcc) < ONE_THIRD_THRESHOLD):
        c += 1
        gc = "1"
    dest.write(";".join([name, wmc, atfd, tcc, gc]))
    dest.write('\n')
        
dest.close()
print c
