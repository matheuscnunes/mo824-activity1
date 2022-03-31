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

            // Create variables and constraints

            // D(j,p)
//            GRBVar[][] demandJP = new GRBVar[j][p];
//            for (int a = 0; a < j; a++) {
//                for (int b = 0; b < p; b++) {
//                    int demand = getRandomNumber(10, 20);
//                    demandJP[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "D[" + a + "][" + b + "]");
//                    model.addConstr(demand, GRB.EQUAL, demandJP[a][b], "D[" + a + "][" + b + "]");
//                }
//            }
            int[][] demandJP = new int[J][P];
            for (int j = 0; j < J; j++) {
                for (int p = 0; p < P; p++) {
                    int demand = getRandomNumber(10, 20);
                    demandJP[j][p] = demand;
                }
            }

            // r(m,p,l)
//            GRBVar[][][] rawMaterialMPL = new GRBVar[m][p][l];
//            for (int a = 0; a < m; a++) {
//                for (int b = 0; b < p; b++) {
//                    for (int c = 0; c < l; c++) {
//                        int materialNeed = getRandomNumber(1, 5);
//                        rawMaterialMPL[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "r[" + a + "][" + b + "][" + c + "]");
//                        model.addConstr(materialNeed, GRB.EQUAL, rawMaterialMPL[a][b][c], "r[" + a + "][" + b + "][" + c + "]");
//                    }
//                }
//            }
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
//            GRBVar[][] rawMaterialAvailableMF = new GRBVar[m][f];
//            for (int a = 0; a < m; a++) {
//                for (int b = 0; b < f; b++) {
//                    int available = getRandomNumber(800, 1000);
//                    rawMaterialAvailableMF[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "R[" + a + "][" + b + "]");
//                    model.addConstr(available, GRB.EQUAL, rawMaterialAvailableMF[a][b], "R[" + a + "][" + b + "]");
//                }
//            }
            int[][] rawMaterialAvailableMF = new int[M][F];
            for (int m = 0; m < M; m++) {
                for (int f = 0; f < F; f++) {
                    int available = getRandomNumber(800, 1000);
                    rawMaterialAvailableMF[m][f] = available;
                }
            }

            // C(l,f)
//            GRBVar[][] capacityLF = new GRBVar[l][f];
//            for (int a = 0; a < m; a++) {
//                for (int b = 0; b < f; b++) {
//                    int capacity = getRandomNumber(80, 100);
//                    capacityLF[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "C[" + a + "][" + b + "]");
//                    model.addConstr(capacity, GRB.EQUAL, capacityLF[a][b], "C[" + a + "][" + b + "]");
//                }
//            }
            int[][] capacityLF = new int[L][F];
            for (int m = 0; m < L; m++) {
                for (int f = 0; f < F; f++) {
                    int capacity = getRandomNumber(80, 100);
                    capacityLF[m][f] = capacity;
                }
            }

            // P(p,l,f)
//            GRBVar[][][] manufacturingCostPLF = new GRBVar[p][l][f];
//            for (int a = 0; a < m; a++) {
//                for (int b = 0; b < p; b++) {
//                    for (int c = 0; c < l; c++) {
//                        int cost = getRandomNumber(10, 100);
//                        manufacturingCostPLF[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "p[" + a + "][" + b + "][" + c + "]");
//                        model.addConstr(cost, GRB.EQUAL, manufacturingCostPLF[a][b][c], "p[" + a + "][" + b + "][" + c + "]");
//                    }
//                }
//            }
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
//            GRBVar[][][] transportCostPFJ = new GRBVar[p][f][j];
//            for (int a = 0; a < m; a++) {
//                for (int b = 0; b < p; b++) {
//                    for (int c = 0; c < l; c++) {
//                        int cost = getRandomNumber(10, 20);
//                        transportCostPFJ[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "t[" + a + "][" + b + "][" + c + "]");
//                        model.addConstr(cost, GRB.EQUAL, transportCostPFJ[a][b][c], "t[" + a + "][" + b + "][" + c + "]");
//                    }
//                }
//            }
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
                        quantityManufacturedPLF[p][l][f] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "Q[" + p + "][" + l + "][" + f + "]");
                    }
                }
            }

            // W(p,f,j) Variable
            GRBVar[][][] quantityTransportedPFJ = new GRBVar[P][F][J];
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        quantityTransportedPFJ[p][f][j] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "W[" + p + "][" + f + "][" + j + "]");
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
                    model.addConstr(expr, GRB.LESS_EQUAL, capacityLF[l][f], "C[" + l + "][" + f + "]");
                }
            }

            // Q(p,l,f), W(p,f,j) ≥ 0
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        expr = new GRBLinExpr();
                        expr.addTerm(1.0, quantityManufacturedPLF[p][l][f]);
                        model.addConstr(expr, GRB.GREATER_EQUAL, 0.0, "Q[" + p + "][" + l + "][" + f + "]");
                    }
                }
            }
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        expr = new GRBLinExpr();
                        expr.addTerm(1.0, quantityTransportedPFJ[p][f][j]);
                        model.addConstr(expr, GRB.GREATER_EQUAL, 0.0, "W[" + p + "][" + f + "][" + j + "]");
                    }
                }
            }

            // Optimize model
            model.optimize();

            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        System.out.print((int) quantityManufacturedPLF[p][l][f].get(GRB.DoubleAttr.X) + " ");
                    }
                    System.out.println();
                }
            }

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
