from nucleo.models import Project, Statement, Smell, Human, HumanAnalysis

SOURCE = 'dados/lm.txt'

src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

project = Project.objects.get(id=2)

for line in lines:
    index = line.rindex(":")
    count = line[index+1:]
    stat = line[:index]
    index = stat.rindex(":")
    stat_name = stat[:index]
    result = Statement.objects.filter(project=project, ref="LM", name=stat_name)
    if result:
        statement = result[0]
        print statement, count
        statement.obs = count.strip()
        statement.save()
