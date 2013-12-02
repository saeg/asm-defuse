package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.hasItem;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

public class VariableTest {

	@Test
	public void SizeOfLongIsTwo() {
		Variable var = new Variable(Type.LONG_TYPE);
		Assert.assertEquals(2, var.getSize());
	}

	@Test
	public void SizeOfDoubleIsTwo() {
		Variable var = new Variable(Type.DOUBLE_TYPE);
		Assert.assertEquals(2, var.getSize());
	}

	@Test
	public void SizeOfAnyOtherTypeIsOne() {
		Variable var = new Variable(Type.INT_TYPE);
		Assert.assertEquals(1, var.getSize());
	}

	@Test
	public void VariableListContainsSelf() {
		Variable var = new Variable(Type.INT_TYPE);
		Assert.assertThat(var.getVariables(), hasItem(var));
	}
	
	@Test
	public void VariablesListIsUnmodifiable() {
		Variable var = new Variable(Type.INT_TYPE);
		Assert.assertEquals(var.getVariables().getClass(), 
				Collections.singletonList(var).getClass());
	}

}
