SOURCE = 'selected-lm.txt'

a = open(SOURCE,'r')
lines = a.readlines()
a.close()


def prepare(line):
    index = line.rindex(':')    
    line = line[:index]
    index = line.rindex(':')    
    line = line[:index]
    line.replace('.java:', ' - Linha: ')
    line = line.replace('/', '.')
    return line

b = open(SOURCE,'w')
for line in lines:
    b.write(prepare(line))
    b.write('\n')

b.close()
