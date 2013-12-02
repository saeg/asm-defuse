package br.com.ooboo.asm.defuse;

import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

public class DefUseInterpreter extends Interpreter<Value> implements Opcodes {

	public DefUseInterpreter() {
		super(ASM4);
	}

	@Override
	public Value newValue(final Type type) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public Value newOperation(final AbstractInsnNode insn) {

		switch (insn.getOpcode()) {
		case ACONST_NULL:
		case ICONST_M1:
		case ICONST_0:
		case ICONST_1:
		case ICONST_2:
		case ICONST_3:
		case ICONST_4:
		case ICONST_5:
			return Constant.WORD;
		case LCONST_0:
		case LCONST_1:
			return Constant.DWORD;
		case FCONST_0:
		case FCONST_1:
		case FCONST_2:
			return Constant.WORD;
		case DCONST_0:
		case DCONST_1:
			return Constant.DWORD;
		case BIPUSH:
		case SIPUSH:
			return Constant.WORD;
		case LDC: {
			final Object cst = ((LdcInsnNode) insn).cst;
			if (cst instanceof Integer) {
				return Constant.WORD;
			} else if (cst instanceof Float) {
				return Constant.WORD;
			} else if (cst instanceof Long) {
				return Constant.DWORD;
			} else if (cst instanceof Double) {
				return Constant.DWORD;
			} else if (cst instanceof String) {
				return Constant.WORD;
			} else if (cst instanceof Type) {
				final int sort = ((Type) cst).getSort();
				if (sort == Type.OBJECT || sort == Type.ARRAY || sort == Type.METHOD) {
					return Constant.WORD;
				} else {
					throw new IllegalArgumentException("Illegal LDC constant " + cst);
				}
			} else if (cst instanceof Handle) {
				return Constant.WORD;
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
			return new ObjectRef(type.desc);
		}
		default:
			throw new IllegalArgumentException("Invalid instruction opcode.");
		}
	}

	@Override
	public Value copyOperation(final AbstractInsnNode insn, final Value value)
			throws AnalyzerException {
		return null;
	}

	@Override
	public Value unaryOperation(final AbstractInsnNode insn, final Value value)
			throws AnalyzerException {
		return null;
	}

	@Override
	public Value binaryOperation(final AbstractInsnNode insn, final Value value1, final Value value2)
			throws AnalyzerException {
		return null;
	}

	@Override
	public Value ternaryOperation(final AbstractInsnNode insn, final Value value1,
			final Value value2, final Value value3) throws AnalyzerException {
		return null;
	}

	@Override
	public Value naryOperation(final AbstractInsnNode insn, final List<? extends Value> values)
			throws AnalyzerException {
		return null;
	}

	@Override
	public void returnOperation(final AbstractInsnNode insn, final Value value, final Value expected)
			throws AnalyzerException {
	}

	@Override
	public Value merge(final Value v, final Value w) {
		return null;
	}

}
