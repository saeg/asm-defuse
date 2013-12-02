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
					{ Opcodes.ACONST_NULL, Constant.WORD, null },
					// -1, 0, 1, 2, 3, 4, 5
					{ Opcodes.ICONST_M1, Constant.WORD, null },
					{ Opcodes.ICONST_0, Constant.WORD, null },
					{ Opcodes.ICONST_1, Constant.WORD, null },
					{ Opcodes.ICONST_2, Constant.WORD, null },
					{ Opcodes.ICONST_3, Constant.WORD, null },
					{ Opcodes.ICONST_4, Constant.WORD, null },
					{ Opcodes.ICONST_5, Constant.WORD, null },
					// 0L, 1L
					{ Opcodes.LCONST_0, Constant.DWORD, null },
					{ Opcodes.LCONST_1, Constant.DWORD, null },
					// 0f, 1f, 2f
					{ Opcodes.FCONST_0, Constant.WORD, null },
					{ Opcodes.FCONST_1, Constant.WORD, null },
					{ Opcodes.FCONST_2, Constant.WORD, null },
					// 0d, 1d
					{ Opcodes.DCONST_0, Constant.DWORD, null },
					{ Opcodes.DCONST_1, Constant.DWORD, null },
					// bipush, sipush
					{ Opcodes.BIPUSH, Constant.WORD, null },
					{ Opcodes.SIPUSH, Constant.WORD, null },
					// ldc Integer and Float
					{ Opcodes.LDC, Constant.WORD, rnd.nextInt() },
					{ Opcodes.LDC, Constant.WORD, rnd.nextFloat() },
					// ldc Long and Double
					{ Opcodes.LDC, Constant.DWORD, rnd.nextLong() },
					{ Opcodes.LDC, Constant.DWORD, rnd.nextDouble() },
					// ldc String
					{ Opcodes.LDC, Constant.WORD, "String" },
					// object type
					{ Opcodes.LDC, Constant.WORD, Type.getType("Ljava.lang.Object;") },
					// array type
					{ Opcodes.LDC, Constant.WORD, Type.getType("[I") },
					// method type
					{ Opcodes.LDC, Constant.WORD, Type.getType("()I") },
					// method handle
					{ Opcodes.LDC, Constant.WORD, new Handle(0, "", "", "") }
					
			}
		);
	}
	
	private AbstractInsnNode insn;
	private Value expected;
	
	public ADefUseInterpreterShouldReturnAConstantCorrectly(int opcode, Constant c, Object arg) {
		if (opcode >= Opcodes.ACONST_NULL && opcode <= Opcodes.DCONST_1) {
			insn = new InsnNode(opcode);
		} else if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
			insn = new IntInsnNode(opcode, opcode);
		} else { // if (opcode == Opcodes.LDC)
			insn = new LdcInsnNode(arg);
		}
		expected = c;
	}
	
	@Test
	public void AssertThatNewOperationReturnsAConstantCorrectly() {
		DefUseInterpreter interpreter = new DefUseInterpreter();
		Value op = interpreter.newOperation(insn);
		Assert.assertThat(op, sameInstance(expected));
	}

}
