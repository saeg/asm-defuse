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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

public class DefUseFrame extends Frame<Value> {

    public static final DefUseFrame NONE = new DefUseFrame(0, 0);

    public final boolean predicate;

    private Set<Variable> defs = Collections.emptySet();

    private Set<Variable> uses = Collections.emptySet();

    public DefUseFrame(final int nLocals, final int nStack) {
        super(nLocals, nStack);
        predicate = false;
    }

    public DefUseFrame(final Frame<? extends Value> src) {
        super(src);
        this.predicate = false;
    }

    public DefUseFrame(final Frame<Value> src, final boolean predicate) {
        super(src);
        this.predicate = predicate;
    }

    public Set<Variable> getDefinitions() {
        return defs;
    }

    public Set<Variable> getUses() {
        return uses;
    }

    @Override
    public void execute(final AbstractInsnNode insn, final Interpreter<Value> interpreter)
            throws AnalyzerException {

        Value value1, value2, value3;
        List<Value> values;
        int var;
        Variable variable;

        switch (insn.getOpcode()) {
        case Opcodes.NOP:
            break;
        case Opcodes.ACONST_NULL:
        case Opcodes.ICONST_M1:
        case Opcodes.ICONST_0:
        case Opcodes.ICONST_1:
        case Opcodes.ICONST_2:
        case Opcodes.ICONST_3:
        case Opcodes.ICONST_4:
        case Opcodes.ICONST_5:
        case Opcodes.LCONST_0:
        case Opcodes.LCONST_1:
        case Opcodes.FCONST_0:
        case Opcodes.FCONST_1:
        case Opcodes.FCONST_2:
        case Opcodes.DCONST_0:
        case Opcodes.DCONST_1:
        case Opcodes.BIPUSH:
        case Opcodes.SIPUSH:
        case Opcodes.LDC:
        case Opcodes.ILOAD:
        case Opcodes.LLOAD:
        case Opcodes.FLOAD:
        case Opcodes.DLOAD:
        case Opcodes.ALOAD:
        case Opcodes.IALOAD:
        case Opcodes.LALOAD:
        case Opcodes.FALOAD:
        case Opcodes.DALOAD:
        case Opcodes.AALOAD:
        case Opcodes.BALOAD:
        case Opcodes.CALOAD:
        case Opcodes.SALOAD:
            super.execute(insn, interpreter);
            break;
        case Opcodes.ISTORE:
        case Opcodes.LSTORE:
        case Opcodes.FSTORE:
        case Opcodes.DSTORE:
        case Opcodes.ASTORE:
            var = ((VarInsnNode) insn).var;
            value1 = pop();
            variable = new Local(value1.type, var);
            defs = Collections.singleton(variable);
            uses = value1.getVariables();
            value1 = interpreter.copyOperation(insn, value1);
            setLocal(var, value1);
            if (value1.getSize() == 2) {
                setLocal(var + 1, interpreter.newEmptyValue(var + 1));
            }
            if (var > 0) {
                final Value local = getLocal(var - 1);
                if (local != null && local.getSize() == 2) {
                    setLocal(var - 1, interpreter.newEmptyValue(var - 1));
                }
            }
            break;
        case Opcodes.IASTORE:
        case Opcodes.LASTORE:
        case Opcodes.FASTORE:
        case Opcodes.DASTORE:
        case Opcodes.AASTORE:
        case Opcodes.BASTORE:
        case Opcodes.CASTORE:
        case Opcodes.SASTORE:
            value3 = pop();
            value2 = pop();
            value1 = pop();
            uses = new LinkedHashSet<Variable>();
            uses.addAll(value3.getVariables());
            uses.addAll(value2.getVariables());
            uses.addAll(value1.getVariables());
            uses = Collections.unmodifiableSet(uses);
            break;
        case Opcodes.POP:
            value1 = pop();
            if (value1 instanceof Invoke) {
                uses = value1.getVariables();
            }
            break;
        case Opcodes.POP2:
            value1 = pop();
            value2 = null;
            if (value1.getSize() == 1) {
                value2 = pop();
            }
            uses = new LinkedHashSet<Variable>();
            if (value1 instanceof Invoke) {
                uses.addAll(value1.getVariables());
            }
            if (value2 instanceof Invoke) {
                uses.addAll(value2.getVariables());
            }
            break;
        case Opcodes.DUP:
        case Opcodes.DUP_X1:
        case Opcodes.DUP_X2:
        case Opcodes.DUP2:
        case Opcodes.DUP2_X1:
        case Opcodes.DUP2_X2:
        case Opcodes.SWAP:
        case Opcodes.IADD:
        case Opcodes.LADD:
        case Opcodes.FADD:
        case Opcodes.DADD:
        case Opcodes.ISUB:
        case Opcodes.LSUB:
        case Opcodes.FSUB:
        case Opcodes.DSUB:
        case Opcodes.IMUL:
        case Opcodes.LMUL:
        case Opcodes.FMUL:
        case Opcodes.DMUL:
        case Opcodes.IDIV:
        case Opcodes.LDIV:
        case Opcodes.FDIV:
        case Opcodes.DDIV:
        case Opcodes.IREM:
        case Opcodes.LREM:
        case Opcodes.FREM:
        case Opcodes.DREM:
        case Opcodes.INEG:
        case Opcodes.LNEG:
        case Opcodes.FNEG:
        case Opcodes.DNEG:
        case Opcodes.ISHL:
        case Opcodes.LSHL:
        case Opcodes.ISHR:
        case Opcodes.LSHR:
        case Opcodes.IUSHR:
        case Opcodes.LUSHR:
        case Opcodes.IAND:
        case Opcodes.LAND:
        case Opcodes.IOR:
        case Opcodes.LOR:
        case Opcodes.IXOR:
        case Opcodes.LXOR:
            super.execute(insn, interpreter);
            break;
        case Opcodes.IINC:
            var = ((IincInsnNode) insn).var;
            setLocal(var, interpreter.unaryOperation(insn, getLocal(var)));
            variable = new Local(Type.INT_TYPE, var);
            defs = Collections.singleton(variable);
            uses = defs;
            break;
        case Opcodes.I2L:
        case Opcodes.I2F:
        case Opcodes.I2D:
        case Opcodes.L2I:
        case Opcodes.L2F:
        case Opcodes.L2D:
        case Opcodes.F2I:
        case Opcodes.F2L:
        case Opcodes.F2D:
        case Opcodes.D2I:
        case Opcodes.D2L:
        case Opcodes.D2F:
        case Opcodes.I2B:
        case Opcodes.I2C:
        case Opcodes.I2S:
        case Opcodes.LCMP:
        case Opcodes.FCMPL:
        case Opcodes.FCMPG:
        case Opcodes.DCMPL:
        case Opcodes.DCMPG:
            super.execute(insn, interpreter);
            break;
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
            uses = pop().getVariables();
            break;
        case Opcodes.IF_ICMPEQ:
        case Opcodes.IF_ICMPNE:
        case Opcodes.IF_ICMPLT:
        case Opcodes.IF_ICMPGE:
        case Opcodes.IF_ICMPGT:
        case Opcodes.IF_ICMPLE:
        case Opcodes.IF_ACMPEQ:
        case Opcodes.IF_ACMPNE:
            uses = new LinkedHashSet<Variable>();
            uses.addAll(pop().getVariables());
            uses.addAll(pop().getVariables());
            break;
        case Opcodes.GOTO:
            break;
        case Opcodes.JSR:
            super.execute(insn, interpreter);
            break;
        case Opcodes.RET:
            break;
        case Opcodes.TABLESWITCH:
        case Opcodes.LOOKUPSWITCH:
        case Opcodes.IRETURN:
        case Opcodes.LRETURN:
        case Opcodes.FRETURN:
        case Opcodes.DRETURN:
        case Opcodes.ARETURN:
            uses = pop().getVariables();
            break;
        case Opcodes.RETURN:
        case Opcodes.GETSTATIC:
            super.execute(insn, interpreter);
            break;
        case Opcodes.PUTSTATIC: {
            final FieldInsnNode f = (FieldInsnNode) insn;
            variable = new StaticField(f.owner, f.name, f.desc);
            defs = Collections.singleton(variable);
            uses = pop().getVariables();
            break;
        }
        case Opcodes.GETFIELD:
            super.execute(insn, interpreter);
            break;
        case Opcodes.PUTFIELD: {
            final FieldInsnNode f = (FieldInsnNode) insn;
            value2 = pop();
            value1 = pop();
            variable = new ObjectField(f.owner, f.name, f.desc, value1);
            defs = Collections.singleton(variable);
            uses = new LinkedHashSet<Variable>();
            uses.addAll(value2.getVariables());
            uses.addAll(value1.getVariables());
            uses = Collections.unmodifiableSet(uses);
            break;
        }
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE: {
            values = new ArrayList<Value>();
            final String desc = ((MethodInsnNode) insn).desc;
            for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
                values.add(0, pop());
            }
            if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
                values.add(0, pop());
            }
            if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                uses = interpreter.naryOperation(insn, values).getVariables();
            } else {
                push(interpreter.naryOperation(insn, values));
            }
            break;
        }
        case Opcodes.INVOKEDYNAMIC: {
            values = new ArrayList<Value>();
            final String desc = ((InvokeDynamicInsnNode) insn).desc;
            for (int i = Type.getArgumentTypes(desc).length; i > 0; --i) {
                values.add(0, pop());
            }
            if (Type.getReturnType(desc) == Type.VOID_TYPE) {
                uses = interpreter.naryOperation(insn, values).getVariables();
            } else {
                push(interpreter.naryOperation(insn, values));
            }
            break;
        }
        case Opcodes.NEW:
        case Opcodes.NEWARRAY:
        case Opcodes.ANEWARRAY:
        case Opcodes.ARRAYLENGTH:
            super.execute(insn, interpreter);
            break;
        case Opcodes.ATHROW:
            uses = pop().getVariables();
            break;
        case Opcodes.CHECKCAST:
        case Opcodes.INSTANCEOF:
            super.execute(insn, interpreter);
            break;
        case Opcodes.MONITORENTER:
        case Opcodes.MONITOREXIT:
            uses = pop().getVariables();
            break;
        case Opcodes.MULTIANEWARRAY:
            super.execute(insn, interpreter);
            break;
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
            uses = pop().getVariables();
            break;
        default:
            throw new IllegalStateException("Illegal opcode " + insn.getOpcode());
        }
    }

    public void addDef(final Variable var) {
        final Set<Variable> newDefs = new LinkedHashSet<Variable>();
        newDefs.addAll(defs);
        newDefs.add(var);
        defs = Collections.unmodifiableSet(newDefs);
    }

}
