package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;

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
		Mockito.when(value.getVariables()).thenReturn(new ArrayList<Variable>(0));
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
	public void GetVariablesDelegateToCountValue() {
		Assert.assertThat(ref.getVariables(), sameInstance(value.getVariables()));
	}

}
