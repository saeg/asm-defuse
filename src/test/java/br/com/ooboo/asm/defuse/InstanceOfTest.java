package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class InstanceOfTest {

	private Value value;

	private InstanceOf iof;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new ArrayList<Variable>(0));
		iof = new InstanceOf(value);
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, iof.getSize());
	}

	@Test
	public void ArrayLengthToString() {
		Assert.assertEquals("InstanceOf(value)", iof.toString());
	}

	@Test
	public void GetVariablesDelegateToReferenceValue() {
		Assert.assertThat(iof.getVariables(), sameInstance(value.getVariables()));
	}

}
