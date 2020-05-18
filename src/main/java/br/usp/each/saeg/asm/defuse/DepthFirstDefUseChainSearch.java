/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2020 Roberto Araujo (roberto.andrioli@gmail.com)
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
import java.util.List;

public class DepthFirstDefUseChainSearch implements DefUseChainSearch {

    private DefUseFrame[] frames;

    private Variable[] variables;

    private int[][] successors;

    private int n;

    @Override
    public DefUseChain[] search(final DefUseFrame[] frames, final Variable[] variables,
            final int[][] adjacencyListSucc, final int[][] adjacencyListPred) {

        this.frames = frames;
        this.variables = variables;
        successors = adjacencyListSucc;
        n = frames.length;

        final List<DefUseChain> list = new ArrayList<DefUseChain>();
        for (int i = 0; i < n; i++) {
            for (final Variable def : frames[i].getDefinitions()) {
                DFS(def, i, list);
            }
        }
        return list.toArray(new DefUseChain[list.size()]);
    }

    /*
     * The search visits every instruction j which is syntactically reachable
     * from i by some definition-clear path.
     */
    private void DFS(final Variable def, final int i, final List<DefUseChain> list) {
        final boolean[] queued = new boolean[n];
        final int[] queue = new int[n];
        int top = 0;
        for (final int succ : successors[i]) {
            queue[top++] = succ;
            queued[succ] = true;
        }
        while (top > 0) {

            final int j = queue[--top];

            // is not necessary remove queued mark (since a node is visited only
            // once). We use the queued mark to indicate that a node has already
            // been visited or will be visited soon.

            if (frames[j].getUses().contains(def)) {
                // reaching definition
                if (frames[j].predicate) {
                    for (final int succ : successors[j]) {
                        list.add(new DefUseChain(i, j, succ, indexOf(variables, def)));
                    }
                } else {
                    list.add(new DefUseChain(i, j, indexOf(variables, def)));
                }
            }
            if (frames[j].getDefinitions().contains(def)) {
                // backtrack
                continue;
            }

            for (final int succ : successors[j]) {
                if (!queued[succ]) {
                    queue[top++] = succ;
                    queued[succ] = true;
                }
            }
        }
    }

}
