package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ValueTest {

	private Value value;

	@Before
	public void setUp() {
		value = new Value(Type.INT_TYPE){};
	}

	@Test
	public void SizeOfLongIsTwo() {
		final Value value = new Value(Type.LONG_TYPE){};
		Assert.assertEquals(2, value.getSize());
	}

	@Test
	public void SizeOfDoubleIsTwo() {
		final Value value = new Value(Type.DOUBLE_TYPE){};
		Assert.assertEquals(2, value.getSize());
	}

	@Test
	public void SizeOfAnyOtherTypeIsOne() {
		Assert.assertEquals(1, value.getSize());
	}

	@Test
	public void VariableListIsEmpty() {
		Assert.assertTrue(value.getVariables().isEmpty());
	}

	@Test
	public void VariablesListIsUnmodifiable() {
		Assert.assertThat(value.getVariables(), sameInstance(Collections.EMPTY_LIST));
	}

}
