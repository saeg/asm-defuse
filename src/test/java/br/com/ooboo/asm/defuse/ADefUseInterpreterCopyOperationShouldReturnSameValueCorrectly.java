package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnSameValueCorrectly {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ Opcodes.DUP,  },
			{ Opcodes.DUP_X1 },
			{ Opcodes.DUP_X2 },
			{ Opcodes.DUP2_X1 },
			{ Opcodes.DUP2_X2 },
			{ Opcodes.SWAP },
		});
	}
	
	private AbstractInsnNode insn;
	
	public ADefUseInterpreterCopyOperationShouldReturnSameValueCorrectly(int opcode) {
		insn = new InsnNode(opcode);
	}
	
	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnSameValueCorrectly() {
		Value value = new Value(null);
		DefUseInterpreter interpreter = new DefUseInterpreter();
		Value copy = interpreter.copyOperation(insn, value);
		Assert.assertThat(copy, sameInstance(value));
	}

}
