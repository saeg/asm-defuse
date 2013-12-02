package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObjectRefTest {

	private ObjectRef ref;

	@Before
	public void setUp() {
		ref = new ObjectRef("Desc");
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, ref.getSize());
	}

	@Test
	public void VariablesListIsEmpty() {
		Assert.assertTrue(ref.getVariables().isEmpty());
	}

	@Test
	public void VariablesListIsUnmodifiable() {
		Assert.assertThat(ref.getVariables(), sameInstance(Collections.EMPTY_LIST));
	}

	@Test
	public void ObjectRefToString() {
		Assert.assertEquals("ObjectRef(Desc)", ref.toString());
	}

}
