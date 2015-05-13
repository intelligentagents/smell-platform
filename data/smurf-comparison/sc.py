from glob import glob

lista = glob('D:/Wally/UFAL/Refactoring/Smurf/Test/Azureus/*.csv')

for l in lista:
	f = open(l, 'r')
	mess = ''
	for line in f:
		line = line.split(';')
		if line[0] != 'None':
			mess += '1;' + line[0] + ';' + str(int(float(line[-1].replace('\n', '')))) + '\n'
	f.close()
	name = l.split('_')
	term = ''
	if name[-1] == 'Test.csv':
		term = '0'
	elif name[-1] == 'Training01.csv':
		term = '1'
	elif name[-1] == 'Training02.csv':
		term = '2'
	f = open('C:/Users/Walysson/git/smell-platform/data/smurf-comparison/azureus/gc-an-' + name[-2] + '-' + term + '.csv', 'w')
	f.write(mess)
	f.close()