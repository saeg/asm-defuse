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

public class MaxMethodNodePathAnalyzerTest {

    private PathAnalyzer<BasicValue> analyzer;

    @Before
    public void setUp() throws AnalyzerException {
        analyzer = new PathAnalyzer<BasicValue>(new BasicInterpreter());
        analyzer.analyze("Owner", new MaxMethodNode());
    }

    @Test
    public void testPaths() {
        Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPath(0));
        Assert.assertArrayEquals(new int[] { 0, 1 }, analyzer.getPath(1));
        Assert.assertArrayEquals(new int[] { 0, 1, 2 }, analyzer.getPath(2));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3 }, analyzer.getPath(3));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, analyzer.getPath(4));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5 }, analyzer.getPath(5));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, analyzer.getPath(6));
        Assert.assertArrayEquals(new int[] { 7 }, analyzer.getPath(7));
        Assert.assertArrayEquals(new int[] { 7, 8 }, analyzer.getPath(8));
        Assert.assertArrayEquals(new int[] { 7, 8, 9 }, analyzer.getPath(9));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10 }, analyzer.getPath(10));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11 }, analyzer.getPath(11));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12 }, analyzer.getPath(12));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13 }, analyzer.getPath(13));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14 }, analyzer.getPath(14));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14, 15 }, analyzer.getPath(15));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, analyzer.getPath(16));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }, analyzer.getPath(17));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, analyzer.getPath(18));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 }, analyzer.getPath(19));
        Assert.assertArrayEquals(new int[] { 20 }, analyzer.getPath(20));
        Assert.assertArrayEquals(new int[] { 20, 21 }, analyzer.getPath(21));
        Assert.assertArrayEquals(new int[] { 20, 21, 22 }, analyzer.getPath(22));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 23 }, analyzer.getPath(23));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 23, 24 }, analyzer.getPath(24));
        Assert.assertArrayEquals(new int[] { 7, 8, 9, 10, 23, 24, 25 }, analyzer.getPath(25));
    }

}
