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
import static br.usp.each.saeg.commons.ArrayUtils.merge;

import java.util.Arrays;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

public class PathAnalyzer<V extends Value> extends FlowAnalyzer<V> {

    private int[][] paths;

    public PathAnalyzer(final Interpreter<V> interpreter) {
        super(interpreter);
    }

    @Override
    public Frame<V>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
        final Frame<V>[] frames = super.analyze(owner, m);
        paths = new int[n][];
        if (frames.length == 0) {
            return frames;
        }

        final boolean[] queued = new boolean[n];
        final int[] queue = new int[n];
        final IntList list = new IntList();
        int top = 0;

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
