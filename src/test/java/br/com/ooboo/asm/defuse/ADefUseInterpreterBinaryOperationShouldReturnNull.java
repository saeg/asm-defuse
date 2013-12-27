package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
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
				{ Opcodes.IF_ACMPNE },
				{ Opcodes.PUTFIELD }
		});
	}

	private final AbstractInsnNode insn;

	public ADefUseInterpreterBinaryOperationShouldReturnNull(final int opcode) {
		if (opcode == Opcodes.PUTFIELD) {
			insn = new FieldInsnNode(opcode, null, null, null);
		} else {
			insn = new JumpInsnNode(opcode, null);
		}
	}

	@Test
	public void AssertThatADefUseInterpreterBinaryOperationReturnsNullCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		Assert.assertNull(interpreter.binaryOperation(insn, null, null));
	}

}
