package br.com.ooboo.asm.defuse.integration;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing extends
		DefUseFrameExecuteAbstractTest {

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
				{ Opcodes.IF_ACMPNE }
		});
	}

	private Variable variable1;
	private Variable variable2;

	private final AbstractInsnNode insn;

	@Before
	public void setUp() {
		variable1 = Mockito.mock(Variable.class);
		variable2 = Mockito.mock(Variable.class);
	}

	public DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing(final int op) {
		super(new DefUseFrame(0, 2));
		insn = new JumpInsnNode(op, null);
	}

	@Test
	public void test1() {
		pushValue();
		pushValue();
		execute(insn);
		assertDef();
		assertUses();
	}

	@Test
	public void test2() {
		pushValue().thatUseVariables(variable1);
		pushValue();
		execute(insn);
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void test3() {
		pushValue();
		pushValue().thatUseVariables(variable2);
		execute(insn);
		assertDef();
		assertUses(variable2);
	}

	@Test
	public void test4() {
		pushValue().thatUseVariables(variable1);
		pushValue().thatUseVariables(variable2);
		execute(insn);
		assertDef();
		assertUses(variable1, variable2);
	}

}
