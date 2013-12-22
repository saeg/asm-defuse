package br.com.ooboo.asm.defuse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

public class VariableTest {

	private Variable var;

	@Before
	public void setUp() {
		var = new Variable(Type.INT_TYPE);
	}

	@Test
	public void VariableListContainsSelf() {
		Assert.assertTrue(var.getVariables().contains(var));
	}

	@Test
	public void VariablesListIsUnmodifiable() {
		Assert.assertEquals(var.getVariables().getClass(), 
				Collections.singletonList(var).getClass());
	}

}
