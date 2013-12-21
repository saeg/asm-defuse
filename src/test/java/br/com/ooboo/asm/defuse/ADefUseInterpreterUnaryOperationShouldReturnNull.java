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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterUnaryOperationShouldReturnNull {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.IFEQ },
				{ Opcodes.IFNE },
				{ Opcodes.IFLT },
				{ Opcodes.IFGE },
				{ Opcodes.IFGT },
				{ Opcodes.IFLE },
				{ Opcodes.TABLESWITCH },
				{ Opcodes.LOOKUPSWITCH },
				{ Opcodes.IRETURN },
				{ Opcodes.LRETURN },
				{ Opcodes.FRETURN },
				{ Opcodes.DRETURN },
				{ Opcodes.ARETURN },
				{ Opcodes.PUTSTATIC },
				{ Opcodes.ATHROW }
		});
	}

	private final AbstractInsnNode insn;

	public ADefUseInterpreterUnaryOperationShouldReturnNull(final int opcode) {
		if (opcode >= Opcodes.IFEQ && opcode <= Opcodes.IFLE) {
			insn = new JumpInsnNode(opcode, null);
		} else if (opcode == Opcodes.TABLESWITCH) {
			insn = new TableSwitchInsnNode(0, 0, null);
		} else if (opcode == Opcodes.LOOKUPSWITCH) {
			insn = new LookupSwitchInsnNode(null, null, null);
		} else if (opcode == Opcodes.PUTSTATIC) {
			insn = new FieldInsnNode(opcode, null, null, null);
		} else { // xRETURN
			insn = new InsnNode(opcode);
		}
	}

	@Test
	public void AssertThatADefUseInterpreterUnaryOperationReturnsNullCorrectly() {
		final Value ignore = null;
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		Assert.assertNull(interpreter.unaryOperation(insn, ignore));
	}

}
