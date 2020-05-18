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
package br.usp.each.saeg.asm.defuse.integration;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.MaxMethodNode;
import br.usp.each.saeg.asm.defuse.ObjectField;
import br.usp.each.saeg.asm.defuse.StaticField;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;
import br.usp.each.saeg.commons.ArrayUtils;

public class DefUseAnalyzerTest {

    private FlowAnalyzer<Value> flowAnalyzer;

    private DefUseAnalyzer analyzer;

    private MethodNode mn;

    @Before
    public void setUp() {
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
        analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
    }

    public void prepareMethodWithUnreachableCode() {
        mn = new MethodNode();
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        // unreachable instruction (frame will be null)
        mn.instructions.add(new LabelNode());
        mn.desc = "()V";
        mn.maxLocals = 0;
        mn.maxStack = 0;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();
    }

    public void prepareMethodWithUnreachableCodeThatManipulateVariableArrayAndStack() {
        mn = new MethodNode();
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        // unreachable instruction (frame will be null)
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
        mn.desc = "()V";
        mn.maxLocals = 2;
        mn.maxStack = 1;
        mn.access = 0;
        mn.tryCatchBlocks = Collections.emptyList();
    }

    public void prepareMethodWithTryCatchBlock() {
        final LabelNode begin = new LabelNode();
        final LabelNode end = new LabelNode();
        final LabelNode handler = new LabelNode();
        final LabelNode jmp = new LabelNode();
        final TryCatchBlockNode tcb = new TryCatchBlockNode(begin, end, handler, "Exception");

        mn = new MethodNode();
        mn.instructions.add(begin);
        mn.instructions.add(new InsnNode(Opcodes.NOP));
        mn.instructions.add(end);
        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, jmp));
        mn.instructions.add(handler);
        mn.instructions.add(new InsnNode(Opcodes.POP));
        mn.instructions.add(jmp);
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.desc = "()V";
        mn.maxLocals = 0;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.singletonList(tcb);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefinitionOfLocalVariable() throws AnalyzerException {
        mn = new MaxMethodNode();
        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        final int n = frames.length;
        final Set<Variable>[] defs = (Set<Variable>[]) new Set<?>[n];

        // Default
        for (int i = 0; i < n; i++) {
            defs[i] = new LinkedHashSet<Variable>();
        }

        // Set instructions that define a local variable
        defs[0].add(new Local(Type.INT_TYPE, 0));
        defs[0].add(new Local(Type.INT_TYPE, 1));
        defs[1].add(new Local(Type.INT_TYPE, 2));
        defs[4].add(new Local(Type.INT_TYPE, 2));
        defs[6].add(new Local(Type.INT_TYPE, 3));
        defs[19].add(new Local(Type.INT_TYPE, 3));
        defs[21].add(new Local(Type.INT_TYPE, 2));

        // Assert
        for (int i = 0; i < n; i++) {
            Assert.assertEquals("Instruction: " + i, defs[i], frames[i].getDefinitions());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUsesOfLocalVariables() throws AnalyzerException {
        mn = new MaxMethodNode();
        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        final int n = frames.length;
        final Set<Variable>[] uses = (Set<Variable>[]) new Set<?>[n];

        // Default
        for (int i = 0; i < n; i++) {
            uses[i] = new LinkedHashSet<Variable>();
        }

        // Set instructions that uses a local variable
        uses[4].add(new Local(Type.INT_TYPE, 2));
        uses[6].add(new Local(Type.INT_TYPE, 0));
        uses[6].add(new Local(Type.INT_TYPE, 2));
        uses[10].add(new Local(Type.INT_TYPE, 1));
        uses[10].add(new Local(Type.INT_TYPE, 2));
        uses[15].add(new Local(Type.INT_TYPE, 0));
        uses[15].add(new Local(Type.INT_TYPE, 2));
        uses[15].add(new Local(Type.INT_TYPE, 3));
        uses[19].add(new Local(Type.INT_TYPE, 0));
        uses[19].add(new Local(Type.INT_TYPE, 2));
        uses[21].add(new Local(Type.INT_TYPE, 2));
        uses[25].add(new Local(Type.INT_TYPE, 3));

        // Assert
        for (int i = 0; i < n; i++) {
            Assert.assertEquals("Instruction: " + i, uses[i], frames[i].getUses());
        }
    }

    @Test
    public void testVariables() throws AnalyzerException {
        mn = new MaxMethodNode();
        analyzer.analyze("Owner", mn);
        final Variable[] variables = analyzer.getVariables();
        Assert.assertEquals(new Local(Type.INT_TYPE, 0), variables[0]);
        Assert.assertEquals(new Local(Type.INT_TYPE, 1), variables[1]);
        Assert.assertEquals(new Local(Type.INT_TYPE, 2), variables[2]);
        Assert.assertEquals(new Local(Type.INT_TYPE, 3), variables[3]);
        Assert.assertEquals(4, variables.length);
    }

    @Test
    public void testDefUseChainsGlobal() throws AnalyzerException {
        mn = new MaxMethodNode();
        analyzer.analyze("Owner", mn);
        DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                analyzer.getDefUseFrames(),
                analyzer.getVariables(),
                flowAnalyzer.getSuccessors(),
                flowAnalyzer.getPredecessors());
        chains = DefUseChain.globals(chains, flowAnalyzer.getLeaders(), flowAnalyzer.getBasicBlocks());
        final DefUseChain[] expected = new DefUseChain[23];

        expected[0] = new DefUseChain(0, 10, 23, 1);
        expected[1] = new DefUseChain(0, 10, 11, 1);
        expected[2] = new DefUseChain(4, 10, 23, 2);
        expected[3] = new DefUseChain(4, 10, 11, 2);
        expected[4] = new DefUseChain(21, 10, 23, 2);
        expected[5] = new DefUseChain(21, 10, 11, 2);

        expected[6] = new DefUseChain(0, 15, 20, 0);
        expected[7] = new DefUseChain(0, 15, 16, 0);
        expected[8] = new DefUseChain(4, 15, 20, 2);
        expected[9] = new DefUseChain(4, 15, 16, 2);
        expected[10] = new DefUseChain(21, 15, 20, 2);
        expected[11] = new DefUseChain(21, 15, 16, 2);
        expected[12] = new DefUseChain(6, 15, 20, 3);
        expected[13] = new DefUseChain(6, 15, 16, 3);
        expected[14] = new DefUseChain(19, 15, 20, 3);
        expected[15] = new DefUseChain(19, 15, 16, 3);

        expected[16] = new DefUseChain(0, 19, 0);
        expected[17] = new DefUseChain(4, 19, 2);
        expected[18] = new DefUseChain(21, 19, 2);

        expected[19] = new DefUseChain(4, 21, 2);
        expected[20] = new DefUseChain(21, 21, 2);

        expected[21] = new DefUseChain(6, 25, 3);
        expected[22] = new DefUseChain(19, 25, 3);

        Assert.assertEquals(expected.length, chains.length);

        final StringBuilder message = new StringBuilder();
        for (int i = 0; i < expected.length; i++) {
            if (ArrayUtils.indexOf(chains, expected[i]) == -1) {
                message.append("Not found dua: ").append(i).append('\n');
            }
        }

        if (message.length() > 0) {
            Assert.fail(message.toString());
        }
    }

    @Test
    public void testDefUseChainsLocal() throws AnalyzerException {
        mn = new MaxMethodNode();
        analyzer.analyze("Owner", mn);
        DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                analyzer.getDefUseFrames(),
                analyzer.getVariables(),
                flowAnalyzer.getSuccessors(),
                flowAnalyzer.getPredecessors());
        chains = DefUseChain.locals(chains, flowAnalyzer.getLeaders(), flowAnalyzer.getBasicBlocks());
        final DefUseChain[] expected = new DefUseChain[3];

        expected[0] = new DefUseChain(0, 6, 0);
        expected[1] = new DefUseChain(1, 4, 2);
        expected[2] = new DefUseChain(4, 6, 2); // Bug/Known limitation here

        Assert.assertEquals(expected.length, chains.length);

        final StringBuilder message = new StringBuilder();
        for (int i = 0; i < expected.length; i++) {
            if (ArrayUtils.indexOf(chains, expected[i]) == -1) {
                message.append("Not found dua: ").append(i).append('\n');
            }
        }

        if (message.length() > 0) {
            Assert.fail(message.toString());
        }
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenAFrameIsNull() {
        prepareMethodWithUnreachableCode();
        Exception exception = null;
        try {
            analyzer.analyze("Owner", mn);
        } catch (final Exception e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenExecuteUnreachableCode() {
        prepareMethodWithUnreachableCodeThatManipulateVariableArrayAndStack();
        Exception exception = null;
        try {
            analyzer.analyze("Owner", mn);
        } catch (final Exception e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenExecuteUnreachableCodeCausedByExceptionFlow() {
        prepareMethodWithTryCatchBlock();
        Exception exception = null;
        try {
            analyzer.analyze("Owner", mn);
        } catch (final Exception e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }

    @Test
    public void ShouldNotCreateEdgesToUnreachableInstruction() throws AnalyzerException {
        prepareMethodWithUnreachableCode();
        analyzer.analyze("Owner", mn);
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getSuccessors(0));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getSuccessors(1));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getPredecessors(0));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getPredecessors(1));
    }

    @Test
    public void ShouldNotCreateEdgesOnExceptionFlow() throws AnalyzerException {
        prepareMethodWithTryCatchBlock();
        analyzer.analyze("Owner", mn);

        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getPredecessors(0));
        Assert.assertArrayEquals(new int[] { 0 }, flowAnalyzer.getPredecessors(1));
        Assert.assertArrayEquals(new int[] { 1 }, flowAnalyzer.getPredecessors(2));
        Assert.assertArrayEquals(new int[] { 2 }, flowAnalyzer.getPredecessors(3));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getPredecessors(4));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getPredecessors(5));
        Assert.assertArrayEquals(new int[] { 3 }, flowAnalyzer.getPredecessors(6));
        Assert.assertArrayEquals(new int[] { 6 }, flowAnalyzer.getPredecessors(7));

        Assert.assertArrayEquals(new int[] { 1 }, flowAnalyzer.getSuccessors(0));
        Assert.assertArrayEquals(new int[] { 2 }, flowAnalyzer.getSuccessors(1));
        Assert.assertArrayEquals(new int[] { 3 }, flowAnalyzer.getSuccessors(2));
        Assert.assertArrayEquals(new int[] { 6 }, flowAnalyzer.getSuccessors(3));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getSuccessors(4));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getSuccessors(5));
        Assert.assertArrayEquals(new int[] { 7 }, flowAnalyzer.getSuccessors(6));
        Assert.assertArrayEquals(new int[] {}, flowAnalyzer.getSuccessors(7));

        Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 6, 7 }, flowAnalyzer.getBasicBlock(0));

        final int[] leaders = flowAnalyzer.getLeaders();
        Assert.assertEquals(0, leaders[0]);
        Assert.assertEquals(0, leaders[1]);
        Assert.assertEquals(0, leaders[2]);
        Assert.assertEquals(0, leaders[3]);
        Assert.assertEquals(0, leaders[6]);
        Assert.assertEquals(0, leaders[7]);
        Assert.assertEquals(-1, leaders[4]);
        Assert.assertEquals(-1, leaders[5]);
    }

    @Test
    public void UseOfStaticFieldWithoutDefinition() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "Owner", "name", "I"));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "()I";
        mn.maxLocals = 0;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        Assert.assertTrue(frames[0].getDefinitions().contains(new StaticField("Owner", "name", "I")));
    }

    @Test
    public void UseOfParametersWithoutDefinition() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "(I)I";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        Assert.assertTrue(frames[0].getDefinitions().contains(new Local(Type.INT_TYPE, 0)));
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenAnalyzeAbstractMethods() throws AnalyzerException {
        mn = new MethodNode();
        mn.desc = "()V";
        mn.access = Opcodes.ACC_ABSTRACT;

        analyzer.analyze("Owner", mn);
        final DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                analyzer.getDefUseFrames(),
                analyzer.getVariables(),
                flowAnalyzer.getSuccessors(),
                flowAnalyzer.getPredecessors());

        Assert.assertEquals(0, flowAnalyzer.getFrames().length);
        Assert.assertEquals(0, analyzer.getDefUseFrames().length);
        Assert.assertEquals(1, analyzer.getVariables().length);
        Assert.assertEquals(0, chains.length);
        Assert.assertEquals(0, flowAnalyzer.getLeaders().length);
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenAnalyzeNativeMethods() throws AnalyzerException {
        mn = new MethodNode();
        mn.desc = "()V";
        mn.access = Opcodes.ACC_NATIVE;

        analyzer.analyze("Owner", mn);
        final DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                analyzer.getDefUseFrames(),
                analyzer.getVariables(),
                flowAnalyzer.getSuccessors(),
                flowAnalyzer.getPredecessors());

        Assert.assertEquals(0, flowAnalyzer.getFrames().length);
        Assert.assertEquals(0, analyzer.getDefUseFrames().length);
        Assert.assertEquals(1, analyzer.getVariables().length);
        Assert.assertEquals(0, chains.length);
        Assert.assertEquals(0, flowAnalyzer.getLeaders().length);
    }

    @Test
    public void ShouldNotThrowAnExceptionWhenAnalyzeStaticNativeMethods() throws AnalyzerException {
        mn = new MethodNode();
        mn.desc = "()V";
        mn.access = Opcodes.ACC_NATIVE | Opcodes.ACC_STATIC;

        analyzer.analyze("Owner", mn);
        final DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(
                analyzer.getDefUseFrames(),
                analyzer.getVariables(),
                flowAnalyzer.getSuccessors(),
                flowAnalyzer.getPredecessors());

        Assert.assertEquals(0, flowAnalyzer.getFrames().length);
        Assert.assertEquals(0, analyzer.getDefUseFrames().length);
        Assert.assertEquals(0, analyzer.getVariables().length);
        Assert.assertEquals(0, chains.length);
        Assert.assertEquals(0, flowAnalyzer.getLeaders().length);
    }

    @Test
    public void UseOfObjectFieldWithoutDefinition() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "Owner", "name", "I"));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "()I";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        Assert.assertTrue(frames[0].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 0)));
        Assert.assertTrue(frames[0].getDefinitions().contains(
                new ObjectField("Owner", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 0))));
    }

    @Test
    public void ShouldTerminateWhenAnalyzeAStraightLineEndlessLoop() throws AnalyzerException {
        mn = new MethodNode();
        final LabelNode label = new LabelNode();
        mn.instructions.add(label);
        mn.instructions.add(new InsnNode(Opcodes.NOP));
        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, label));
        mn.desc = "()V";
        mn.maxLocals = 0;
        mn.maxStack = 0;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();
        analyzer.analyze("Owner", mn);
    }

    @Test
    public void ShouldNotVisitAnInstructionTwiceWhenComputingBasicBlocks() throws AnalyzerException {
        final LabelNode dflt = new LabelNode();
        final LabelNode case1 = new LabelNode();
        final LabelNode case2 = new LabelNode();
        final LabelNode[] labels = new LabelNode[] { case1, case2 };

        mn = new MethodNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        mn.instructions.add(new LookupSwitchInsnNode(dflt, new int[] { 0, 1 }, labels));
        mn.instructions.add(case1);
        mn.instructions.add(new InsnNode(Opcodes.NOP));
        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, dflt));
        mn.instructions.add(case2);
        mn.instructions.add(new InsnNode(Opcodes.NOP));
        mn.instructions.add(dflt);
        mn.instructions.add(new InsnNode(Opcodes.RETURN));
        mn.desc = "(I)V";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();
        analyzer.analyze("Owner", mn);

        final int[] leaders = flowAnalyzer.getLeaders();
        final int[][] bBlocks = flowAnalyzer.getBasicBlocks();

        Assert.assertEquals(0, leaders[0]);
        Assert.assertEquals(0, leaders[1]);

        Assert.assertEquals(2, leaders[2]);
        Assert.assertEquals(2, leaders[3]);
        Assert.assertEquals(2, leaders[4]);

        Assert.assertEquals(1, leaders[5]);
        Assert.assertEquals(1, leaders[6]);

        Assert.assertEquals(3, leaders[7]);
        Assert.assertEquals(3, leaders[8]);

        Assert.assertEquals(4, bBlocks.length);
        Assert.assertArrayEquals(new int[] { 0, 1 }, bBlocks[0]);
        Assert.assertArrayEquals(new int[] { 5, 6 }, bBlocks[1]);
        Assert.assertArrayEquals(new int[] { 2, 3, 4 }, bBlocks[2]);
        Assert.assertArrayEquals(new int[] { 7, 8 }, bBlocks[3]);
    }

    @Test
    public void DefinitionOfReferenceToObjectField() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "LMyClass;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 0));
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "MyClass", "name", "I"));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "()I";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();

        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frames[0].getLocal(0));
        Assert.assertTrue(frames[1].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 0)));
        Assert.assertTrue(frames[1].getDefinitions().contains(
                new ObjectField("MyClass", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 0))));
    }

    @Test
    public void DefinitionOfReferenceToObjectField2() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
        final LabelNode label = new LabelNode();
        mn.instructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "LMyClass1;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
        final LabelNode end = new LabelNode();
        mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, end));
        mn.instructions.add(label);
        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "LMyClass2;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
        mn.instructions.add(end);
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "MyClass0", "name", "I"));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "(Z)I";
        mn.maxLocals = 2;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();

        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frames[0].getLocal(1));

        Assert.assertTrue(frames[3].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 1)));

        Assert.assertTrue(frames[7].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 1)));

        Assert.assertTrue(frames[3].getDefinitions().contains(
                new ObjectField("MyClass0", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 1))));

        Assert.assertTrue(frames[7].getDefinitions().contains(
                new ObjectField("MyClass0", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 1))));
    }

    @Test
    public void DefinitionOfReferenceToObjectField3() throws AnalyzerException {
        mn = new MethodNode();
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        final LabelNode skip = new LabelNode();
        mn.instructions.add(new JumpInsnNode(Opcodes.IFNONNULL, skip));
        mn.instructions.add(new TypeInsnNode(Opcodes.NEW, "LMyClass;"));
        mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 0));
        mn.instructions.add(skip);
        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "MyClass", "name", "I"));
        mn.instructions.add(new InsnNode(Opcodes.IRETURN));
        mn.desc = "(LMyClass;)I";
        mn.maxLocals = 1;
        mn.maxStack = 1;
        mn.access = Opcodes.ACC_STATIC;
        mn.tryCatchBlocks = Collections.emptyList();

        analyzer.analyze("Owner", mn);
        final DefUseFrame[] frames = analyzer.getDefUseFrames();

        Assert.assertTrue(frames[0].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 0)));

        Assert.assertTrue(frames[3].getDefinitions().contains(
                new Local(Type.getObjectType("java/lang/Object"), 0)));

        Assert.assertTrue(frames[0].getDefinitions().contains(
                new ObjectField("MyClass", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 0))));

        Assert.assertTrue(frames[3].getDefinitions().contains(
                new ObjectField("MyClass", "name", "I",
                        new Local(Type.getObjectType("java/lang/Object"), 0))));
    }

}
