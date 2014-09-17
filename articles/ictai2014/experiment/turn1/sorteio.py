import random

gc = 50
fe = 61
lm = 127

def sorteio(max, amostra):
    lista = []
    items = range(1,max+1)
    while len(lista) < amostra:
        index = random.randint(0, len(items))
        lista.append(items.pop(index-1))
    lista.sort()
    return lista

def imprimir(index, listas):
    print "Subject %d:\n\tLong Parameter List: [1] \n\tGod Class: %s\n\tFeature Envy: %s\n\tLong Method:%s" % (index, listas[0], listas[1], listas[2])


for i in range(1,11):
    listas = []
    listas.append(sorteio(gc, 20))
    listas.append(sorteio(fe, 20))
    listas.append(sorteio(lm, 20))
    imprimir(i, listas)
    

