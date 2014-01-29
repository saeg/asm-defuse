package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ArrayLengthTest {

	private Value value;

	private ValueHolder length;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
		length = new ValueHolder(Type.INT_TYPE, value, "Length");
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, length.getSize());
	}

	@Test
	public void ArrayLengthToString() {
		Assert.assertEquals("Length(value)", length.toString());
	}

	@Test
	public void GetVariablesDelegateToReferenceValue() {
		Assert.assertThat(length.getVariables(), sameInstance(value.getVariables()));
	}

}
