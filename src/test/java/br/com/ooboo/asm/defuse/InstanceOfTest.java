package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class InstanceOfTest {

	@Test
	public void SizeIsOne() {
		final Value ignore = null;
		final InstanceOf iof = new InstanceOf(ignore);
		Assert.assertEquals(1, iof.getSize());
	}

	@Test
	public void ArrayLengthToString() {
		final Value v = new Value(Type.INT_TYPE) {
			@Override
			public String toString() {
				return "value";
			};
		};
		final InstanceOf iof = new InstanceOf(v);
		Assert.assertEquals("InstanceOf(value)", iof.toString());
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
		final InstanceOf iof = new InstanceOf(value);
		Assert.assertThat(iof.getVariables(), sameInstance(value.getVariables()));
	}

}
