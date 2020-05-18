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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.ObjectRef;
import br.usp.each.saeg.asm.defuse.StaticField;
import br.usp.each.saeg.asm.defuse.Value;

public class FrameExecuteDefUseInterpreterTest {

    private DefUseInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new DefUseInterpreter();
    }

    @Test
    public void StoreAValueOfTypeInt() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ISTORE, 0);
        frame.push(Value.INT_VALUE);
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.INT_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeFloat() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.FSTORE, 0);
        frame.push(Value.FLOAT_VALUE);
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.FLOAT_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeLong() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.LSTORE, 0);
        frame.push(Value.LONG_VALUE);
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.LONG_VALUE.with(insn), frame.getLocal(0));
        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(1));
    }

    @Test
    public void StoreAValueOfTypeDouble() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.DSTORE, 0);
        frame.push(Value.DOUBLE_VALUE);
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.DOUBLE_VALUE.with(insn), frame.getLocal(0));
        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(1));
    }

    @Test
    public void StoreAValueOfTypeReference() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ASTORE, 0);
        frame.push(Value.REFERENCE_VALUE);
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.REFERENCE_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeObjectRef() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ASTORE, 0);
        frame.push(new ObjectRef(Type.getType("Ljava/lang/String;")));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.REFERENCE_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeStaticInt() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ISTORE, 0);
        frame.push(new StaticField("Owner", "Name", "I"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.INT_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeStaticFloat() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.FSTORE, 0);
        frame.push(new StaticField("Owner", "Name", "F"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.FLOAT_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeStaticLong() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.LSTORE, 0);
        frame.push(new StaticField("Owner", "Name", "J"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.LONG_VALUE.with(insn), frame.getLocal(0));
        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(1));
    }

    @Test
    public void StoreAValueOfTypeStaticDouble() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.DSTORE, 0);
        frame.push(new StaticField("Owner", "Name", "D"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.DOUBLE_VALUE.with(insn), frame.getLocal(0));
        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(1));
    }

    @Test
    public void StoreAValueOfTypeStaticObjectReference() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ASTORE, 0);
        frame.push(new StaticField("Owner", "Name", "Ljava/lang/String;"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.REFERENCE_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void StoreAValueOfTypeStaticArrayReference() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        final VarInsnNode insn = new VarInsnNode(Opcodes.ASTORE, 0);
        frame.push(new StaticField("Owner", "Name", "[I"));
        frame.execute(insn, interpreter);
        Assert.assertEquals(Value.REFERENCE_VALUE.with(insn), frame.getLocal(0));
    }

    @Test
    public void LoadAValueOfTypeInt() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        frame.setLocal(0, Value.INT_VALUE);
        frame.execute(new VarInsnNode(Opcodes.ILOAD, 0), interpreter);
        final Local local = (Local) frame.pop();
        Assert.assertEquals(0, local.var);
        Assert.assertEquals(Type.INT_TYPE, local.type);
        Assert.assertEquals(1, local.getSize());
    }

    @Test
    public void LoadAValueOfTypeFloat() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        frame.setLocal(0, Value.FLOAT_VALUE);
        frame.execute(new VarInsnNode(Opcodes.FLOAD, 0), interpreter);
        final Local local = (Local) frame.pop();
        Assert.assertEquals(0, local.var);
        Assert.assertEquals(Type.FLOAT_TYPE, local.type);
        Assert.assertEquals(1, local.getSize());
    }

    @Test
    public void LoadAValueOfTypeLong() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        frame.setLocal(0, Value.LONG_VALUE);
        frame.setLocal(1, Value.UNINITIALIZED_VALUE);
        frame.execute(new VarInsnNode(Opcodes.LLOAD, 0), interpreter);
        final Local local = (Local) frame.pop();
        Assert.assertEquals(0, local.var);
        Assert.assertEquals(Type.LONG_TYPE, local.type);
        Assert.assertEquals(2, local.getSize());
    }

    @Test
    public void LoadAValueOfTypeDouble() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(2, 1);
        frame.setLocal(0, Value.DOUBLE_VALUE);
        frame.setLocal(1, Value.UNINITIALIZED_VALUE);
        frame.execute(new VarInsnNode(Opcodes.DLOAD, 0), interpreter);
        final Local local = (Local) frame.pop();
        Assert.assertEquals(0, local.var);
        Assert.assertEquals(Type.DOUBLE_TYPE, local.type);
        Assert.assertEquals(2, local.getSize());
    }

    @Test
    public void LoadAValueOfTypeReference() throws AnalyzerException {
        final Frame<Value> frame = new Frame<Value>(1, 1);
        frame.setLocal(0, Value.REFERENCE_VALUE);
        frame.execute(new VarInsnNode(Opcodes.ALOAD, 0), interpreter);
        final Local local = (Local) frame.pop();
        Assert.assertEquals(0, local.var);
        Assert.assertEquals(Type.getObjectType("java/lang/Object"), local.type);
        Assert.assertEquals(1, local.getSize());
    }

}
