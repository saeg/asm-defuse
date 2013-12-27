package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterNaryOperationShouldReturnInvoke {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
				{ Opcodes.INVOKEVIRTUAL }, 
				{ Opcodes.INVOKESPECIAL },
				{ Opcodes.INVOKESTATIC }, 
				{ Opcodes.INVOKEINTERFACE }, 
				{ Opcodes.INVOKEDYNAMIC } 
		});
	}

	private AbstractInsnNode insn;

	public ADefUseInterpreterNaryOperationShouldReturnInvoke(final int opcode) {
		if (opcode == Opcodes.INVOKEDYNAMIC) {
			insn = new InvokeDynamicInsnNode(null, "()V", null);
		} else {
			insn = new MethodInsnNode(opcode, null, null, "()V");
		}
	}

	@Test
	public void AssertThatADefUseInterpreterNaryOperationReturnsInvokeValueCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final List<Value> values = new ArrayList<Value>();
		final Invoke invoke = (Invoke) interpreter.naryOperation(insn, values);
		Assert.assertTrue(invoke.values == values);
	}

}
