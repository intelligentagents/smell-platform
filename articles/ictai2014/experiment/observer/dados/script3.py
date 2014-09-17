#coding:utf-8

from nucleo.models import Statement, Project, Smell, Human, HumanAnalysis

def human_analysis(project, human_id, _questions, _answers):
    human = Human.objects.get(id=human_id)
    smell_names = ["LPL", "GC", "FE", "LM"]
    lpl = Smell.objects.get(short_name="LPL")
    gc = Smell.objects.get(short_name="GC")
    fe = Smell.objects.get(short_name="FE")
    lm = Smell.objects.get(short_name="LM")
    smells = [lpl, gc, fe, lm ]
    
    for i in range(4):
        questions = _questions[i]
        answers = _answers[i]
        for j in range(len(questions)):
            if answers[j] in "01":
                ref="%s#%s" % (smell_names[i], questions[j])
                stat = Statement.objects.get(ref=ref)
                print stat, int(answers[j])
                HumanAnalysis.objects.create(human=human, statement=stat, verified=int(answers[j]), smell=smells[i])
            
project = Project.objects.all()[0]   
q = [[1],
    [3, 5, 7, 13, 15, 18, 19, 21, 22, 24, 25, 27, 29, 30, 41, 42, 46, 47, 48, 50],
    [6, 8, 10, 15, 16, 17, 18, 21, 26, 27, 29, 34, 37, 43, 44, 52, 53, 56, 57, 58],
    [2, 10, 21, 22, 23, 42, 50, 52, 55, 56, 69, 75, 84, 87, 96, 102, 107, 109, 111, 126]]
r = ["1", "011010111101101?1110", "11110011111111111111", "11000100100110000010"]
human_analysis(project, 6,q,r)
