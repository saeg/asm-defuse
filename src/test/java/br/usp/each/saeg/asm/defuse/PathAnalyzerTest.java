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

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class PathAnalyzerTest {

    private PathAnalyzer<BasicValue> analyzer;

    @Before
    public void setUp() {
        analyzer = new PathAnalyzer<BasicValue>(new BasicInterpreter());
    }

    @Test
    public void testPath() throws AnalyzerException {
        final MethodNode mn = new MethodNode();

        // set-up

        mn.desc = "(I)V";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        // instructions

        final LabelNode ret1 = new LabelNode();
        final LabelNode ret2 = new LabelNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, ret1));
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, ret2));
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.instructions.add(ret1);
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.instructions.add(ret2);
        mn.instructions.add(new InsnNode(Opcodes.RETURN));

        analyzer.analyze("Owner", mn);

        Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPath(0));
        Assert.assertArrayEquals(new int[] { 0, 1 }, analyzer.getPath(1));
        Assert.assertArrayEquals(new int[] { 0, 1, 2 }, analyzer.getPath(2));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3 }, analyzer.getPath(3));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, analyzer.getPath(4));
        Assert.assertArrayEquals(new int[] { 0, 1, 5 }, analyzer.getPath(5));
        Assert.assertArrayEquals(new int[] { 0, 1, 5, 6 }, analyzer.getPath(6));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 7 }, analyzer.getPath(7));
        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 7, 8 }, analyzer.getPath(8));
    }

    @Test
    public void testPathInfiniteLoop() throws AnalyzerException {
        final MethodNode mn = new MethodNode();

        // set-up

        mn.desc = "()V";
        mn.maxLocals = 0;
        mn.maxStack = 0;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        // instructions

        final LabelNode label = new LabelNode();
        mn.instructions.add(label);
        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, label));

        analyzer.analyze("Owner", mn);

        Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPath(0));
        Assert.assertArrayEquals(new int[] { 0, 1 }, analyzer.getPath(1));
    }

}
