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
	public void SizeOfVoidIsZero() {
		final Value value = new Value(Type.VOID_TYPE);
		Assert.assertEquals(0, value.getSize());
	}

	@Test
	public void SizeOfUNINITIALIZED_VALUEIsOne() {
		Assert.assertEquals(1, Value.UNINITIALIZED_VALUE.getSize());
	}

	@Test
	public void SizeOfAnyOtherTypeIsOne() {
		Assert.assertEquals(1, new Value(Type.BOOLEAN_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.CHAR_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.BYTE_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.SHORT_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.INT_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.FLOAT_TYPE).getSize());
		Assert.assertEquals(1, new Value(Type.getObjectType("java/lang/String")).getSize());
		Assert.assertEquals(1, new Value(Type.getType("Ljava/lang/String;")).getSize());
		Assert.assertEquals(1, new Value(Type.getType("[I")).getSize());
	}

	@Test
	public void DefinedValuesHaveCorrectType() {
		Assert.assertEquals(null, Value.UNINITIALIZED_VALUE.type);
		Assert.assertEquals(Type.INT_TYPE, Value.INT_VALUE.type);
		Assert.assertEquals(Type.FLOAT_TYPE, Value.FLOAT_VALUE.type);
		Assert.assertEquals(Type.LONG_TYPE, Value.LONG_VALUE.type);
		Assert.assertEquals(Type.DOUBLE_TYPE, Value.DOUBLE_VALUE.type);
		Assert.assertEquals(Type.getObjectType("java/lang/Object"), Value.REFERENCE_VALUE.type);
	}

	@Test
	public void VariablesSetIsEmpty() {
		Assert.assertTrue(value.getVariables().isEmpty());
	}

	@Test
	public void VariablesSetIsUnmodifiable() {
		Assert.assertThat(value.getVariables(), sameInstance(Collections.EMPTY_SET));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsNull() {
		new Value(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsAMethodType() {
		new Value(Type.getMethodType("()V"));
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
	public void DifferentClassReturnsFalseOnEquals() {
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
	public void EqualsReturnSameHashCode() {
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

	@Test
	public void UNINITIALIZED_VALUEValueToString() {
		Assert.assertEquals(".", Value.UNINITIALIZED_VALUE.toString());
	}

	@Test
	public void REFERENCE_VALUEValueToString() {
		Assert.assertEquals("R", Value.REFERENCE_VALUE.toString());
	}

	@Test
	public void ValueToString() {
		final Type type = Mockito.mock(Type.class);
		final Value value = new Value(type);
		Mockito.when(type.getDescriptor()).thenReturn("Some Value");
		Assert.assertEquals("Some Value", value.toString());
		Mockito.verify(type, Mockito.times(1)).getDescriptor();
	}

}
