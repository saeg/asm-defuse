package br.com.ooboo.asm.defuse.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.DefUseInterpreter;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing {

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

	private DefUseInterpreter interpreter;

	private DefUseFrame frame;

	private Variable variable1;
	private Variable variable2;

	private final AbstractInsnNode insn;

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
		frame = new DefUseFrame(0, 2);
		variable1 = new Variable(Type.INT_TYPE);
		variable2 = new Variable(Type.INT_TYPE);
	}

	public DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing(final int op) {
		insn = new JumpInsnNode(op, null);
	}

	@Test
	public void test1() {
		push(null);
		push(null);
		execute();
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void test2() {
		push(variable1);
		push(null);
		execute();
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertFalse(frame.getUses().contains(variable2));
	}

	@Test
	public void test3() {
		push(null);
		push(variable2);
		execute();
		Assert.assertFalse(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void test4() {
		push(variable1);
		push(variable2);
		execute();
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	// ----------------- //
	// Auxiliary methods //
	// ------------------//

	private void push(final Variable use) {

		final Value mock = mock(Value.class);

		final Set<Variable> variables = new HashSet<Variable>();

		if (use != null) {
			variables.add(use);
		}

		when(mock.getVariables()).thenReturn(variables);

		frame.push(mock);

	}

	private void execute() {
		try {
			frame.execute(insn, interpreter);
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

}
