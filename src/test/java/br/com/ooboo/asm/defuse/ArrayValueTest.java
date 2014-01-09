package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ArrayValueTest {

	private static final Type type = Mockito.mock(Type.class);
	private static final Set<Variable> value1Vars = Collections.singleton(new Variable(type));
	private static final Set<Variable> value2Vars = Collections.singleton(new Variable(type));

	private ArrayValue value;

	@Before
	public void setUp() {
		final Value value1 = Mockito.mock(Value.class);
		final Value value2 = Mockito.mock(Value.class);
		Mockito.when(value1.toString()).thenReturn("A");
		Mockito.when(value2.toString()).thenReturn("B");
		Mockito.when(value1.getVariables()).thenReturn(value1Vars);
		Mockito.when(value2.getVariables()).thenReturn(value2Vars);
		value = new ArrayValue(type, value1, value2);
	}

	@Test
	public void ArrayRefToString() {
		Assert.assertEquals("A[B]", value.toString());
	}

	@Test
	public void VariableListContainsVariablesFromReferenceAndIndex() {
		final Set<Variable> vars = value.getVariables();
		Assert.assertEquals(value1Vars.size() + value2Vars.size(), vars.size());
		Assert.assertTrue(vars.containsAll(value1Vars));
		Assert.assertTrue(vars.containsAll(value2Vars));
	}

}
