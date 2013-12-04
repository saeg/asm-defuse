package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ADefUseInterpreterNewValueShould {

	private DefUseInterpreter interpreter;

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
	}

	@Test
	public void ReturnUninitializedValueWhenTypeIsNull() {
		final Value v = interpreter.newValue(null);
		Assert.assertThat(v, sameInstance(Value.UNINITIALIZED_VALUE));
	}

	@Test
	public void ReturnNullWhenTypeIsVoid() {
		final Value v = interpreter.newValue(Type.VOID_TYPE);
		Assert.assertNull(v);
	}

	@Test
	public void ReturnIntWhenTypeIsBoolean() {
		final Value v = interpreter.newValue(Type.BOOLEAN_TYPE);
		Assert.assertThat(v, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void ReturnIntWhenTypeIsChar() {
		final Value v = interpreter.newValue(Type.CHAR_TYPE);
		Assert.assertThat(v, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void ReturnIntWhenTypeIsByte() {
		final Value v = interpreter.newValue(Type.BYTE_TYPE);
		Assert.assertThat(v, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void ReturnIntWhenTypeIsShort() {
		final Value v = interpreter.newValue(Type.SHORT_TYPE);
		Assert.assertThat(v, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void ReturnIntWhenTypeIsInt() {
		final Value v = interpreter.newValue(Type.INT_TYPE);
		Assert.assertThat(v, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void ReturnFloatWhenTypeIsFloat() {
		final Value v = interpreter.newValue(Type.FLOAT_TYPE);
		Assert.assertThat(v, sameInstance(Value.FLOAT_VALUE));
	}

	@Test
	public void ReturnLongWhenTypeIsLong() {
		final Value v = interpreter.newValue(Type.LONG_TYPE);
		Assert.assertThat(v, sameInstance(Value.LONG_VALUE));
	}

	@Test
	public void ReturnDoubleWhenTypeIsDouble() {
		final Value v = interpreter.newValue(Type.DOUBLE_TYPE);
		Assert.assertThat(v, sameInstance(Value.DOUBLE_VALUE));
	}

	@Test
	public void ReturnReferenceWhenTypeIsArray() {
		final Value v = interpreter.newValue(Type.getType("[I"));
		Assert.assertThat(v, sameInstance(Value.REFERENCE_VALUE));
	}

	@Test
	public void ReturnReferenceWhenTypeIsObject() {
		final Value v = interpreter.newValue(Type.getType("Ljava.lang.String;"));
		Assert.assertThat(v, sameInstance(Value.REFERENCE_VALUE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsInvalid() {
		interpreter.newValue(Type.getType("(I)V"));
	}

}
