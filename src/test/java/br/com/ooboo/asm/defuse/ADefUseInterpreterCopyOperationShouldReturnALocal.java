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
import org.objectweb.asm.tree.VarInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnALocal {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ Opcodes.ILOAD, 0, Type.INT_TYPE },
			{ Opcodes.LLOAD, 1, Type.LONG_TYPE },
			{ Opcodes.FLOAD, 2, Type.FLOAT_TYPE },
			{ Opcodes.DLOAD, 3, Type.DOUBLE_TYPE },
			{ Opcodes.ALOAD, 4, Type.getType(Object.class) }
		});
	}

	private final VarInsnNode insn;

	private final Value value;

	public ADefUseInterpreterCopyOperationShouldReturnALocal(final int op, final int v, final Type t) {
		insn = new VarInsnNode(op, v);
		value = new Value(t);
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnLocalCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final Local local = (Local) interpreter.copyOperation(insn, value);
		Assert.assertEquals(insn.var, local.var);
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnLocalWithCorrectSize() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final Local local = (Local) interpreter.copyOperation(insn, value);
		Assert.assertEquals(value.getSize(), local.getSize());
	}

}
