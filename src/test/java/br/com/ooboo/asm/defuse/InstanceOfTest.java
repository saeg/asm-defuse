package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class InstanceOfTest {

	private Value value;

	private ValueHolder iof;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
		iof = ValueHolder.instanceOf(value);
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, iof.getSize());
	}

	@Test
	public void InstanceOfToString() {
		Assert.assertEquals("InstanceOf(value)", iof.toString());
	}

	@Test
	public void GetVariablesDelegateToReferenceValue() {
		Assert.assertThat(iof.getVariables(), sameInstance(value.getVariables()));
	}

}
