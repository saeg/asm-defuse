/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2020 Roberto Araujo (roberto.andrioli@gmail.com)
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
package br.usp.each.saeg.asm.defuse;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import br.usp.each.saeg.commons.ArrayUtils;

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
        Assert.assertTrue(c.isComputationalChain());
        Assert.assertFalse(c.isPredicateChain());
        Assert.assertEquals(String.format("(%d, %d, %d)", def, use, var), c.toString());
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
        Assert.assertFalse(c.isComputationalChain());
        Assert.assertTrue(c.isPredicateChain());
        Assert.assertEquals(String.format("(%d, (%d,%d), %d)", def, use, target, var), c.toString());
    }

    @Test
    public void EqualsReturnTrue() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
        final DefUseChain c2 = new DefUseChain(1, 2, 3, 4);
        Assert.assertTrue(c1.equals(c2));
    }

    @Test
    public void EqualsReturnSameHashCode() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
        final DefUseChain c2 = new DefUseChain(1, 2, 3, 4);
        Assert.assertEquals(c1.hashCode(), c2.hashCode());
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
    public void DifferentDefReturnsDifferentHashCode() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3);
        final DefUseChain c2 = new DefUseChain(4, 2, 3);
        Assert.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void DifferentUseReturnsFalseOnEquals() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3);
        final DefUseChain c2 = new DefUseChain(1, 4, 3);
        Assert.assertFalse(c1.equals(c2));
    }

    @Test
    public void DifferentUseReturnsDifferentHashCode() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3);
        final DefUseChain c2 = new DefUseChain(1, 4, 3);
        Assert.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void DifferentVarReturnsFalseOnEquals() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3);
        final DefUseChain c2 = new DefUseChain(1, 2, 4);
        Assert.assertFalse(c1.equals(c2));
    }

    @Test
    public void DifferentVarReturnsDifferentHashCode() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3);
        final DefUseChain c2 = new DefUseChain(1, 2, 4);
        Assert.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void DifferentTargetReturnsFalseOnEquals() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
        final DefUseChain c2 = new DefUseChain(1, 2, 5, 4);
        Assert.assertFalse(c1.equals(c2));
    }

    @Test
    public void DifferentTargetReturnsDifferentHashCode() {
        final DefUseChain c1 = new DefUseChain(1, 2, 3, 4);
        final DefUseChain c2 = new DefUseChain(1, 2, 5, 4);
        Assert.assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void ToBasicBlockLocal() {
        final DefUseChain[] chains = new DefUseChain[1];
        chains[0] = new DefUseChain(0, 1, 2);

        final int[] leaders = new int[2];
        leaders[0] = 0;
        leaders[1] = 0;

        final int[][] basicBlocks = new int[1][];
        basicBlocks[0] = new int[] { 0, 1 };

        Assert.assertEquals(0, DefUseChain.toBasicBlock(chains, leaders, basicBlocks).length);
        Assert.assertFalse(DefUseChain.isGlobal(chains[0], leaders, basicBlocks));
        Assert.assertTrue(DefUseChain.isLocal(chains[0], leaders, basicBlocks));
    }

    @Test
    public void ToBasicBlockComputationalUseGlobal() {
        final DefUseChain[] chains = new DefUseChain[1];
        chains[0] = new DefUseChain(1, 3, 5);

        final int[] leaders = new int[4];
        leaders[0] = 0;
        leaders[1] = 0;
        leaders[2] = 1;
        leaders[3] = 1;

        final int[][] basicBlocks = new int[2][];
        basicBlocks[0] = new int[] { 0, 1 };
        basicBlocks[1] = new int[] { 2, 3 };

        final DefUseChain[] bbChains = DefUseChain.toBasicBlock(chains, leaders, basicBlocks);
        Assert.assertTrue(ArrayUtils.contains(bbChains, new DefUseChain(0, 1, 5)));
        Assert.assertEquals(1, bbChains.length);
        Assert.assertTrue(DefUseChain.isGlobal(chains[0], leaders, basicBlocks));
        Assert.assertFalse(DefUseChain.isLocal(chains[0], leaders, basicBlocks));
    }

    @Test
    public void ToBasicBlockPredicateUseDefUseChainGlobal() {
        final DefUseChain[] chains = new DefUseChain[1];
        chains[0] = new DefUseChain(1, 3, 4, 5);

        final int[] leaders = new int[5];
        leaders[0] = 0;
        leaders[1] = 0;
        leaders[2] = 1;
        leaders[3] = 1;
        leaders[4] = 2;

        final int[][] basicBlocks = new int[3][];
        basicBlocks[0] = new int[] { 0, 1 };
        basicBlocks[1] = new int[] { 2, 3 };
        basicBlocks[2] = new int[] { 4 };

        final DefUseChain[] bbChains = DefUseChain.toBasicBlock(chains, leaders, basicBlocks);
        Assert.assertTrue(ArrayUtils.contains(bbChains, new DefUseChain(0, 1, 2, 5)));
        Assert.assertEquals(1, bbChains.length);
        Assert.assertTrue(DefUseChain.isGlobal(chains[0], leaders, basicBlocks));
        Assert.assertFalse(DefUseChain.isLocal(chains[0], leaders, basicBlocks));
    }

    @Test
    public void ToBasicBlockPredicateUseDefUseChainSameBlock() {
        final DefUseChain[] chains = new DefUseChain[1];
        chains[0] = new DefUseChain(0, 1, 2, 3);

        final int[] leaders = new int[3];
        leaders[0] = 0;
        leaders[1] = 0;
        leaders[2] = 1;

        final int[][] basicBlocks = new int[2][];
        basicBlocks[0] = new int[] { 0, 1 };
        basicBlocks[1] = new int[] { 2 };

        final DefUseChain[] bbChains = DefUseChain.toBasicBlock(chains, leaders, basicBlocks);
        Assert.assertTrue(ArrayUtils.contains(bbChains, new DefUseChain(0, 0, 1, 3)));
        Assert.assertEquals(1, bbChains.length);
        Assert.assertTrue(DefUseChain.isGlobal(chains[0], leaders, basicBlocks));
        Assert.assertFalse(DefUseChain.isLocal(chains[0], leaders, basicBlocks));
    }

    @Test
    public void PredicateUseDefUseChainIsAlwaysGlobal() {
        final DefUseChain chain = new DefUseChain(0, 1, 2, 3);

        final int[] leaders = new int[3];
        leaders[0] = 0;
        leaders[1] = 0;
        leaders[2] = 1;

        final int[][] basicBlocks = new int[2][];
        basicBlocks[0] = new int[] { 0, 1 };
        basicBlocks[1] = new int[] { 2 };

        Assert.assertTrue(DefUseChain.isGlobal(chain, leaders, basicBlocks));
        Assert.assertFalse(DefUseChain.isLocal(chain, leaders, basicBlocks));
    }

    @Test
    public void ToBasicBlockRemoveDuplicates() {
        final DefUseChain[] chains = new DefUseChain[2];
        chains[0] = new DefUseChain(0, 2, 4);
        chains[1] = new DefUseChain(1, 3, 4);

        final int[] leaders = new int[4];
        leaders[0] = 0;
        leaders[1] = 0;
        leaders[2] = 1;
        leaders[3] = 1;

        final int[][] basicBlocks = new int[2][];
        basicBlocks[0] = new int[] { 0, 1 };
        basicBlocks[1] = new int[] { 2, 3 };

        final DefUseChain[] bbChains = DefUseChain.toBasicBlock(chains, leaders, basicBlocks);
        Assert.assertTrue(ArrayUtils.contains(bbChains, new DefUseChain(0, 1, 4)));
        Assert.assertEquals(1, bbChains.length);
    }

}
