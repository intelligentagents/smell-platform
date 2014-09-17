#coding:utf-8

from nucleo.models import Statement, Project, Smell


def statements(project, lines):
    for line in lines:
        if not line.strip():
            continue
        print line
        ref,stat = line.split("%%%")
        Statement.objects.create(project=project, name=stat.strip(), ref=ref.strip())
            
project = Project.objects.all()[0]            
a = open('dados/lm.txt', 'r')
statements(project, a.readlines())
a.close()