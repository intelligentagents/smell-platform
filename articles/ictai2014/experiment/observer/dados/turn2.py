from nucleo.models import Project, Statement, Smell, Human, HumanAnalysis

SOURCE = 'dados/results.csv'

src = open(SOURCE, 'r')
lines = src.readlines()
src.close()

project = Project.objects.get(id=2)

for line in lines:
    print line
    subject, smell, ref, stat_name, v1, v0 = line.split(";")
    human = Human.objects.get(id=int(subject))
    smell = Smell.objects.get(short_name=smell.strip())
    stat, created = Statement.objects.get_or_create(project=project, ref=smell.short_name, name=stat_name.strip())
    verified = False
    if v1 and v1 in "xX":
        verified = True
    HumanAnalysis.objects.create(human=human, smell=smell, statement=stat, verified=verified)
