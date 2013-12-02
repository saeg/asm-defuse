package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.hasItem;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class VariableTest {

	@Test
	public void SizeOfLongIsTwo() {
		final Variable var = new Variable(Type.LONG_TYPE);
		Assert.assertEquals(2, var.getSize());
	}

	@Test
	public void SizeOfDoubleIsTwo() {
		final Variable var = new Variable(Type.DOUBLE_TYPE);
		Assert.assertEquals(2, var.getSize());
	}

	@Test
	public void SizeOfAnyOtherTypeIsOne() {
		final Variable var = new Variable(Type.INT_TYPE);
		Assert.assertEquals(1, var.getSize());
	}

	@Test
	public void VariableListContainsSelf() {
		final Variable var = new Variable(Type.INT_TYPE);
		Assert.assertThat(var.getVariables(), hasItem(var));
	}

	@Test
	public void VariablesListIsUnmodifiable() {
		final Variable var = new Variable(Type.INT_TYPE);
		Assert.assertEquals(var.getVariables().getClass(), 
				Collections.singletonList(var).getClass());
	}

}
