/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2014 Roberto Araujo (roberto.andrioli@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.usp.each.saeg.asm.defuse;

import static br.usp.each.saeg.commons.ArrayUtils.indexOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReachDefsDefUseChainSearch implements DefUseChainSearch {

    private DefUseFrame[] frames;

    private Variable[] variables;

    private int[][] successors;

    private int[][] predecessors;

    private RDSet[] rdSets;

    private int n;

    @Override
    public DefUseChain[] search(final DefUseFrame[] frames, final Variable[] variables,
            final int[][] adjacencyListSucc, final int[][] adjacencyListPred) {

        this.frames = frames;
        this.variables = variables;
        successors = adjacencyListSucc;
        predecessors = adjacencyListPred;
        n = frames.length;

        computeGenAndKill();
        reachingDefinitions();
        return computeDefUseChains();
    }

    private void computeGenAndKill() {
        rdSets = new RDSet[n];
        for (int i = 0; i < n; i++) {
            rdSets[i] = new RDSet(variables.length);
            for (final Variable def : frames[i].getDefinitions()) {
                final int var = indexOf(variables, def);
                rdSets[i].gen(i, var);
                for (int j = 0; j < n; j++) {
                    for (final Variable other : frames[j].getDefinitions()) {
                        if (i != j && def.equals(other)) {
                            rdSets[i].kill(j, var);
                        }
                    }
                }
            }
        }
    }

    private void reachingDefinitions() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < n; i++) {

                rdSets[i].in.clear();
                for (final int pred : predecessors[i]) {
                    rdSets[i].in.addAll(rdSets[pred].out);
                }

                final Set<Integer> oldout = new HashSet<Integer>(rdSets[i].out);
                final Set<Integer> temp = new HashSet<Integer>(rdSets[i].in);
                temp.removeAll(rdSets[i].kill);
                rdSets[i].out.clear();
                rdSets[i].out.addAll(rdSets[i].gen);
                rdSets[i].out.addAll(temp);

                if (!rdSets[i].out.equals(oldout)) {
                    changed = true;
                }
            }
        }
    }

    private DefUseChain[] computeDefUseChains() {
        final List<DefUseChain> chains = new ArrayList<DefUseChain>();
        for (int i = 0; i < n; i++) {
            for (final Variable use : frames[i].getUses()) {

                final int var = indexOf(variables, use);

                if (frames[i].predicate) {

                    for (int j = 0; j < n; j++) {
                        if (rdSets[i].out(j, var)) {
                            for (final int succ : successors[i]) {
                                chains.add(new DefUseChain(j, i, succ, var));
                            }
                        }
                    }

                } else {
                    for (int j = 0; j < n; j++) {
                        if (rdSets[i].in(j, var)) {
                            chains.add(new DefUseChain(j, i, var));
                        }
                    }
                }

            }
        }
        return chains.toArray(new DefUseChain[chains.size()]);
    }

    public RDSet[] getRDSets() {
        return rdSets;
    }

    public static class RDSet {

        private final Set<Integer> in;
        private final Set<Integer> out;
        private final Set<Integer> gen;
        private final Set<Integer> kill;

        private final int vars;

        public RDSet(final int variables) {
            in = new HashSet<Integer>();
            out = new HashSet<Integer>();
            gen = new HashSet<Integer>();
            kill = new HashSet<Integer>();
            vars = variables;
        }

        public Set<Integer> gen() {
            return Collections.unmodifiableSet(gen);
        }

        public void gen(final int insn, final int var) {
            gen.add(insn * vars + var);
        }

        public Set<Integer> kill() {
            return Collections.unmodifiableSet(kill);
        }

        public void kill(final int insn, final int var) {
            kill.add(insn * vars + var);
        }

        public boolean in(final int insn, final int var) {
            return in.contains(insn * vars + var);
        }

        public boolean out(final int insn, final int var) {
            return out.contains(insn * vars + var);
        }

    }

}
