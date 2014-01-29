package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class CastTest {

	private Value value;

	private ValueHolder cast;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
		cast = new ValueHolder(Type.INT_TYPE, value, "Cast");
	}

	@Test
	public void GetVariablesDelegateToValue() {
		Assert.assertThat(cast.getVariables(), sameInstance(value.getVariables()));
	}

	@Test
	public void CastToString() {
		Assert.assertEquals("Cast(value)", cast.toString());
	}

}
