package br.com.ooboo.asm.defuse.integration;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.DefUseInterpreter;
import br.com.ooboo.asm.defuse.Invoke;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseFrameExecutePOPInstruction {

	private DefUseInterpreter interpreter;

	private DefUseFrame frame;

	private Variable variable1;
	private Variable variable2;

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
		frame = new DefUseFrame(0, 2);
		variable1 = new Variable(Type.INT_TYPE);
		variable2 = new Variable(Type.INT_TYPE);
	}

	@Test
	public void POPRegularValue() {
		push(Value.class, 1);
		executePOP();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void POPInvokeValue() {
		push(Invoke.class, 1);
		executePOP();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void POP2RegularValue() {
		push(Value.class, 2);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void POP2InvokeValue() {
		push(Invoke.class, 2);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void POP2Case1() {
		push(Value.class, 1);
		push(Value.class, 1);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void POP2Case2() {
		push(Invoke.class, 1);
		push(Value.class, 1);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void POP2Case3() {
		push(Value.class, 1);
		push(Invoke.class, 1);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void POP2Case4() {
		push(Invoke.class, 1);
		push(Invoke.class, 1);
		executePOP2();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	// ----------------- //
	// Auxiliary methods //
	// ------------------//

	private void push(final Class<? extends Value> clazz, final int size) {

		final Value mock = mock(clazz);

		final Set<Variable> variables = new HashSet<Variable>();

		if (frame.getStackSize() == 0) {
			variables.add(variable1);
		}
		if (frame.getStackSize() == 1) {
			variables.add(variable2);
		}

		when(mock.getVariables()).thenReturn(variables);
		when(mock.getSize()).thenReturn(size);

		frame.push(mock);

	}

	private void executePOP() {
		try {
			frame.execute(new InsnNode(Opcodes.POP), interpreter);
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

	private void executePOP2() {
		try {
			frame.execute(new InsnNode(Opcodes.POP2), interpreter);
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

}
