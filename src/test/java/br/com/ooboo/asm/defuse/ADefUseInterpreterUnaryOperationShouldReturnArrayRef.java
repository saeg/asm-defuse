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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterUnaryOperationShouldReturnArrayRef {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.NEWARRAY, Opcodes.T_BOOLEAN, Type.getType("[Z") },
				{ Opcodes.NEWARRAY, Opcodes.T_CHAR, Type.getType("[C") },
				{ Opcodes.NEWARRAY, Opcodes.T_BYTE, Type.getType("[B") },
				{ Opcodes.NEWARRAY, Opcodes.T_SHORT, Type.getType("[S") },
				{ Opcodes.NEWARRAY, Opcodes.T_INT, Type.getType("[I") },
				{ Opcodes.NEWARRAY, Opcodes.T_FLOAT, Type.getType("[F") },
				{ Opcodes.NEWARRAY, Opcodes.T_DOUBLE, Type.getType("[D") },
				{ Opcodes.NEWARRAY, Opcodes.T_LONG, Type.getType("[J") },
				{ Opcodes.ANEWARRAY, "java/lang/String", Type.getType("[Ljava/lang/String;") }
		});
	}

	private final AbstractInsnNode insn;

	private final Type expected;

	public ADefUseInterpreterUnaryOperationShouldReturnArrayRef(
			final int opcode, final Object type, final Type expected) {

		if (opcode == Opcodes.NEWARRAY) {
			insn = new IntInsnNode(opcode, (Integer) type);
		} else {
			insn = new TypeInsnNode(opcode, (String) type);
		}
		this.expected = expected;
	}

	@Test
	public void AssertThatADefUseInterpreterUnaryOperationReturnArrayRef() {
		final Value ignore = null;
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final ArrayRef ref = (ArrayRef) interpreter.unaryOperation(insn, ignore);
		Assert.assertEquals(expected, ref.type);
	}

}
