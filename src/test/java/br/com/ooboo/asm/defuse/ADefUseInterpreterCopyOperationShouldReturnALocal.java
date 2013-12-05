package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnALocal {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ Opcodes.ILOAD, Value.INT_VALUE },
			{ Opcodes.LLOAD, Value.LONG_VALUE },
			{ Opcodes.FLOAD, Value.FLOAT_VALUE },
			{ Opcodes.DLOAD, Value.DOUBLE_VALUE },
			{ Opcodes.ALOAD, Value.REFERENCE_VALUE }
		});
	}

	private final VarInsnNode insn;

	private final Value value;

	private DefUseInterpreter interpreter;

	public ADefUseInterpreterCopyOperationShouldReturnALocal(final int opcode, final Value value) {
		this.insn = new VarInsnNode(opcode, new Random().nextInt());
		this.value = value;
	}

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnLocalCorrectly() {
		final Local local = (Local) interpreter.copyOperation(insn, value);
		Assert.assertEquals(insn.var, local.var);
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnLocalWithCorrectSize() {
		final Local local = (Local) interpreter.copyOperation(insn, value);
		Assert.assertEquals(value.getSize(), local.getSize());
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnLocalWithCorrectType() {
		final Local local = (Local) interpreter.copyOperation(insn, value);
		Assert.assertThat(local.type, sameInstance(value.type));
	}

}
