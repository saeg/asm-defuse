package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ValueTest {

	private Value value;

	@Before
	public void setUp() {
		value = new Value(Type.INT_TYPE);
	}

	@Test
	public void SizeOfLongIsTwo() {
		final Value value = new Value(Type.LONG_TYPE);
		Assert.assertEquals(2, value.getSize());
	}

	@Test
	public void SizeOfDoubleIsTwo() {
		final Value value = new Value(Type.DOUBLE_TYPE);
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

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsNull() {
		new Value(null);
	}

	@Test
	public void EqualsASelfReturnTrue() {
		Assert.assertTrue(value.equals(value));
	}

	@Test
	public void EqualsANullReturnsFalse() {
		Assert.assertFalse(value.equals(null));
	}

	@Test
	public void OnEqualsDifferentClassReturnsFalse() {
		final Value other = Mockito.mock(Value.class);
		Assert.assertFalse(value.equals(other));
	}

	@Test
	public void EqualsReturnTrue() {
		final Value other = new Value(Type.INT_TYPE);
		Assert.assertTrue(value.equals(other));
	}

	@Test
	public void DifferentReturnFalseOnEquals() {
		final Value other = new Value(Type.LONG_TYPE);
		Assert.assertFalse(value.equals(other));
	}

	@Test
	public void EqualsReturnSameHash() {
		final Value other = new Value(Type.INT_TYPE);
		Assert.assertEquals(value.hashCode(), other.hashCode());
	}

	@Test
	public void DifferentReturnDifferentHashCode() {
		final Value other = new Value(Type.LONG_TYPE);
		Assert.assertNotEquals(value.hashCode(), other.hashCode());
	}

	@Test
	public void EqualsDoNotThrowAnExceptionWhenTypeIsNull() {
		Value.UNINITIALIZED_VALUE.equals(Value.INT_VALUE);
	}

	@Test
	public void HashCodeDoNotThrowAnExceptionWhenTypeIsNull() {
		Value.UNINITIALIZED_VALUE.hashCode();
	}

}
