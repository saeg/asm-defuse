package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Test;

public class ConstantTest {

	@Test
	public void WORDSizeIsOne() {
		Assert.assertEquals(1, Constant.WORD.getSize());
	}

	@Test
	public void DWORDSizeIsTwo() {
		Assert.assertEquals(2, Constant.DWORD.getSize());
	}

	@Test
	public void ConstantToString() {
		Assert.assertEquals("Constant", Constant.WORD.toString());
		Assert.assertEquals("Constant", Constant.DWORD.toString());
	}

}
