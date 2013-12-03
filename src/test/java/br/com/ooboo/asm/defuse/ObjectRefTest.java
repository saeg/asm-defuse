package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObjectRefTest {

	private ObjectRef ref;

	@Before
	public void setUp() {
		ref = new ObjectRef("Ljava.lang.String;");
	}

	@Test
	public void SizeIsOne() {
		Assert.assertEquals(1, ref.getSize());
	}

	@Test
	public void ObjectRefToString() {
		Assert.assertEquals("ObjectRef(java.lang.String)", ref.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenObjectDescriptorIsInvalid() {
		new ObjectRef("[I");
	}

}
