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
import org.objectweb.asm.tree.MethodInsnNode;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecuteInvokeInstruction extends DefUseFrameExecuteAbstractTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.INVOKEVIRTUAL },
				{ Opcodes.INVOKESPECIAL },
				{ Opcodes.INVOKEINTERFACE }
		});
	}

	private Variable variable1;
	private Variable variable2;
	private Variable variable3;

	private final int op;

	public DefUseFrameExecuteInvokeInstruction(final int op) {
		super(new DefUseFrame(0, 3));
		this.op = op;
	}

	@Before
	public void setUp() {
		variable1 = Mockito.mock(Variable.class);
		variable2 = Mockito.mock(Variable.class);
		variable3 = Mockito.mock(Variable.class);
	}

	@Test
	public void NoArgsTest1() {
		pushValue();
		execute(invoke("()V"));
		assertDef();
		assertUses();
	}

	@Test
	public void NoArgsTest2() {
		pushValue().thatUseVariables(variable1);
		execute(invoke("()V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void OneArgsTest1() {
		pushValue();
		pushValue();
		execute(invoke("(I)V"));
		assertDef();
		assertUses();
	}

	@Test
	public void OneArgsTest2() {
		pushValue().thatUseVariables(variable1);
		pushValue();
		execute(invoke("(I)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void OneArgsTest3() {
		pushValue();
		pushValue().thatUseVariables(variable1);
		execute(invoke("(I)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void OneArgsTest4() {
		pushValue().thatUseVariables(variable1);
		pushValue().thatUseVariables(variable2);
		execute(invoke("(I)V"));
		assertDef();
		assertUses(variable1, variable2);
	}

	@Test
	public void TwoArgsTest1() {
		pushValue();
		pushValue();
		pushValue();
		execute(invoke("(II)V"));
		assertDef();
		assertUses();
	}

	@Test
	public void TwoArgsTest2() {
		pushValue().thatUseVariables(variable1);
		pushValue();
		pushValue();
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void TwoArgsTest3() {
		pushValue();
		pushValue().thatUseVariables(variable1);
		pushValue();
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void TwoArgsTest4() {
		pushValue();
		pushValue();
		pushValue().thatUseVariables(variable1);
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void TwoArgsTest5() {
		pushValue().thatUseVariables(variable1);
		pushValue().thatUseVariables(variable2);
		pushValue();
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1, variable2);
	}

	@Test
	public void TwoArgsTest6() {
		pushValue().thatUseVariables(variable1);
		pushValue();
		pushValue().thatUseVariables(variable2);
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1, variable2);
	}

	@Test
	public void TwoArgsTest7() {
		pushValue().thatUseVariables(variable1);
		pushValue().thatUseVariables(variable2);
		pushValue().thatUseVariables(variable3);
		execute(invoke("(II)V"));
		assertDef();
		assertUses(variable1, variable2, variable3);
	}

	@Test
	public void ReturnAValue() {
		pushValue().thatUseVariables(variable1);
		pushValue().thatUseVariables(variable2);
		pushValue().thatUseVariables(variable3);
		execute(invoke("(II)I"));
		assertDef();
		assertUses();
	}

	private AbstractInsnNode invoke(final String desc) {
		return new MethodInsnNode(op, "Owner", "name", desc);
	}

}
