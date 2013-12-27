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
				{ Opcodes.DADD, Type.DOUBLE_TYPE }
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
