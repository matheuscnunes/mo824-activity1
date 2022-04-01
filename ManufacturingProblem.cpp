#include <bits/stdc++.h>
#include "/Library/gurobi951/macos_universal2/lib/gurobi_c++.h"
 
using namespace std;
 
inline int getRandomNumber(int l, int r) {
    return rand() % r + l;
}
int main(int argc, char const *argv[]) {
    vector<int> values(10) = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
 
    for(int value: values) {
        cout << "Case J =  " << value << endl;
        try {
            GRBEnv env = GRBEnv(true);
            env.start();
            env.set("LogFile","manufacturing-problem.log");
            GRBModel model = GRBModel(env);
            int J = value;
            int F = getRandomNumber(J, 2 * J);
            int L = getRandomNumber(5, 10);
            int M = getRandomNumber(5, 10);
            int P = getRandomNumber(5, 10);
            cout << "Clients: " << J << endl;
            cout << "Factories: " << F << endl;
            cout << "Machines: " << L << endl;
            cout << "Raw materials: " << M << endl;
            cout << "Products: " << P << endl;
 
            // Create variables and constraints
 
            // D(j,p)
            int demandJP[J][P];
            for(int j = 0; j < J; j++) {
                for(int p = 0; p < P; p++) {
                    int demand = getRandomNumber(5, 10);
                    demandJP[j][p] = demand
                }
            }
 
            // r(m,p,l)
            int rawMaterialMPL[M][P][L];
            for (int m = 0; m < M; m++) {
                for (int p = 0; p < P; p++) {
                    for (int l = 0; l < L; l++) {
                        int materialNeed = getRandomNumber(1, 5);
                        rawMaterialMPL[m][p][l] = materialNeed;
                    }
                }
            }
 
            // R(m,f)
            int rawMaterialAvailableMF[M][F];
            for (int m = 0; m < M; m++) {
                for (int f = 0; f < F; f++) {
                    int available = getRandomNumber(800, 1000);
                    rawMaterialAvailableMF[m][f] = available;
                }
            }
 
            // C(l,f)
            int capacityLF[L][F];
            for (int l = 0; l < L; l++) {
                for (int f = 0; f < F; f++) {
                    int capacity = getRandomNumber(80, 100);
                    capacityLF[l][f] = capacity;
                }
            }
 
            // P(p,l,f)
            int manufacturingCostPLF[P][L][F];
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        int cost = getRandomNumber(10, 100);
                        manufacturingCostPLF[p][l][f] = cost;
                    }
                }
            }
 
            // t(p,f,j)
            int transportCostPFJ[P][F][J];
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        int cost = getRandomNumber(10, 20);
                        transportCostPFJ[p][f][j] = cost;
                    }
                }
            }
 
            // Q(p,l,f) Variable
            GRBVar quantityManufacturedPLF[P][L][F];
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        quantityManufacturedPLF[p][l][f] = model.addVar(0.0, 1.0, 0.0, GRB_INTEGER, "Q[" + to_string(p) + "][" + to_string(l) + "][" + to_string(f) + "]");
                    }
                }
            }
 
            // W(p,f,j) Variable
            GRB VarquantityTransportedPFJ[P][F][J];
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        quantityTransportedPFJ[p][f][j] = model.addVar(0.0, 1.0, 0.0, GRB_INTEGER, "W[" + to_string(p) + "][" + to_string(f) + "][" + to_string(j) + "]");
                    }
                }
            }
 
            // Integrate new variables
            model.update();
 
            // Objective MIN z = ∑∑∑Q(p,l,f) * p(p,l,f) + ∑∑∑W(p,f,j) * t(p,f,j)
            GRBLinExpr expr = 0.0;
            // ∑∑∑Q(p,l,f) * p(p,l,f)
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        expr += manufacturingCostPLF[p][l][f] * quantityManufacturedPLF[p][l][f];
                    }
                }
            }
            // ∑∑∑W(p,f,j) * t(p,f,j)
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        expr += transportCostPFJ[p][f][j] * quantityTransportedPFJ[p][f][j];
                    }
                }
            }
            model.setObjective(expr, GRB.MINIMIZE);
 
            // Restrictions
 
            // D(j,p) = (f∈F)∑ W(p,f,j) ∀j∀p
            for (int j = 0; j < J; j++) {
                for (int p = 0; p < P; p++) {
                    expr = 0.0;
                    for (int f = 0; f < F; f++) {
                        expr += quantityTransportedPFJ[p][f][j];
                    }
                    model.addConstr(expr, GRB.EQUAL, demandJP[j][p], "D[" + to_string(j) + "][" + to_string(p) + "]");
                }
            }
 
            // R(m,f) ≥ (l∈L)∑(p∈P)∑Q(p,l,f) * r(m,p,l) ∀m∀j
            for (int m = 0; m < M; m++) {
                for (int f = 0; f < F; f++) {
                    expr = 0.0;
                    for (int l = 0; l < L; l++) {
                        for (int p = 0; p < P; p++) {
                            expr += rawMaterialMPL[m][p][l] * quantityManufacturedPLF[p][l][f];
                        }
                    }
                    model.addConstr(expr, GRB.LESS_EQUAL, rawMaterialAvailableMF[m][f], "R[" + to_string(m) + "][" + to_string(f) + "]");
                }
            }
 
            // C(l,f) ≥ (p∈P)∑Q(p,l,f) ∀l∀f
            for (int l = 0; l < L; l++) {
                for (int f = 0; f < F; f++) {
                    expr = 0.0;
                    for (int p = 0; p < P; p++) {
                        expr += quantityManufacturedPLF[p][l][f];
                    }
                    model.addConstr(expr, GRB.LESS_EQUAL, capacityLF[l][f], "C[" + to_string(l) + "][" + to_string(f) + "]");
                }
            }
 
            // ∑Q(p,l,f) = ∑W(p,f,j) ∀p∀f
            GRBLinExpr exprA = 0.0
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        exprA += quantityManufacturedPLF[p][l][f];
                    }
                }
            }
 
            GRBLinExpr exprB = 0.0
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        exprB += quantityTransportedPFJ[p][f][j];
                    }
                }
            }
            model.addConstr(exprA, GRB.EQUAL, exprB, "Q(p,l,f) = W(p,l,j)");
 
 
            // Optimize model
            model.optimize();
            for (int p = 0; p < P; p++) {
                for (int l = 0; l < L; l++) {
                    for (int f = 0; f < F; f++) {
                        double value = quantityManufacturedPLF[p][l][f].get(GRB.DoubleAttr.X);
                        if (value > 0) {
                            cout << quantityManufacturedPLF[p][l][f].get(GRB.StringAttr.VarName) << ": " << (int)value << " ";
                        }
                    }
                }
            }
 
            for (int p = 0; p < P; p++) {
                for (int f = 0; f < F; f++) {
                    for (int j = 0; j < J; j++) {
                        double value = quantityTransportedPFJ[p][f][j].get(GRB.DoubleAttr.X);
                        if (value > 0) {
                            cout << quantityTransportedPFJ[p][f][j].get(GRB.StringAttr.VarName) << ": " << (int) value << " ";
                        }
                    }
                }
            }
 
            // Dispose of model and environment
            model.write("model.lp");
            model.dispose();
            env.dispose();
            cout << endl;
        } catch (GRBException e) {
            cout << "Error code: " << e.getErrorCode() << ". " << e.getMessage();
        }
    }
    return 0;
}