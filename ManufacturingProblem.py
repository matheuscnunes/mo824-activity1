import gurobipy as gp
from gurobipy import GRB
import random
from random import randint
values = [100, 200, 300, 400, 500, 600, 700, 800, 900, 1000]

for value in values:
	try:
		J = value
		F = randint(5, 10)
		L = randint(5, 10)
		M = randint(5, 10)
		P = randint(5, 10)

		print("Clients:", J)
		print("Factories:", F)
		print("Machines:", L)
		print("Raw materials:", M)
		print("Products:", P)

		#D(j,p)
		demandJP = [[randint(5, 10) for _ in range(P)] for _ in range(J)]

		#r(m,p,l)
		rawMaterialMPL = [[[randint(1, 5) for _ in range(L)] for _ in range(P)] for _ in range(M)]

		#R(m,f)
		rawMaterialAvailableMF = [[randint(800, 1000) for _ in range(F)] for _ in range(M)]

		#C(l,f)
		capacityLF = [[randint(80, 100) for _ in range(F)] for _ in range(L)]

		#P(p,l,f)
		manufacturingCostPLF = [[[randint(10, 100) for _ in range(F)] for _ in range(L)] for _ in range(P)]

		#t(p,f,j)
		transportCostPFJ = [[[randint(10, 20) for _ in range(J)] for _ in range(F)] for _ in range(P)]

		# Create a new model
		model = gp.Model("mip1")

		#Q(p,l,f) Variable
		

	except gp.GurobiError as e:
		print('Error code ' + str(e.errno) + ': ' + str(e))

	except AttributeError:
		print('Encountered an attribute error')