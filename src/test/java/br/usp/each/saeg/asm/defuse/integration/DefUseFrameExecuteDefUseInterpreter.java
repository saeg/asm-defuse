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
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.ObjectField;
import br.usp.each.saeg.asm.defuse.StaticField;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

public class DefUseFrameExecuteDefUseInterpreter extends DefUseFrameExecuteAbstractTest {

    private Value value1;
    private Value value2;
    private Value obj;
    private Variable variable1;
    private Variable variable2;

    public DefUseFrameExecuteDefUseInterpreter() {
        super(new DefUseFrame(2, 2));
    }

    @Before
    public void setUp() {
        value1 = new Value(Type.INT_TYPE);
        value2 = new Value(Type.LONG_TYPE);
        obj = new Value(Type.getObjectType("java/lang/Object"));
        variable1 = Mockito.mock(Variable.class);
        variable2 = Mockito.mock(Variable.class);
    }

    @Test
    public void StoreTest1() {
        push(value1);
        execute(new VarInsnNode(Opcodes.ISTORE, 0));
        assertDef(new Local(value1.type, 0));
        assertUses();
    }

    @Test
    public void StoreTest2() {
        push(value1).thatUseVariables(variable1);
        execute(new VarInsnNode(Opcodes.ISTORE, 0));
        assertDef(new Local(value1.type, 0));
        assertUses(variable1);
    }

    @Test
    public void StoreTest3() {
        push(value2);
        execute(new VarInsnNode(Opcodes.LSTORE, 0));
        assertDef(new Local(value1.type, 0));
        assertUses();
    }

    @Test
    public void StoreTest4() {
        frame.setLocal(0, Value.LONG_VALUE);
        frame.setLocal(1, Value.UNINITIALIZED_VALUE);
        push(value1);
        execute(new VarInsnNode(Opcodes.ISTORE, 1));
        Assert.assertEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(0));
        Assert.assertNotEquals(Value.UNINITIALIZED_VALUE, frame.getLocal(1));
    }

    @Test
    public void StoreTest5() {
        push(value1);
        execute(new VarInsnNode(Opcodes.ISTORE, 1));
        Assert.assertEquals(null, frame.getLocal(0));
        Assert.assertNotEquals(null, frame.getLocal(1));
    }

    @Test
    public void PutStaticTest1() {
        push(value1);
        execute(new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I"));
        assertDef(new StaticField("Owner", "name", "I"));
        assertUses();
    }

    @Test
    public void PutStaticTest2() {
        push(value1).thatUseVariables(variable1);
        execute(new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I"));
        assertDef(new StaticField("Owner", "name", "I"));
        assertUses(variable1);
    }

    @Test
    public void PutFieldTest1() {
        obj = push(obj).get();
        push(value1);
        execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
        assertDef(new ObjectField("Owner", "name", "I", obj));
        assertUses();
    }

    @Test
    public void PutFieldTest2() {
        obj = push(obj).thatUseVariables(variable1).get();
        push(value1);
        execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
        assertDef(new ObjectField("Owner", "name", "I", obj));
        assertUses(variable1);
    }

    @Test
    public void PutFieldTest3() {
        obj = push(obj).get();
        push(value1).thatUseVariables(variable2);
        execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
        assertDef(new ObjectField("Owner", "name", "I", obj));
        assertUses(variable2);
    }

    @Test
    public void PutFieldTest4() {
        obj = push(obj).thatUseVariables(variable1).get();
        push(value1).thatUseVariables(variable2);
        execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
        assertDef(new ObjectField("Owner", "name", "I", obj));
        assertUses(variable1, variable2);
    }

    @Test
    public void IINCTest() {
        execute(new IincInsnNode(0, 1));
        final Local local = new Local(Type.INT_TYPE, 0);
        assertDef(local);
        assertUses(local);
    }

}
