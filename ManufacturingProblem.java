import gurobi.*;


public class ManufacturingProblem {
    public static void main(String[] args) {
        try {
            GRBEnv env = new GRBEnv("manufacturing-problem.log");
            GRBModel model = new GRBModel(env);
            int j = 10;
            int f = getRandomNumber(j, 2 * j); // Factories number
            int l = getRandomNumber(5, 10); // Machines number
            int m = getRandomNumber(5, 10); // Raw materials number
            int p = getRandomNumber(5, 10); // Products number

            // Create variables and constraints

            // D(j,p)
            GRBVar[][] demandJP = new GRBVar[j][p];
            for (int a = 0; a < j; a++) {
                for (int b = 0; b < p; b++) {
                    int demand = getRandomNumber(10, 20);
                    demandJP[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "D[" + a + "][" + b + "]");
                    model.addConstr(demand, GRB.EQUAL, demandJP[a][b], "D[" + a + "][" + b + "]");
                }
            }

            // r(m,p,l)
            GRBVar[][][] rawMaterialMPL = new GRBVar[m][p][l];
            for (int a = 0; a < m; a++) {
                for (int b = 0; b < p; b++) {
                    for (int c = 0; c < l; c++) {
                        int materialNeed = getRandomNumber(1, 5);
                        rawMaterialMPL[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "r[" + a + "][" + b + "][" + c + "]");
                        model.addConstr(materialNeed, GRB.EQUAL, rawMaterialMPL[a][b][c], "r[" + a + "][" + b + "][" + c + "]");
                    }
                }
            }

            // R(m,f)
            GRBVar[][] rawMaterialAvailableMF = new GRBVar[m][f];
            for (int a = 0; a < m; a++) {
                for (int b = 0; b < f; b++) {
                    int available = getRandomNumber(800, 1000);
                    rawMaterialAvailableMF[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "R[" + a + "][" + b + "]");
                    model.addConstr(available, GRB.EQUAL, rawMaterialAvailableMF[a][b], "R[" + a + "][" + b + "]");
                }
            }

            // C(l,f)
            GRBVar[][] capacityLF = new GRBVar[l][f];
            for (int a = 0; a < m; a++) {
                for (int b = 0; b < f; b++) {
                    int capacity = getRandomNumber(80, 100);
                    capacityLF[a][b] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "C[" + a + "][" + b + "]");
                    model.addConstr(capacity, GRB.EQUAL, capacityLF[a][b], "C[" + a + "][" + b + "]");
                }
            }

            // P(p,l,f)
            GRBVar[][][] manufacturingCostPLF = new GRBVar[p][l][f];
            for (int a = 0; a < m; a++) {
                for (int b = 0; b < p; b++) {
                    for (int c = 0; c < l; c++) {
                        int cost = getRandomNumber(10, 100);
                        manufacturingCostPLF[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "p[" + a + "][" + b + "][" + c + "]");
                        model.addConstr(cost, GRB.EQUAL, manufacturingCostPLF[a][b][c], "p[" + a + "][" + b + "][" + c + "]");
                    }
                }
            }

            // t(p,f,j)
            GRBVar[][][] transportCostPFJ = new GRBVar[p][f][j];
            for (int a = 0; a < m; a++) {
                for (int b = 0; b < p; b++) {
                    for (int c = 0; c < l; c++) {
                        int cost = getRandomNumber(10, 20);
                        transportCostPFJ[a][b][c] = model.addVar(0.0, 1.0, 0.0, GRB.INTEGER, "t[" + a + "][" + b + "][" + c + "]");
                        model.addConstr(cost, GRB.EQUAL, transportCostPFJ[a][b][c], "t[" + a + "][" + b + "][" + c + "]");
                    }
                }
            }

            // TODO: Define Q(p,l,f) variables

            // TODO: Define W(p,f,j) variables

            // Integrate new variables
            model.update();

            // TODO: Define objective MIN z = ∑∑∑Q(p,l,f) * p(p,l,f) + ∑∑∑W(p,f,j) * t(p,f,j)
            GRBLinExpr expr = new GRBLinExpr();
            for (int a = 0; a < p; a++) {
                for (int b = 0; b < f; b++) {
                    for (int c = 0; c < l; c++) {
//                        expr.addTerm(1.0, ...);
                    }
                }
            }
            model.setObjective(expr, GRB.MINIMIZE);

            // TODO: Define restrictions

            // Optimize model
            model.optimize();

            // TODO: Print results

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
