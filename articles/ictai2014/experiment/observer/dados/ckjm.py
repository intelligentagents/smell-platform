from nucleo.models import Project, Statement, Smell, Human, HumanAnalysis, MeasureCalculator, Measure, Metric

SOURCE = '/home/hozano/apps/ckjm-2.1/ganttproject-2.10.2.txt'


src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

metrics = ['WMC', 'DIT', 'NOC', 'CBO', 'RFC', 'LCOM', 'Ca', 'Ce', 'NPM', 'LCOM3', 'LOC', 'DAM', 'MOA', 'MFA', 'CAM', 'IC', 'CBM', 'AMC']

project = Project.objects.get(id=1)
mc = MeasureCalculator.objects.get(id=1)
cont = 0
for line in lines:
    if line and line.startswith(" "): continue 
    measures = line.split(" ")
    name = measures.pop(0)
    stats = Statement.objects.filter(project=project, name=name, ref__startswith="GC")
    
    if stats:
        cont += 1
        stat = stats[0] 
        if len(stats) > 1:
            print stat.name, stat.ref, stat.id
            raise Exception("deu merda")
        for i in range(len(measures)):
            print stat.ref
            metric = Metric.objects.get(name=metrics[i])
            value = float(measures[i].replace(',','.'))
            Measure.objects.create(calculator=mc, metric=metric, value=value, statement=stat) 


print cont