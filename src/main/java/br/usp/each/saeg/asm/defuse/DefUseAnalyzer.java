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

import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class DefUseAnalyzer implements Opcodes {

    private final Analyzer<Value> analyzer;

    private final DefUseInterpreter interpreter;

    private DefUseFrame[] duframes;

    private Variable[] variables;

    public DefUseAnalyzer() {
        this(new DefUseInterpreter());
    }

    private DefUseAnalyzer(final DefUseInterpreter interpreter) {
        this(new Analyzer<Value>(interpreter), interpreter);
    }

    public DefUseAnalyzer(final Analyzer<Value> analyzer, final DefUseInterpreter interpreter) {
        this.analyzer = analyzer;
        this.interpreter = interpreter;
    }

    public DefUseFrame[] analyze(final String owner, final MethodNode m)
            throws AnalyzerException {

        final int n = m.instructions.size();
        duframes = new DefUseFrame[n];

        final Frame<Value>[] frames = analyzer.analyze(owner, m);
        final Set<Variable> vars = new LinkedHashSet<Variable>();

        final Type[] args = Type.getArgumentTypes(m.desc);
        int local = 0;
        if ((m.access & ACC_STATIC) == 0) {
            final Type ctype = Type.getObjectType(owner);
            vars.add(new Local(ctype, local++));
        }
        for (final Type type : args) {
            vars.add(new Local(type, local++));
            if (type.getSize() == 2) {
                local++;
            }
        }
        final int nargs = (m.access & ACC_STATIC) == 0 ? args.length + 1 : args.length;

        for (int i = 0; i < n; i++) {
            final AbstractInsnNode insn = m.instructions.get(i);
            if (frames[i] == null) {
                duframes[i] = DefUseFrame.NONE;
            } else {
                duframes[i] = new DefUseFrame(frames[i], isPredicate(insn.getOpcode()));
            }
            switch (insn.getType()) {
            case AbstractInsnNode.LABEL:
            case AbstractInsnNode.LINE:
            case AbstractInsnNode.FRAME:
                break;
            default:
                if (duframes[i] != DefUseFrame.NONE) {
                    duframes[i].execute(insn, interpreter);
                    vars.addAll(duframes[i].getDefinitions());
                    vars.addAll(duframes[i].getUses());
                }
                break;
            }
        }
        for (int i = 0; i < n; i++) {
            if (duframes[i] != DefUseFrame.NONE) {
                reachDefs(m.instructions, i);
            }
        }
        variables = vars.toArray(new Variable[vars.size()]);

        if (frames.length == 0) {
            return duframes;
        }

        for (int i = 0; i < variables.length; i++) {
            final Variable def = variables[i];
            if (i < nargs || def instanceof StaticField) {
                duframes[0].addDef(def);
            } else if (def instanceof ObjectField) {
                final ObjectField defField = (ObjectField) def;
                if (duframes[0].getDefinitions().contains(defField.getRoot())) {
                    duframes[0].addDef(def);
                }
            }
        }

        return duframes;
    }

    private void reachDefs(final InsnList instructions, final int i) {
        for (final Variable var : duframes[i].getUses()) {
            if (var instanceof ObjectField) {
                final Value root = ((ObjectField) var).getRoot();
                if (root instanceof Local) {
                    final Local l = (Local) root;
                    for (final AbstractInsnNode def : duframes[i].getLocal(l.var).insns) {
                        final int index = instructions.indexOf(def);
                        duframes[index].addDef(var);
                    }
                }
            }
        }
    }

    public DefUseFrame[] getDefUseFrames() {
        return duframes;
    }

    public Variable[] getVariables() {
        return variables;
    }

    private boolean isPredicate(final int opcode) {
        if (opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE)
            return true;

        if (opcode == Opcodes.TABLESWITCH ||
            opcode == Opcodes.LOOKUPSWITCH ||
            opcode == Opcodes.IFNULL ||
            opcode == Opcodes.IFNONNULL) {
            return true;
        }
        return false;
    }

}
