package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class CastTest {

	@Test
	public void GetVariablesDelegateToValue() {
		final Value value = new Value(Type.INT_TYPE) {
			final List<Variable> VAR = new ArrayList<Variable>(0);

			@Override
			public List<Variable> getVariables() {
				return VAR;
			};
		};
		final Cast cast = new Cast(Type.INT_TYPE, value);
		Assert.assertThat(cast.getVariables(), sameInstance(value.getVariables()));
	}

	@Test
	public void ToStringDelegateToValue() {
		final Value value = new Value(Type.INT_TYPE) {
			@Override
			public String toString() {
				return "Funny description";
			}
		};
		final Cast cast = new Cast(Type.INT_TYPE, value);
		Assert.assertThat(cast.toString(), sameInstance(value.toString()));
	}

}
