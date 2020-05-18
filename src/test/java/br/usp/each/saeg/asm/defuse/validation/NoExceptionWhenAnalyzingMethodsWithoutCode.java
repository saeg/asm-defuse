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
package br.usp.each.saeg.asm.defuse.validation;

import static br.usp.each.saeg.asm.defuse.validation.ValidationTestUtil.getClassNode;
import static br.usp.each.saeg.asm.defuse.validation.ValidationTestUtil.getMethodNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.PathAnalyzer;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;
import br.usp.each.saeg.asm.defuse.validation.targets.Interface;
import br.usp.each.saeg.asm.defuse.validation.targets.Native;

@RunWith(Parameterized.class)
public class NoExceptionWhenAnalyzingMethodsWithoutCode {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Interface.class, "interfaceMethod", "()V" },
                { Native.class, "nativeMethod", "()V" }
        });
    }

    private final MethodNode mn;

    public NoExceptionWhenAnalyzingMethodsWithoutCode(
            final Class<?> target, final String name, final String desc) throws IOException {
        mn = getMethodNode(getClassNode(target), name, desc);
    }

    @Test
    public void flowAnalyzer() throws AnalyzerException {
        final FlowAnalyzer<BasicValue> analyzer = new FlowAnalyzer<BasicValue>(new BasicInterpreter());
        final Frame<BasicValue>[] frames = analyzer.analyze("owner", mn);
        final int[][] successors = analyzer.getSuccessors();
        final int[][] predecessors = analyzer.getPredecessors();
        final int[][] basicBlocks = analyzer.getBasicBlocks();
        final int[] leaders = analyzer.getLeaders();

        Assert.assertEquals(0, frames.length);
        Assert.assertEquals(0, successors.length);
        Assert.assertEquals(0, predecessors.length);
        Assert.assertEquals(0, basicBlocks.length);
        Assert.assertEquals(0, leaders.length);
    }

    @Test
    public void pathAnalyzer() throws AnalyzerException {
        final PathAnalyzer<BasicValue> analyzer = new PathAnalyzer<BasicValue>(new BasicInterpreter());
        final Frame<BasicValue>[] frames = analyzer.analyze("owner", mn);
        final int[][] paths = analyzer.getPaths();

        Assert.assertEquals(0, frames.length);
        Assert.assertEquals(0, paths.length);
    }

    @Test
    public void defUseAnalyzer() throws AnalyzerException {
        final DefUseAnalyzer analyzer = new DefUseAnalyzer();
        final Frame<Value>[] frames = analyzer.analyze("owner", mn);
        final DefUseFrame[] duframes = analyzer.getDefUseFrames();
        final Variable[] variables = analyzer.getVariables();

        Assert.assertEquals(0, frames.length);
        Assert.assertEquals(0, duframes.length);
        Assert.assertEquals(1, variables.length); // The methods are not static!
    }

}
