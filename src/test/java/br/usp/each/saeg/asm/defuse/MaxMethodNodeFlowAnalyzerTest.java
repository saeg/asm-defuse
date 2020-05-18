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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class MaxMethodNodeFlowAnalyzerTest {

    private FlowAnalyzer<BasicValue> analyzer;

    @Before
    public void setUp() throws AnalyzerException {
        analyzer = new FlowAnalyzer<BasicValue>(new BasicInterpreter());
        analyzer.analyze("Owner", new MaxMethodNode());
    }

    @Test
    public void testSuccessors() {
        Assert.assertArrayEquals(new int[] { 1 }, analyzer.getSuccessors(0));
        Assert.assertArrayEquals(new int[] { 2 }, analyzer.getSuccessors(1));
        Assert.assertArrayEquals(new int[] { 3 }, analyzer.getSuccessors(2));
        Assert.assertArrayEquals(new int[] { 4 }, analyzer.getSuccessors(3));
        Assert.assertArrayEquals(new int[] { 5 }, analyzer.getSuccessors(4));
        Assert.assertArrayEquals(new int[] { 6 }, analyzer.getSuccessors(5));
        Assert.assertArrayEquals(new int[] { 7 }, analyzer.getSuccessors(6));
        Assert.assertArrayEquals(new int[] { 8 }, analyzer.getSuccessors(7));
        Assert.assertArrayEquals(new int[] { 9 }, analyzer.getSuccessors(8));
        Assert.assertArrayEquals(new int[] { 10 }, analyzer.getSuccessors(9));
        Assert.assertArrayEquals(new int[] { 11, 23 }, analyzer.getSuccessors(10));
        Assert.assertArrayEquals(new int[] { 12 }, analyzer.getSuccessors(11));
        Assert.assertArrayEquals(new int[] { 13 }, analyzer.getSuccessors(12));
        Assert.assertArrayEquals(new int[] { 14 }, analyzer.getSuccessors(13));
        Assert.assertArrayEquals(new int[] { 15 }, analyzer.getSuccessors(14));
        Assert.assertArrayEquals(new int[] { 16, 20 }, analyzer.getSuccessors(15));
        Assert.assertArrayEquals(new int[] { 17 }, analyzer.getSuccessors(16));
        Assert.assertArrayEquals(new int[] { 18 }, analyzer.getSuccessors(17));
        Assert.assertArrayEquals(new int[] { 19 }, analyzer.getSuccessors(18));
        Assert.assertArrayEquals(new int[] { 20 }, analyzer.getSuccessors(19));
        Assert.assertArrayEquals(new int[] { 21 }, analyzer.getSuccessors(20));
        Assert.assertArrayEquals(new int[] { 22 }, analyzer.getSuccessors(21));
        Assert.assertArrayEquals(new int[] { 7 }, analyzer.getSuccessors(22));
        Assert.assertArrayEquals(new int[] { 24 }, analyzer.getSuccessors(23));
        Assert.assertArrayEquals(new int[] { 25 }, analyzer.getSuccessors(24));
        Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(25));
    }

    @Test
    public void testPredecessors() {
        Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(0));
        Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPredecessors(1));
        Assert.assertArrayEquals(new int[] { 1 }, analyzer.getPredecessors(2));
        Assert.assertArrayEquals(new int[] { 2 }, analyzer.getPredecessors(3));
        Assert.assertArrayEquals(new int[] { 3 }, analyzer.getPredecessors(4));
        Assert.assertArrayEquals(new int[] { 4 }, analyzer.getPredecessors(5));
        Assert.assertArrayEquals(new int[] { 5 }, analyzer.getPredecessors(6));
        Assert.assertArrayEquals(new int[] { 6, 22 }, analyzer.getPredecessors(7));
        Assert.assertArrayEquals(new int[] { 7 }, analyzer.getPredecessors(8));
        Assert.assertArrayEquals(new int[] { 8 }, analyzer.getPredecessors(9));
        Assert.assertArrayEquals(new int[] { 9 }, analyzer.getPredecessors(10));
        Assert.assertArrayEquals(new int[] { 10 }, analyzer.getPredecessors(11));
        Assert.assertArrayEquals(new int[] { 11 }, analyzer.getPredecessors(12));
        Assert.assertArrayEquals(new int[] { 12 }, analyzer.getPredecessors(13));
        Assert.assertArrayEquals(new int[] { 13 }, analyzer.getPredecessors(14));
        Assert.assertArrayEquals(new int[] { 14 }, analyzer.getPredecessors(15));
        Assert.assertArrayEquals(new int[] { 15 }, analyzer.getPredecessors(16));
        Assert.assertArrayEquals(new int[] { 16 }, analyzer.getPredecessors(17));
        Assert.assertArrayEquals(new int[] { 17 }, analyzer.getPredecessors(18));
        Assert.assertArrayEquals(new int[] { 18 }, analyzer.getPredecessors(19));
        Assert.assertArrayEquals(new int[] { 15, 19 }, analyzer.getPredecessors(20));
        Assert.assertArrayEquals(new int[] { 20 }, analyzer.getPredecessors(21));
        Assert.assertArrayEquals(new int[] { 21 }, analyzer.getPredecessors(22));
        Assert.assertArrayEquals(new int[] { 10 }, analyzer.getPredecessors(23));
        Assert.assertArrayEquals(new int[] { 23 }, analyzer.getPredecessors(24));
        Assert.assertArrayEquals(new int[] { 24 }, analyzer.getPredecessors(25));
    }

    @Test
    public void testBasicBlocksLeaders() {
        final int[] leaders = analyzer.getLeaders();
        // Block 0
        Assert.assertEquals(0, leaders[0]);
        Assert.assertEquals(0, leaders[1]);
        Assert.assertEquals(0, leaders[2]);
        Assert.assertEquals(0, leaders[3]);
        Assert.assertEquals(0, leaders[4]);
        Assert.assertEquals(0, leaders[5]);
        Assert.assertEquals(0, leaders[6]);
        // Block 1
        Assert.assertEquals(1, leaders[7]);
        Assert.assertEquals(1, leaders[8]);
        Assert.assertEquals(1, leaders[9]);
        Assert.assertEquals(1, leaders[10]);
        // Block 3
        Assert.assertEquals(3, leaders[11]);
        Assert.assertEquals(3, leaders[12]);
        Assert.assertEquals(3, leaders[13]);
        Assert.assertEquals(3, leaders[14]);
        Assert.assertEquals(3, leaders[15]);
        // Block 5
        Assert.assertEquals(5, leaders[16]);
        Assert.assertEquals(5, leaders[17]);
        Assert.assertEquals(5, leaders[18]);
        Assert.assertEquals(5, leaders[19]);
        // Block 4
        Assert.assertEquals(4, leaders[20]);
        Assert.assertEquals(4, leaders[21]);
        Assert.assertEquals(4, leaders[22]);
        // Block 2
        Assert.assertEquals(2, leaders[23]);
        Assert.assertEquals(2, leaders[24]);
        Assert.assertEquals(2, leaders[25]);
    }

    @Test
    public void testBasicBlockInstructionsSequence() throws AnalyzerException {
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, analyzer.getBasicBlock(0));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10 }, analyzer.getBasicBlock(1));
        Assert.assertArrayEquals(new int[] { 23, 24, 25 }, analyzer.getBasicBlock(2));
        Assert.assertArrayEquals(new int[] { 11, 12, 13, 14, 15 }, analyzer.getBasicBlock(3));
        Assert.assertArrayEquals(new int[] { 20, 21, 22 }, analyzer.getBasicBlock(4));
        Assert.assertArrayEquals(new int[] { 16, 17, 18, 19 }, analyzer.getBasicBlock(5));
    }

}
