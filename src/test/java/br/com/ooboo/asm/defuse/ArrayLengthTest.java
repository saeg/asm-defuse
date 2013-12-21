package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ArrayLengthTest {

	@Test
	public void SizeIsOne() {
		final Value ignore = null;
		final ArrayLength length = new ArrayLength(ignore);
		Assert.assertEquals(1, length.getSize());
	}

	@Test
	public void ArrayLengthToString() {
		final Value v = new Value(Type.INT_TYPE) {
			@Override
			public String toString() {
				return "value";
			};
		};
		final ArrayLength length = new ArrayLength(v);
		Assert.assertEquals("ArrayLength(value)", length.toString());
	}

	@Test
	public void GetVariablesDelegateToReferenceValue() {
		final Value value = new Value(Type.INT_TYPE) {
			final List<Variable> VAR = new ArrayList<Variable>(0);

			@Override
			public List<Variable> getVariables() {
				return VAR;
			};
		};
		final ArrayLength length = new ArrayLength(value);
		Assert.assertThat(length.getVariables(), sameInstance(value.getVariables()));
	}

}
