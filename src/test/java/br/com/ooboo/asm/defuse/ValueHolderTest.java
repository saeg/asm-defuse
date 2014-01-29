package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ValueHolderTest {

	private Value value;

	private ValueHolder vh;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
		vh = new ValueHolder(Type.INT_TYPE, value);
	}

	@Test
	public void GetVariablesDelegateToValue() {
		Assert.assertThat(vh.getVariables(), sameInstance(value.getVariables()));
	}

	@Test
	public void ToStringDelegateToValue() {
		Assert.assertEquals(value.toString(), vh.toString());
	}

}
