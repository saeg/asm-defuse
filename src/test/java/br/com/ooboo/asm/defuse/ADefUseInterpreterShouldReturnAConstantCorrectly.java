package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterShouldReturnAConstantCorrectly {

	@Parameters
	public static Collection<Object[]> data() {
		Random rnd = new Random();
		return Arrays.asList(
			new Object[][] {
					// null
					{ Opcodes.ACONST_NULL, Value.REFERENCE_VALUE, null },
					// -1, 0, 1, 2, 3, 4, 5
					{ Opcodes.ICONST_M1, Value.INT_VALUE, null },
					{ Opcodes.ICONST_0, Value.INT_VALUE, null },
					{ Opcodes.ICONST_1, Value.INT_VALUE, null },
					{ Opcodes.ICONST_2, Value.INT_VALUE, null },
					{ Opcodes.ICONST_3, Value.INT_VALUE, null },
					{ Opcodes.ICONST_4, Value.INT_VALUE, null },
					{ Opcodes.ICONST_5, Value.INT_VALUE, null },
					// 0L, 1L
					{ Opcodes.LCONST_0, Value.LONG_VALUE, null },
					{ Opcodes.LCONST_1, Value.LONG_VALUE, null },
					// 0f, 1f, 2f
					{ Opcodes.FCONST_0, Value.FLOAT_VALUE, null },
					{ Opcodes.FCONST_1, Value.FLOAT_VALUE, null },
					{ Opcodes.FCONST_2, Value.FLOAT_VALUE, null },
					// 0d, 1d
					{ Opcodes.DCONST_0, Value.DOUBLE_VALUE, null },
					{ Opcodes.DCONST_1, Value.DOUBLE_VALUE, null },
					// bipush, sipush
					{ Opcodes.BIPUSH, Value.INT_VALUE, null },
					{ Opcodes.SIPUSH, Value.INT_VALUE, null },
					// ldc Integer and Float
					{ Opcodes.LDC, Value.INT_VALUE, rnd.nextInt() },
					{ Opcodes.LDC, Value.FLOAT_VALUE, rnd.nextFloat() },
					// ldc Long and Double
					{ Opcodes.LDC, Value.LONG_VALUE, rnd.nextLong() },
					{ Opcodes.LDC, Value.DOUBLE_VALUE, rnd.nextDouble() },
					// ldc String
					{ Opcodes.LDC, Value.REFERENCE_VALUE, "String" },
					// object type
					{ Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("Ljava.lang.Object;") },
					// array type
					{ Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("[I") },
					// method type
					{ Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("()I") },
					// method handle
					{ Opcodes.LDC, Value.REFERENCE_VALUE, new Handle(0, "", "", "") }
					
			}
		);
	}

	private AbstractInsnNode insn;

	private Value expected;

	public ADefUseInterpreterShouldReturnAConstantCorrectly(int opcode, Value v, Object arg) {
		if (opcode >= Opcodes.ACONST_NULL && opcode <= Opcodes.DCONST_1) {
			insn = new InsnNode(opcode);
		} else if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
			insn = new IntInsnNode(opcode, opcode);
		} else { // if (opcode == Opcodes.LDC)
			insn = new LdcInsnNode(arg);
		}
		expected = v;
	}

	@Test
	public void AssertThatNewOperationReturnsAConstantCorrectly() {
		final DefUseInterpreter interpreter = new DefUseInterpreter();
		final Value op = interpreter.newOperation(insn);
		Assert.assertThat(op, sameInstance(expected));
	}

}
