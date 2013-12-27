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
public class ADefUseInterpreterBinaryOperationShouldReturnArrayValue {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.IALOAD, Type.INT_TYPE },
				{ Opcodes.LALOAD, Type.LONG_TYPE },
				{ Opcodes.FALOAD, Type.FLOAT_TYPE },
				{ Opcodes.DALOAD, Type.DOUBLE_TYPE },
				{ Opcodes.AALOAD, Type.getObjectType("java/lang/Object") },
				{ Opcodes.BALOAD, Type.BYTE_TYPE },
				{ Opcodes.CALOAD, Type.CHAR_TYPE },
				{ Opcodes.SALOAD, Type.SHORT_TYPE }
		});
	}

	private final InsnNode insn;

	private final Type expected;

	public ADefUseInterpreterBinaryOperationShouldReturnArrayValue(final int op, final Type expected) {
		this.insn = new InsnNode(op);
		this.expected = expected;
	}

	@Test
	public void AssertThatADefUseInterpreterBinaryOperationReturnsArrayValueCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final ArrayValue value = (ArrayValue) interpreter.binaryOperation(insn, null, null);
		Assert.assertEquals(expected, value.type);
	}

}
