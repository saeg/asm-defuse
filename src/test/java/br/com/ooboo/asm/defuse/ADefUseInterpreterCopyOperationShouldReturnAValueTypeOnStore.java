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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnAValueTypeOnStore {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ Opcodes.ISTORE, Type.INT_TYPE, Value.INT_VALUE },
			{ Opcodes.LSTORE, Type.LONG_TYPE, Value.LONG_VALUE },
			{ Opcodes.FSTORE, Type.FLOAT_TYPE, Value.FLOAT_VALUE },
			{ Opcodes.DSTORE, Type.DOUBLE_TYPE, Value.DOUBLE_VALUE },
			{ Opcodes.ASTORE, Type.getType("Ljava/lang/Object;"), Value.REFERENCE_VALUE }
		});
	}

	private final AbstractInsnNode insn;

	private final Value value;

	private final Value expected;

	public ADefUseInterpreterCopyOperationShouldReturnAValueTypeOnStore(
			final int opcode, final Type type, final Value expected) {
		
		this.insn = new InsnNode(opcode);
		this.value = new Value(type);
		this.expected = expected;
	}

	@Test
	public void AssertThatDefUseInterpreterCopyOperationReturnNullCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final Value copy = interpreter.copyOperation(insn, value);
		Assert.assertThat(copy, sameInstance(expected));
	}

}
