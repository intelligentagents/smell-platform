import random

source = 'xerces-fe.csv'
dest = 'filter-fe.csv'

a = open(source, 'r')
data2 = a.readlines()

data = data2

b = open(dest, 'w')

for dev in range(1):
    b.write('DEV %s ---------------------------------\n' % str(dev+1))
    samp = random.sample(data, 17)
    for i in samp:
        b.write(i)

a.close()
b.close()

    
