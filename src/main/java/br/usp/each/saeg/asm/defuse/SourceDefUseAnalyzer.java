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

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class SourceDefUseAnalyzer extends DefUseAnalyzer {

    private int[] lines;

    private Set<Variable>[] defs;

    private Set<Variable>[] puses;

    private Set<Variable>[] cuses;

    @Override
    @SuppressWarnings("unchecked")
    public DefUseFrame[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
        final DefUseFrame[] frames = super.analyze(owner, m);
        final InsnList insns = m.instructions;

        lines = new int[n];
        for (int i = 0; i < n; i++) {
            if (insns.get(i) instanceof LineNumberNode) {
                final LineNumberNode insn = (LineNumberNode) insns.get(i);
                lines[insns.indexOf(insn.start)] = insn.line;
            }
        }

        int line = 0;
        int firstLine = Integer.MAX_VALUE;
        int lastLine = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            if (lines[i] == 0) {
                lines[i] = line;
            } else {
                line = lines[i];
            }
            if (line > lastLine) {
                lastLine = line;
            }
            if (line < firstLine) {
                firstLine = line;
            }
        }

        defs = (Set<Variable>[]) new Set<?>[lastLine - firstLine + 1];
        puses = (Set<Variable>[]) new Set<?>[lastLine - firstLine + 1];
        cuses = (Set<Variable>[]) new Set<?>[lastLine - firstLine + 1];
        for (int l = 0; l < lastLine - firstLine + 1; l++) {
            defs[l] = new HashSet<Variable>();
            puses[l] = new HashSet<Variable>();
            cuses[l] = new HashSet<Variable>();
        }
        for (int i = 0; i < n; i++) {
            defs[lines[i] - firstLine].addAll(frames[i].getDefinitions());
            if (frames[i].predicate) {
                puses[lines[i] - firstLine].addAll(frames[i].getUses());
            } else {
                cuses[lines[i] - firstLine].addAll(frames[i].getUses());
            }
        }

        return frames;
    }

    public int[] getLines() {
        return lines;
    }

    public Set<Variable>[] getDefs() {
        return defs;
    }

    public Set<Variable>[] getPUses() {
        return puses;
    }

    public Set<Variable>[] getCUses() {
        return cuses;
    }

}
