/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2016 Roberto Araujo (roberto.andrioli@gmail.com)
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
import static br.usp.each.saeg.commons.ArrayUtils.merge;
import static br.usp.each.saeg.commons.ArrayUtils.toArray;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

public class FlowAnalyzer<V extends Value> extends Analyzer<V> {

    private Set<Integer>[] successors;

    private Set<Integer>[] predecessors;

    private int[][] blocks;

    private int[] leaders;

    private int[][] paths;

    private int n;

    public FlowAnalyzer(final Interpreter<V> interpreter) {
        super(interpreter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Frame<V>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
        n = m.instructions.size();

        blocks = new int[n][];
        leaders = new int[n];
        paths = new int[n][];
        Arrays.fill(leaders, -1);
        successors = (Set<Integer>[]) new Set<?>[n];
        predecessors = (Set<Integer>[]) new Set<?>[n];
        for (int i = 0; i < n; i++) {
            successors[i] = new LinkedHashSet<Integer>();
            predecessors[i] = new LinkedHashSet<Integer>();
        }

        final Frame<V>[] frames = super.analyze(owner, m);
        if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            return (Frame<V>[]) new Frame<?>[0];
        }

        final boolean[] queued = new boolean[n];
        final int[] queue = new int[n];
        int top = 0;
        int basicBlock = 0;
        queue[top++] = 0;
        queued[0] = true;
        final IntList list = new IntList();

        while (top > 0) {
            int i = queue[--top];
            leaders[i] = basicBlock;
            list.add(i);
            while (successors[i].size() == 1) {
                final int child = successors[i].iterator().next();
                if (queued[child] || predecessors[child].size() > 1) {
                    break;
                }
                i = child;
                leaders[i] = basicBlock;
                list.add(i);
            }
            blocks[basicBlock] = list.toArray();
            list.clear();
            basicBlock++;
            for (final int succ : successors[i]) {
                if (!queued[succ]) {
                    queue[top++] = succ;
                    queued[succ] = true;
                }
            }
        }
        blocks = Arrays.copyOf(blocks, basicBlock);

        Arrays.fill(queued, false);
        for (int i = 0; i < n; i++) {
            if (successors[i].size() == 0 && leaders[i] != -1) {
                queue[top++] = i;
                queued[i] = true;
            }
        }

        while (top > 0) {
            final int i = queue[--top];
            int b = leaders[i];
            list.add(b);
            while (predecessors[blocks[b][0]].size() == 1 && blocks[b][0] != 0) {
                b = leaders[predecessors[blocks[b][0]].iterator().next()];
                list.add(b);
            }
            paths[i] = list.toReverseArray();
            list.clear();
            for (final int pred : predecessors[blocks[b][0]]) {
                if (!queued[pred]) {
                    queue[top++] = pred;
                    queued[pred] = true;
                }
            }
        }

        return frames;
    }

    @Override
    protected void newControlFlowEdge(final int insn, final int successor) {
        successors[insn].add(successor);
        predecessors[successor].add(insn);
    }

    @Override
    protected boolean newControlFlowExceptionEdge(final int insn, final int successor) {
        // ignoring exception flow
        return false;
    }

    public int[] getSuccessors(final int insn) {
        return toArray(successors[insn], new int[0]);
    }

    public int[][] getSuccessors() {
        final int[][] successors = new int[n][];
        for (int i = 0; i < n; i++) {
            successors[i] = getSuccessors(i);
        }
        return successors;
    }

    public int[] getPredecessors(final int insn) {
        return toArray(predecessors[insn], new int[0]);
    }

    public int[][] getPredecessors() {
        final int[][] predecessors = new int[n][];
        for (int i = 0; i < n; i++) {
            predecessors[i] = getPredecessors(i);
        }
        return predecessors;
    }

    public int[] getLeaders() {
        return leaders;
    }

    public int[] getBasicBlock(final int id) {
        return blocks[id];
    }

    public int[][] getBasicBlocks() {
        return blocks;
    }

    public int[][] getPaths() {
        return paths;
    }

    public int[] getPath(final int insn) {
        int[] path = paths[insn];

        if (path == null) {
            path = blocks[leaders[insn]];
            if (paths[path[path.length - 1]] == null) {
                if (path[0] != 0 && predecessors[path[0]].size() == 1) {
                    path = merge(getPath(predecessors[path[0]].iterator().next()), path);
                }
                return Arrays.copyOf(path, indexOf(path, insn) + 1);
            }
            path = paths[path[path.length - 1]];
        }

        int size = 0;
        for (final int block : path) {
            size = size + blocks[block].length;
        }
        final int[] insnPath = new int[size];

        size = 0;
        for (final int block : path) {
            System.arraycopy(blocks[block], 0, insnPath, size, blocks[block].length);
            size = size + blocks[block].length;
        }

        return Arrays.copyOf(insnPath, indexOf(insnPath, insn) + 1);
    }

}
