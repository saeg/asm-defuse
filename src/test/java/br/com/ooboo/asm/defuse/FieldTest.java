package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {

	private Field field;

	@Before
	public void setUp() {
		field = new Field("pkg/Owner", "Name", "[D") {};
	}

	@Test
	public void AttributesAreCorrect() {
		Assert.assertEquals("pkg/Owner", field.owner);
		Assert.assertEquals("Name", field.name);
		Assert.assertEquals("[D", field.desc);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ThrowAnExceptionWhenDescriptorIsInvalid() {
		new StaticField("Owner", "Name", "Desc");
	}

}
