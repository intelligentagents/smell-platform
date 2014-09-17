#coding:utf-8

from nucleo.models import Statement, Project, Smell, ToolAnalysis, Tool

def analysis(project, lines):
    check = Tool.objects.get(name="Checkstyle")
    pmd = Tool.objects.get(name="PMD")
    infusion = Tool.objects.get(name="inFusion")
    jd = Tool.objects.get(name="JDeodorant")
    tools = [check, pmd, infusion, jd]
    lpl = Smell.objects.get(short_name="LPL")
    gc = Smell.objects.get(short_name="GC")
    fe = Smell.objects.get(short_name="FE")
    lm = Smell.objects.get(short_name="LM")
    smells = {'LPL':lpl, 'GC':gc, 'FE':fe, 'LM':lm }
    
    for line in lines:
        if not line.strip():
            continue
        print line
        parts = line.split("\t")
        stat = Statement.objects.get(ref=parts[0].strip())
        if not stat:
            raise
        smellname = parts[0].strip().split("#")[0]
        for i in range(1,5):
            if parts[i].strip() != "x": 
                ToolAnalysis.objects.create(tool=tools[i-1], statement=stat, verified=int(parts[i]), smell=smells[smellname])
            
project = Project.objects.all()[0]            
a = open('dados/analysis.txt', 'r')
analysis(project, a.readlines())
a.close()