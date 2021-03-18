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

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterCopyOperationShouldReturnALocal {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { Opcodes.ILOAD, Value.INT_VALUE },
            { Opcodes.LLOAD, Value.LONG_VALUE },
            { Opcodes.FLOAD, Value.FLOAT_VALUE },
            { Opcodes.DLOAD, Value.DOUBLE_VALUE },
            { Opcodes.ALOAD, Value.REFERENCE_VALUE }
        });
    }

    private final VarInsnNode insn;

    private final Value value;

    private DefUseInterpreter interpreter;

    public ADefUseInterpreterCopyOperationShouldReturnALocal(final int opcode, final Value value) {
        this.insn = new VarInsnNode(opcode, new Random().nextInt());
        this.value = value;
    }

    @Before
    public void setUp() {
        interpreter = new DefUseInterpreter();
    }

    @Test
    public void AssertThatDefUseInterpreterCopyOperationReturnLocalCorrectly() {
        final Local local = (Local) interpreter.copyOperation(insn, value);
        Assert.assertEquals(insn.var, local.var);
    }

    @Test
    public void AssertThatDefUseInterpreterCopyOperationReturnLocalWithCorrectSize() {
        final Local local = (Local) interpreter.copyOperation(insn, value);
        Assert.assertEquals(value.getSize(), local.getSize());
    }

    @Test
    public void AssertThatDefUseInterpreterCopyOperationReturnLocalWithCorrectType() {
        final Local local = (Local) interpreter.copyOperation(insn, value);
        MatcherAssert.assertThat(local.type, sameInstance(value.type));
    }

}
