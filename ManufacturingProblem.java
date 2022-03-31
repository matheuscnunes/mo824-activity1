import gurobi.*;


public class ManufacturingProblem {
    public static void main(String[] args) {
        try {
            GRBEnv env = new GRBEnv("manufacturing-problem.log");
            GRBModel model = new GRBModel(env);
            int J = 100;
            int F = getRandomNumber(J, 2 * J); // Factories number
            int L = getRandomNumber(5, 10); // Machines number
            int M = getRandomNumber(5, 10); // Raw materials number
            int P = getRandomNumber(5, 10); // Products number
            System.out.println("Clients: " + J);
            System.out.println("Factories: " + F);
            System.out.println("Machines: " + L);
            System.out.println("Raw materials: " + M);
            System.out.println("Products: " + P);
            System.out.println("\n\n");

            int totalDemand = 0;
            int totalVariables = 0;
            int totalRestrictions = 0;

            // Create variables and constraints

            // D(j,p)
            int[][] demandJP = new int[J][P];
            for (int j = 0; j < J; j++) {
                for (int p = 0; p < P; p++) {
                    int demand = getRandomNumber(10, 20);
                    totalDemand += demand;
                    demandJP[j][p] = demand;
                }
            }

            // r(m,p,l)
            int[][][] rawMaterialMPL = new int[M][P][L];
            for (int m = 0; m < M; m++) {
                for (int p = 0; p < P; p++) {
                    for (int l = 0; l < L; l++) {
                        int materialNeed = getRandomNumber(1, 5);
                        rawMaterialMPL[m][p][l] = materialNeed;
                    }
                }
            }

            // R(m,f)
            int[][] rawMaterialAvailableMF = new int[M][F];
            for (int m = 0; m < M; m++) {
                for (int f = 0; f < F; f++) {
                    int available = getRandomNumber(800, 1000);
                    rawMaterialAvailableMF[m][f] = available;
                }
            }

            // C(l,f)
            int[][] capacityLF = new int[L][F];
            for (int l = 0; l < L; l++) {
                for (int f = 0; f < F; f++) {
                    int capacity = getRandomNumber(80, 100);
                    capacityLF[l][f] = capacity;
                }
            }

            // P(p,l,f)
            int[][][] manufacturingCostPLF = new int[P][L][F];
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        int cost = getRandomNumber(10, 100);
                        manufacturingCostPLF[p][l][f] = cost;
                    }
                }
            }

            // t(p,f,j)
            int[][][] transportCostPFJ = new int[P][F][J];
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        int cost = getRandomNumber(10, 20);
                        transportCostPFJ[p][f][j] = cost;
                    }
                }
            }

            // Q(p,l,f) Variable
            GRBVar[][][] quantityManufacturedPLF = new GRBVar[P][L][F];
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        totalVariables++;
                        quantityManufacturedPLF[p][l][f] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.INTEGER, "Q[" + p + "][" + l + "][" + f + "]");
                    }
                }
            }

            // W(p,f,j) Variable
            GRBVar[][][] quantityTransportedPFJ = new GRBVar[P][F][J];
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        totalVariables++;
                        quantityTransportedPFJ[p][f][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.INTEGER, "W[" + p + "][" + f + "][" + j + "]");
                    }
                }
            }

            // Integrate new variables
            model.update();

            // Objective MIN z = ∑∑∑Q(p,l,f) * p(p,l,f) + ∑∑∑W(p,f,j) * t(p,f,j)
            GRBLinExpr expr = new GRBLinExpr();
            // ∑∑∑Q(p,l,f) * p(p,l,f)
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        expr.addTerm(manufacturingCostPLF[p][l][f], quantityManufacturedPLF[p][l][f]);
                    }
                }
            }
            // ∑∑∑W(p,f,j) * t(p,f,j)
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        expr.addTerm(transportCostPFJ[p][f][j], quantityTransportedPFJ[p][f][j]);
                    }
                }
            }
            model.setObjective(expr, GRB.MINIMIZE);

            // Restrictions

            // D(j,p) = (f∈F)∑ W(p,f,j) ∀j∀p
            for (int j = 0; j < J; j++) {
                for (int p = 0; p < P; p++) {
                    expr = new GRBLinExpr();
                    for (int f = 0; f < F; f++) {
                        expr.addTerm(1.0, quantityTransportedPFJ[p][f][j]);
                    }
                    totalRestrictions++;
                    model.addConstr(expr, GRB.EQUAL, demandJP[j][p], "D[" + j + "][" + p + "]");
                }
            }

            // R(m,f) ≥ (l∈L)∑(p∈P)∑Q(p,l,f) * r(m,p,l) ∀m∀j
            for (int m = 0; m < M; m++) {
                for (int f = 0; f < F; f++) {
                    expr = new GRBLinExpr();
                    for (int l = 0; l < L; l++) {
                        for (int p = 0; p < P; p++) {
                            expr.addTerm(rawMaterialMPL[m][p][l], quantityManufacturedPLF[p][l][f]);
                        }
                    }
                    totalRestrictions++;
                    model.addConstr(expr, GRB.LESS_EQUAL, rawMaterialAvailableMF[m][f], "R[" + m + "][" + f + "]");
                }
            }

            // C(l,f) ≥ (p∈P)∑Q(p,l,f) ∀l∀f
            for (int l = 0; l < L; l++) {
                for (int f = 0; f < F; f++) {
                    expr = new GRBLinExpr();
                    for (int p = 0; p < P; p++) {
                        expr.addTerm(1.0, quantityManufacturedPLF[p][l][f]);
                    }
                    totalRestrictions++;
                    model.addConstr(expr, GRB.LESS_EQUAL, capacityLF[l][f], "C[" + l + "][" + f + "]");
                }
            }

            // (l∈f)∑Q(p,l,f) = (j∈J)∑W(p,f,j) ∀p∀f
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    expr = new GRBLinExpr();
                    for (int l = 0; l < L; l++) {
                        expr.addTerm(1.0, quantityManufacturedPLF[p][l][f]);
                    }
                    for (int j = 0; j < J; j++) {
                        expr.addTerm(-1.0, quantityTransportedPFJ[p][f][j]);
                    }

                    totalRestrictions++;
                    model.addConstr(expr, GRB.EQUAL, 0.0, "Q[" + p + "][" + f + "]=W[" + p + "][" + f + "]");
                }
            }

            // Optimize model
            model.optimize();

            int manufactured = 0;
            int transported = 0;
            System.out.println("RESULTS\n");
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        double value = quantityManufacturedPLF[p][l][f].get(GRB.DoubleAttr.X);
                        manufactured += value;
                        if (value > 0) {
                            System.out.print(quantityManufacturedPLF[p][l][f].get(GRB.StringAttr.VarName) + ": " + (int) value + " ");
                        }
                    }
                }
            }
            System.out.println("\n\n-----------------------------------------------------------------\n");

            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        double value = quantityTransportedPFJ[p][f][j].get(GRB.DoubleAttr.X);
                        transported += value;
                        if (value > 0) {
                            System.out.print(quantityTransportedPFJ[p][f][j].get(GRB.StringAttr.VarName) + ": " + (int) value + " ");
                        }
                    }
                }
            }
            System.out.println("\n-----------------------------------------------------------------\n\n");
            System.out.println("Total demand: " + totalDemand + " - Total manufactured: " + manufactured + " - Total transported: " + transported);
            System.out.println("Total variables: " + totalVariables + " - Total restrictions: " + totalRestrictions);
            System.out.println("\n-----------------------------------------------------------------");

            // Dispose of model and environment
            model.write("model.lp");
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
