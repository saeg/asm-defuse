package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ArrayLengthTest {

	private Value value;

	private ArrayLength length;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new ArrayList<Variable>(0));
		length = new ArrayLength(value);
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, length.getSize());
	}

	@Test
	public void ArrayLengthToString() {
		Assert.assertEquals("ArrayLength(value)", length.toString());
	}

	@Test
	public void GetVariablesDelegateToReferenceValue() {
		Assert.assertThat(length.getVariables(), sameInstance(value.getVariables()));
	}

}
