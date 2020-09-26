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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Interpreter;

public class DefUseInterpreter extends Interpreter<Value> implements Opcodes {

    public DefUseInterpreter() {
        super(ASM9);
    }

    @Override
    public Value newValue(final Type type) {
        if (type == null) {
            return Value.UNINITIALIZED_VALUE;
        }
        switch (type.getSort()) {
        case Type.VOID:
            return null;
        case Type.BOOLEAN:
        case Type.CHAR:
        case Type.BYTE:
        case Type.SHORT:
        case Type.INT:
            return Value.INT_VALUE;
        case Type.FLOAT:
            return Value.FLOAT_VALUE;
        case Type.LONG:
            return Value.LONG_VALUE;
        case Type.DOUBLE:
            return Value.DOUBLE_VALUE;
        case Type.ARRAY:
        case Type.OBJECT:
            return Value.REFERENCE_VALUE;
        default:
            throw new IllegalArgumentException("Illegal type" + type);
        }
    }

    @Override
    public Value newOperation(final AbstractInsnNode insn) {
        switch (insn.getOpcode()) {
        case ACONST_NULL:
            return Value.REFERENCE_VALUE;
        case ICONST_M1:
        case ICONST_0:
        case ICONST_1:
        case ICONST_2:
        case ICONST_3:
        case ICONST_4:
        case ICONST_5:
            return Value.INT_VALUE;
        case LCONST_0:
        case LCONST_1:
            return Value.LONG_VALUE;
        case FCONST_0:
        case FCONST_1:
        case FCONST_2:
            return Value.FLOAT_VALUE;
        case DCONST_0:
        case DCONST_1:
            return Value.DOUBLE_VALUE;
        case BIPUSH:
        case SIPUSH:
            return Value.INT_VALUE;
        case LDC: {
            final Object cst = ((LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
                return Value.INT_VALUE;
            } else if (cst instanceof Float) {
                return Value.FLOAT_VALUE;
            } else if (cst instanceof Long) {
                return Value.LONG_VALUE;
            } else if (cst instanceof Double) {
                return Value.DOUBLE_VALUE;
            } else if (cst instanceof String) {
                return Value.REFERENCE_VALUE;
            } else if (cst instanceof Type) {
                final int sort = ((Type) cst).getSort();
                if (sort == Type.OBJECT || sort == Type.ARRAY || sort == Type.METHOD) {
                    return Value.REFERENCE_VALUE;
                } else {
                    throw new IllegalArgumentException("Illegal LDC constant " + cst);
                }
            } else if (cst instanceof Handle) {
                return Value.REFERENCE_VALUE;
            } else if (cst instanceof ConstantDynamic) {
                final String desc = ((ConstantDynamic) cst).getDescriptor();
                return newValue(Type.getType(desc));
            } else {
                throw new IllegalArgumentException("Illegal LDC constant " + cst);
            }
        }
        case JSR:
            throw new UnsupportedOperationException(
                    "Do not support instruction types JSR - Deprecated in Java 6");
        case GETSTATIC: {
            final FieldInsnNode f = (FieldInsnNode) insn;
            return new StaticField(f.owner, f.name, f.desc);
        }
        case NEW: {
            final TypeInsnNode type = (TypeInsnNode) insn;
            return new ObjectRef(Type.getObjectType(type.desc));
        }
        default:
            throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    @Override
    public Value copyOperation(final AbstractInsnNode insn, final Value value) {
        switch (insn.getOpcode()) {
        case ILOAD:
        case LLOAD:
        case FLOAD:
        case DLOAD:
        case ALOAD: {
            final VarInsnNode v = (VarInsnNode) insn;
            return new Local(value.type, v.var);
        }
        case ISTORE:
        case LSTORE:
        case FSTORE:
        case DSTORE:
        case ASTORE:
            return newValue(value.type).with(insn);
        case DUP:
        case DUP_X1:
        case DUP_X2:
        case DUP2:
        case DUP2_X1:
        case DUP2_X2:
        case SWAP:
            return value;
        default:
            throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    @Override
    public Value unaryOperation(final AbstractInsnNode insn, final Value value) {
        switch (insn.getOpcode()) {
        case INEG:
        case LNEG:
        case FNEG:
        case DNEG:
            return value;
        case IINC:
            return Value.INT_VALUE.with(insn);
        case I2L:
            return new ValueHolder(Type.LONG_TYPE, value);
        case I2F:
            return new ValueHolder(Type.FLOAT_TYPE, value);
        case I2D:
            return new ValueHolder(Type.DOUBLE_TYPE, value);
        case L2I:
            return new ValueHolder(Type.INT_TYPE, value);
        case L2F:
            return new ValueHolder(Type.FLOAT_TYPE, value);
        case L2D:
            return new ValueHolder(Type.DOUBLE_TYPE, value);
        case F2I:
            return new ValueHolder(Type.INT_TYPE, value);
        case F2L:
            return new ValueHolder(Type.LONG_TYPE, value);
        case F2D:
            return new ValueHolder(Type.DOUBLE_TYPE, value);
        case D2I:
            return new ValueHolder(Type.INT_TYPE, value);
        case D2L:
            return new ValueHolder(Type.LONG_TYPE, value);
        case D2F:
            return new ValueHolder(Type.FLOAT_TYPE, value);
        case I2B:
            return new ValueHolder(Type.BYTE_TYPE, value);
        case I2C:
            return new ValueHolder(Type.CHAR_TYPE, value);
        case I2S:
            return new ValueHolder(Type.SHORT_TYPE, value);
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
        case TABLESWITCH:
        case LOOKUPSWITCH:
        case IRETURN:
        case LRETURN:
        case FRETURN:
        case DRETURN:
        case ARETURN:
        case PUTSTATIC:
            return null;
        case GETFIELD: {
            final FieldInsnNode f = (FieldInsnNode) insn;
            return new ObjectField(f.owner, f.name, f.desc, value);
        }
        case NEWARRAY: {
            final IntInsnNode iinsn = (IntInsnNode) insn;
            switch (iinsn.operand) {
            case T_BOOLEAN:
                return new ArrayRef(Type.getType("[Z"), value);
            case T_CHAR:
                return new ArrayRef(Type.getType("[C"), value);
            case T_BYTE:
                return new ArrayRef(Type.getType("[B"), value);
            case T_SHORT:
                return new ArrayRef(Type.getType("[S"), value);
            case T_INT:
                return new ArrayRef(Type.getType("[I"), value);
            case T_FLOAT:
                return new ArrayRef(Type.getType("[F"), value);
            case T_DOUBLE:
                return new ArrayRef(Type.getType("[D"), value);
            case T_LONG:
                return new ArrayRef(Type.getType("[J"), value);
            default:
                throw new IllegalArgumentException("Invalid array type");
            }
        }
        case ANEWARRAY: {
            final TypeInsnNode tinsn = (TypeInsnNode) insn;
            return new ArrayRef(Type.getType("[" + Type.getObjectType(tinsn.desc)), value);
        }
        case ARRAYLENGTH:
            return new ValueHolder(Type.INT_TYPE, value);
        case ATHROW:
            return null;
        case CHECKCAST: {
            final TypeInsnNode tinsn = (TypeInsnNode) insn;
            return new ValueHolder(Type.getObjectType(tinsn.desc), value);
        }
        case INSTANCEOF:
            return new ValueHolder(Type.INT_TYPE, value);
        case MONITORENTER:
        case MONITOREXIT:
        case IFNULL:
        case IFNONNULL:
            return null;
        default:
            throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    @Override
    public Value binaryOperation(final AbstractInsnNode insn, final Value value1, final Value value2) {
        // no problem not maintain order. javac is generating lookupswitch
        switch (insn.getOpcode()) {
        case IALOAD:
            return new ArrayValue(Type.INT_TYPE, value1, value2);
        case LALOAD:
            return new ArrayValue(Type.LONG_TYPE, value1, value2);
        case FALOAD:
            return new ArrayValue(Type.FLOAT_TYPE, value1, value2);
        case DALOAD:
            return new ArrayValue(Type.DOUBLE_TYPE, value1, value2);
        case AALOAD:
            return new ArrayValue(Type.getObjectType("java/lang/Object"), value1, value2);
        case BALOAD:
            return new ArrayValue(Type.BYTE_TYPE, value1, value2);
        case CALOAD:
            return new ArrayValue(Type.CHAR_TYPE, value1, value2);
        case SALOAD:
            return new ArrayValue(Type.SHORT_TYPE, value1, value2);
        case IADD:
        case ISUB:
        case IMUL:
        case IDIV:
        case IREM:
        case ISHL:
        case ISHR:
        case IUSHR:
        case IAND:
        case IOR:
        case IXOR:
        case LCMP:
        case FCMPL:
        case FCMPG:
        case DCMPL:
        case DCMPG:
            return new Merge(Type.INT_TYPE, value1, value2);
        case LADD:
        case LSUB:
        case LMUL:
        case LDIV:
        case LREM:
        case LSHL:
        case LSHR:
        case LUSHR:
        case LAND:
        case LOR:
        case LXOR:
            return new Merge(Type.LONG_TYPE, value1, value2);
        case FADD:
        case FSUB:
        case FMUL:
        case FDIV:
        case FREM:
            return new Merge(Type.FLOAT_TYPE, value1, value2);
        case DADD:
        case DSUB:
        case DMUL:
        case DDIV:
        case DREM:
            return new Merge(Type.DOUBLE_TYPE, value1, value2);
        case IF_ICMPEQ:
        case IF_ICMPNE:
        case IF_ICMPLT:
        case IF_ICMPGE:
        case IF_ICMPGT:
        case IF_ICMPLE:
        case IF_ACMPEQ:
        case IF_ACMPNE:
        case PUTFIELD:
            return null;
        default:
            throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    @Override
    public Value ternaryOperation(final AbstractInsnNode insn, final Value value1,
            final Value value2, final Value value3) {
        return null;
    }

    @Override
    public Value naryOperation(final AbstractInsnNode insn, final List<? extends Value> values) {
        switch (insn.getOpcode()) {
        case INVOKEVIRTUAL:
        case INVOKESPECIAL:
        case INVOKESTATIC:
        case INVOKEINTERFACE: {
            final MethodInsnNode invoke = (MethodInsnNode) insn;
            return new Invoke(Type.getReturnType(invoke.desc), values);
        }
        case INVOKEDYNAMIC: {
            final InvokeDynamicInsnNode invoke = (InvokeDynamicInsnNode) insn;
            return new Invoke(Type.getReturnType(invoke.desc), values);
        }
        case MULTIANEWARRAY: {
            final MultiANewArrayInsnNode arr = (MultiANewArrayInsnNode) insn;
            return new ArrayRef(Type.getType(arr.desc), values);
        }
        default:
            throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    @Override
    public void returnOperation(final AbstractInsnNode insn, final Value value, final Value expected) {
    }

    @Override
    public Value merge(final Value v, final Value w) {
        if (v.getVariables().containsAll(w.getVariables()) && v.insns.containsAll(w.insns)) {
            return v;
        }
        if (w.getVariables().containsAll(v.getVariables()) && w.insns.containsAll(v.insns)) {
            return w;
        }
        final Set<AbstractInsnNode> insns = new HashSet<AbstractInsnNode>();
        insns.addAll(v.insns);
        insns.addAll(w.insns);
        return new Merge(v.type, v, w, Collections.unmodifiableSet(insns));
    }

}
