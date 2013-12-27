package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterBinaryOperationShouldReturnBinaryValue {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.IADD, Type.INT_TYPE },
				{ Opcodes.LADD, Type.LONG_TYPE },
				{ Opcodes.FADD, Type.FLOAT_TYPE },
				{ Opcodes.DADD, Type.DOUBLE_TYPE },
				{ Opcodes.ISUB, Type.INT_TYPE },
				{ Opcodes.LSUB, Type.LONG_TYPE },
				{ Opcodes.FSUB, Type.FLOAT_TYPE },
				{ Opcodes.DSUB, Type.DOUBLE_TYPE },
				{ Opcodes.IMUL, Type.INT_TYPE },
				{ Opcodes.LMUL, Type.LONG_TYPE },
				{ Opcodes.FMUL, Type.FLOAT_TYPE },
				{ Opcodes.DMUL, Type.DOUBLE_TYPE },
				{ Opcodes.IDIV, Type.INT_TYPE },
				{ Opcodes.LDIV, Type.LONG_TYPE },
				{ Opcodes.FDIV, Type.FLOAT_TYPE },
				{ Opcodes.DDIV, Type.DOUBLE_TYPE },
				{ Opcodes.IREM, Type.INT_TYPE },
				{ Opcodes.LREM, Type.LONG_TYPE },
				{ Opcodes.FREM, Type.FLOAT_TYPE },
				{ Opcodes.DREM, Type.DOUBLE_TYPE },
				{ Opcodes.ISHL, Type.INT_TYPE },
				{ Opcodes.LSHL, Type.LONG_TYPE },
				{ Opcodes.ISHR, Type.INT_TYPE },
				{ Opcodes.LSHR, Type.LONG_TYPE },
				{ Opcodes.IUSHR, Type.INT_TYPE },
				{ Opcodes.LUSHR, Type.LONG_TYPE },
				{ Opcodes.IAND, Type.INT_TYPE },
				{ Opcodes.LAND, Type.LONG_TYPE },
				{ Opcodes.IOR, Type.INT_TYPE },
				{ Opcodes.LOR, Type.LONG_TYPE },
				{ Opcodes.IXOR, Type.INT_TYPE },
				{ Opcodes.LXOR, Type.LONG_TYPE },
				{ Opcodes.LCMP, Type.INT_TYPE },
				{ Opcodes.FCMPL, Type.INT_TYPE },
				{ Opcodes.FCMPG, Type.INT_TYPE },
				{ Opcodes.DCMPL, Type.INT_TYPE },
				{ Opcodes.DCMPG, Type.INT_TYPE }
		});
	}

	private final InsnNode insn;

	private final Type expected;

	public ADefUseInterpreterBinaryOperationShouldReturnBinaryValue(final int op, final Type expected) {
		this.insn = new InsnNode(op);
		this.expected = expected;
	}

	@Test
	public void AssertThatADefUseInterpreterBinaryOperationReturnsBinaryValueCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final Binary value = (Binary) interpreter.binaryOperation(insn, null, null);
		Assert.assertEquals(expected, value.type);
	}

}
