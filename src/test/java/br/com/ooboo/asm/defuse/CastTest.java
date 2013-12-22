package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class CastTest {

	private Value value;

	private Cast cast;

	@Before
	public void setUp() {
		value = Mockito.mock(Value.class);
		Mockito.when(value.toString()).thenReturn("value");
		Mockito.when(value.getVariables()).thenReturn(new ArrayList<Variable>(0));
		cast = new Cast(Type.INT_TYPE, value);
	}

	@Test
	public void GetVariablesDelegateToValue() {
		Assert.assertThat(cast.getVariables(), sameInstance(value.getVariables()));
	}

	@Test
	public void ToStringDelegateToValue() {
		final Cast cast = new Cast(Type.INT_TYPE, value);
		Assert.assertThat(cast.toString(), sameInstance(value.toString()));
	}

}
