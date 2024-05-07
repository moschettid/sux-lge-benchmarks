import it.unimi.dsi.Util;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.sux4j.mph.solve.Modulo2System;
import it.unimi.dsi.util.XoShiRo256PlusPlusRandom;

import java.util.ArrayList;
import java.util.Arrays;

import static it.unimi.dsi.sux4j.mph.solve.Modulo2System.lazyGaussianElimination;

public class Main {
    public static final double[] deltas = {0, 0, 0, 1.1, 1.03};
    public static void gen_equations(XoShiRo256PlusPlusRandom random, int nEqs, int nVarsPerEq, Modulo2System system, int[][] var2eq, long[] c){
        int n_vars = (int)Math.ceil(nEqs * deltas[nVarsPerEq]);

        final ArrayList<Integer>[] var2equations = new ArrayList[n_vars];
        for(int i=0; i<n_vars; i++) var2equations[i] = new ArrayList<>();

        final IntOpenHashSet edge = new IntOpenHashSet();
        int x;
        for (int i = 0; i < nEqs; i++) {
            edge.clear();
            for(int v=0; v<nVarsPerEq; v++){
                do x = (int) random.nextLong(n_vars); while(edge.contains(x));
                edge.add(x);
            }

            c[i] = random.nextLong(100);
            final Modulo2System.Modulo2Equation equation = new Modulo2System.Modulo2Equation(c[i], n_vars);
            for(final IntIterator iterator = edge.iterator(); iterator.hasNext();) {
                int v = iterator.nextInt();
                equation.add(v);
                var2equations[v].add(i);
            }
            system.add(equation);
        }

        for(int i=0; i<n_vars; i++) var2eq[i] = var2equations[i].stream().mapToInt(k -> k).toArray();
    }

    public static void main(String[] args) {
        var random = new XoShiRo256PlusPlusRandom(0);

        for (int nEqs : new int[]{100, 1000, 30000}){
            for(int nVarsPerEq : new int []{3, 4}){
                boolean solved;
                do {
                    int nVars = (int)Math.ceil(nEqs * deltas[nVarsPerEq]);
                    final Modulo2System system = new Modulo2System(nVars);
                    int[][] var2Eq = new int[nVars][];
                    final long[] c = new long[nEqs];

                    gen_equations(random, nEqs, nVarsPerEq, system, var2Eq, c);

                    final long[] solution = new long[nVars];
                    Arrays.fill(solution, 0);

                    System.out.printf("Lazy gaussian elimination w/ %d equations and %d vars\n", nEqs, nVarsPerEq);

                    solved = lazyGaussianElimination(system.copy(), var2Eq, c, Util.identity(nVars), solution);

                    System.out.println(solution[0]);
                    System.out.println();
                } while(!solved);
            }
        }
    }
}