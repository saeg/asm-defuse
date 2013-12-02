package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Collections;

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
	public void VariablesListIsEmpty() {
		Assert.assertTrue(Constant.WORD.getVariables().isEmpty());
		Assert.assertTrue(Constant.DWORD.getVariables().isEmpty());
	}

	@Test
	public void VariablesListIsUnmodifiable() {
		Assert.assertThat(Constant.WORD.getVariables(), sameInstance(Collections.EMPTY_LIST));
		Assert.assertThat(Constant.DWORD.getVariables(), sameInstance(Collections.EMPTY_LIST));
	}

	@Test
	public void ConstantToString() {
		Assert.assertEquals("Constant", Constant.WORD.toString());
		Assert.assertEquals("Constant", Constant.DWORD.toString());
	}

}
