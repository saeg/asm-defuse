/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2014 Roberto Araujo (roberto.andrioli@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
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
