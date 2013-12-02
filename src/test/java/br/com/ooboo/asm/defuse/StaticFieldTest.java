package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Test;

public class StaticFieldTest {

	@Test
	public void StaticFieldToString() {
		Assert.assertEquals("S@Owner.Name", 
				new StaticField("Owner", "Name", "Desc").toString());
		Assert.assertEquals("S@pkg.Owner.Name",
				new StaticField("pkg/Owner", "Name", "Desc").toString());
	}

}
