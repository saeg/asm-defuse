package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ArrayRefTest {

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenTypeIsInvalid() {
		final Value ignore = null;
		new ArrayRef(Type.getType("I"), ignore);
	}

	@Test
	public void SizeIsOne() {
		final Value ignore = null;
		final ArrayRef ref = new ArrayRef(Type.getType("[I"), ignore);
		Assert.assertEquals(1, ref.getSize());
	}

	@Test
	public void ArrayRefToString() {
		final Value v = new Value(Type.INT_TYPE) {
			@Override
			public String toString() {
				return "value";
			};
		};
		final ArrayRef ref = new ArrayRef(Type.getType("[I"), v);
		Assert.assertEquals("ArrayRef[value]", ref.toString());
	}

	@Test
	public void GetVariablesDelegateToCountValue() {
		final Value value = new Value(Type.INT_TYPE) {
			final List<Variable> VAR = new ArrayList<Variable>(0);

			@Override
			public List<Variable> getVariables() {
				return VAR;
			};
		};
		final ArrayRef ref = new ArrayRef(Type.getType("[I"), value);
		Assert.assertThat(ref.getVariables(), sameInstance(value.getVariables()));
	}

}
