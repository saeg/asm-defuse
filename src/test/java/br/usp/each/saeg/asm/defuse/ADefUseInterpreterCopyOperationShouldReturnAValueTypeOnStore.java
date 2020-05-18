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

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnAValueTypeOnStore {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { Opcodes.ISTORE, Type.INT_TYPE, Value.INT_VALUE },
            { Opcodes.LSTORE, Type.LONG_TYPE, Value.LONG_VALUE },
            { Opcodes.FSTORE, Type.FLOAT_TYPE, Value.FLOAT_VALUE },
            { Opcodes.DSTORE, Type.DOUBLE_TYPE, Value.DOUBLE_VALUE },
            { Opcodes.ASTORE, Type.getType("Ljava/lang/Object;"), Value.REFERENCE_VALUE }
        });
    }

    private final AbstractInsnNode insn;

    private final Value value;

    private final Value expected;

    public ADefUseInterpreterCopyOperationShouldReturnAValueTypeOnStore(
            final int opcode, final Type type, final Value expected) {

        this.insn = new VarInsnNode(opcode, new Random().nextInt());
        this.value = new Value(type);
        this.expected = expected;
    }

    @Test
    public void AssertThatDefUseInterpreterCopyOperationReturnValueTypeCorrectly() {
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final Value copy = interpreter.copyOperation(insn, value);
        Assert.assertEquals(expected.with(insn), copy);
    }

}
