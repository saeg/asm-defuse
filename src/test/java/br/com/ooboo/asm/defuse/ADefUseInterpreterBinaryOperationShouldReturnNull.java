package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterBinaryOperationShouldReturnNull {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.IF_ICMPEQ },
				{ Opcodes.IF_ICMPNE },
				{ Opcodes.IF_ICMPLT },
				{ Opcodes.IF_ICMPGE },
				{ Opcodes.IF_ICMPGT },
				{ Opcodes.IF_ICMPLE },
				{ Opcodes.IF_ACMPEQ },
				{ Opcodes.IF_ACMPNE }
		});
	}

	private final JumpInsnNode insn;

	public ADefUseInterpreterBinaryOperationShouldReturnNull(final int opcode) {
		insn = new JumpInsnNode(opcode, null);
	}

	@Test
	public void AssertThatADefUseInterpreterBinaryOperationReturnsNullCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		Assert.assertNull(interpreter.binaryOperation(insn, null, null));
	}

}
