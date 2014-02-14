package br.com.ooboo.asm.defuse;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DefUseChainTest {

	@Test
	public void NewComputationalUseDefUseChain() {
		final Random rnd = new Random();
		final int def = rnd.nextInt();
		final int use = rnd.nextInt();
		final int var = rnd.nextInt();

		final DefUseChain c = new DefUseChain(def, use, var);

		Assert.assertEquals(def, c.def);
		Assert.assertEquals(use, c.use);
		Assert.assertEquals(var, c.var);
		Assert.assertEquals(-1, c.target);
	}

	@Test
	public void NewPredicateUseDefUseChain() {
		final Random rnd = new Random();
		final int def = rnd.nextInt();
		final int use = rnd.nextInt();
		final int var = rnd.nextInt();
		final int target = rnd.nextInt();

		final DefUseChain c = new DefUseChain(def, use, target, var);

		Assert.assertEquals(def, c.def);
		Assert.assertEquals(use, c.use);
		Assert.assertEquals(var, c.var);
		Assert.assertEquals(target, c.target);
	}

	@Test
	public void EqualsReturnTrue() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
		final DefUseChain c2 = new DefUseChain(1, 2, 3, 4);
		Assert.assertTrue(c1.equals(c2));
	}

	@Test
	public void EqualsASelfReturnTrue() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		Assert.assertTrue(c1.equals(c1));
	}

	@Test
	public void EqualsANullReturnsFalse() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		Assert.assertFalse(c1.equals(null));
	}

	@Test
	public void DifferentClassReturnsFalseOnEquals() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		final DefUseChain other = Mockito.mock(DefUseChain.class);
		Assert.assertFalse(c1.equals(other));
	}

	@Test
	public void DifferentDefReturnsFalseOnEquals() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		final DefUseChain c2 = new DefUseChain(4, 2, 3);
		Assert.assertFalse(c1.equals(c2));
	}

	@Test
	public void DifferentUseReturnsFalseOnEquals() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		final DefUseChain c2 = new DefUseChain(1, 4, 3);
		Assert.assertFalse(c1.equals(c2));
	}

	@Test
	public void DifferentVarReturnsFalseOnEquals() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3);
		final DefUseChain c2 = new DefUseChain(1, 2, 4);
		Assert.assertFalse(c1.equals(c2));
	}

	@Test
	public void DifferentTargetReturnsFalseOnEquals() {
		final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
		final DefUseChain c2 = new DefUseChain(1, 2, 5, 4);
		Assert.assertFalse(c1.equals(c2));
	}

}
