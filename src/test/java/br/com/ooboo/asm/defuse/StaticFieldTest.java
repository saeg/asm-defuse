package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StaticFieldTest {

	private StaticField sfield;

	@Before
	public void setUp() {
		sfield = new StaticField("pkg/Owner", "Name", "[D");
	}

	@Test
	public void StaticFieldToString() {
		Assert.assertEquals("S@pkg.Owner.Name", sfield.toString());
	}

}
