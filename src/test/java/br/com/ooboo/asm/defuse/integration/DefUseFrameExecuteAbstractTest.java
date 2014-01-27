package br.com.ooboo.asm.defuse.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.mockito.Mockito;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.DefUseInterpreter;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseFrameExecuteAbstractTest {

	public final DefUseFrame frame;

	public DefUseFrameExecuteAbstractTest(final DefUseFrame frame) {
		this.frame = frame;
	}

	public ValuePushed pushValue() {
		final Value mock = Mockito.mock(Value.class);
		frame.push(mock);
		return new ValuePushed(mock);
	}

	public ValuePushed push(final Class<? extends Value> clazz, final int size) {
		final Value mock = Mockito.mock(clazz);
		Mockito.when(mock.getSize()).thenReturn(size);
		frame.push(mock);
		return new ValuePushed(mock);
	}

	public ValuePushed push(final Value value) {
		final Value mock = Mockito.spy(value);
		frame.push(mock);
		return new ValuePushed(mock);
	}

	public void execute(final AbstractInsnNode insn) {
		try {
			frame.execute(insn, new DefUseInterpreter());
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

	public void assertDef(final Variable... vars) {
		Assert.assertEquals(vars.length, frame.getDefinitions().size());
		for (final Variable variable : vars) {
			Assert.assertTrue(frame.getDefinitions().contains(variable));
		}
	}

	public void assertUses(final Variable... vars) {
		Assert.assertEquals(vars.length, frame.getUses().size());
		for (final Variable variable : vars) {
			Assert.assertTrue(frame.getUses().contains(variable));
		}
	}

	public class ValuePushed {

		private final Value value;

		public ValuePushed(final Value value) {
			this.value = value;
		}

		public ValuePushed thatUseVariables(final Variable... vars) {
			final Set<Variable> variables = new HashSet<Variable>();
			variables.addAll(Arrays.asList(vars));
			Mockito.when(value.getVariables()).thenReturn(variables);
			return this;
		}

		public Value get() {
			return value;
		}

	}

}
