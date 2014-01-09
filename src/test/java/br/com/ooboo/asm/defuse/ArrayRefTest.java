package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ArrayRefTest {

	private Value value;

	private ArrayRef ref;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
		ref = new ArrayRef(Type.getType("[I"), value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsInvalid() {
		new ArrayRef(Type.getType("I"), value);
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, ref.getSize());
	}

	@Test
	public void ArrayRefToString() {
		Assert.assertEquals("ArrayRef[value]", ref.toString());
	}

	@Test
	public void VariableListContainsVariablesFromCounts() {
		final Type aType = Mockito.mock(Type.class);
		final Variable var1 = new Variable(aType);
		final Variable var2 = new Variable(aType);
		final List<Variable> valueVars = Arrays.asList(var1, var2);
		final ArrayRef arref = new ArrayRef(Type.getType("[I"), valueVars);
		final Set<Variable> vars = arref.getVariables();
		Assert.assertEquals(var1.getVariables().size() + var2.getVariables().size(), vars.size());
		Assert.assertTrue(vars.containsAll(var1.getVariables()));
		Assert.assertTrue(vars.containsAll(var2.getVariables()));
		Assert.assertFalse(vars.isEmpty());
	}

}
