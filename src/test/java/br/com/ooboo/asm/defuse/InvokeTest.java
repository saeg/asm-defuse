package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class InvokeTest {

	private static final Type aType = Mockito.mock(Type.class);

	@Test
	public void InvokeToString() {
		final Variable var1 = Mockito.mock(Variable.class);
		final Variable var2 = Mockito.mock(Variable.class);
		final List<Variable> valueVars = Arrays.asList(var1, var2);
		final Invoke invoke = new Invoke(aType, valueVars);
		Mockito.when(var1.toString()).thenReturn("A");
		Mockito.when(var2.toString()).thenReturn("B");
		Assert.assertEquals("Invoke[A, B]", invoke.toString());
	}

	@Test
	public void VariableListContainsVariablesFromValues() {
		final Variable var1 = new Variable(aType);
		final Variable var2 = new Variable(aType);
		final List<Variable> valueVars = Arrays.asList(var1, var2);
		final Invoke invoke = new Invoke(aType, valueVars);
		final Set<Variable> vars = invoke.getVariables();
		Assert.assertEquals(var1.getVariables().size() + var2.getVariables().size(), vars.size());
		Assert.assertTrue(vars.containsAll(var1.getVariables()));
		Assert.assertTrue(vars.containsAll(var2.getVariables()));
		Assert.assertFalse(vars.isEmpty());
	}

	@Test
	public void SizeOfInvokeThatReturnIntIsOne() {
		final Invoke invoke = new Invoke(Type.INT_TYPE, null);
		Assert.assertEquals(1, invoke.getSize());
	}

	@Test
	public void SizeOfInvokeThatReturnLongIsTwo() {
		final Invoke invoke = new Invoke(Type.LONG_TYPE, null);
		Assert.assertEquals(2, invoke.getSize());
	}

	@Test
	public void SizeOfInvokeThatReturnVoidIsZero() {
		final Invoke invoke = new Invoke(Type.VOID_TYPE, null);
		Assert.assertEquals(0, invoke.getSize());
	}

}