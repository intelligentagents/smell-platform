import re

from nucleo.models import Project, Statement, Smell, Human, HumanAnalysis, Metric, Measure, MeasureCalculator

SOURCE = 'dados/gc-xerces.txt'
src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

regex = re.compile("WMC=(?P<WMC>\d+),\s*ATFD=(?P<ATFD>\d+),\s*TCC=(?P<TCC>\d+.\d+)")


project = Project.objects.get(id=2)

wmc = Metric.objects.get(short_name="WMC2")
tcc = Metric.objects.get(short_name="TCC")
atfd = Metric.objects.get(short_name="ATFD")
mc = MeasureCalculator.objects.get(id=2)

c = 0
for line in lines:
    print line
    r = regex.search(line)
    dic = r.groupdict()
    parts = line.split('.java')
    name = parts[0]
    stat = Statement.objects.filter(project=project, name=name.strip(), ref__startswith="GC")
    if stat:
        stat = stat[0]
        Measure.objects.create(calculator=mc, metric=wmc, statement=stat, value=float(dic["WMC"])) 
        Measure.objects.create(calculator=mc, metric=atfd, statement=stat, value=float(dic["ATFD"]))
        Measure.objects.create(calculator=mc, metric=tcc, statement=stat, value=float(dic["TCC"]))
        
print c
