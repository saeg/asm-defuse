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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterBinaryOperationShouldReturnMergeValue {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Opcodes.IADD, Type.INT_TYPE },
                { Opcodes.LADD, Type.LONG_TYPE },
                { Opcodes.FADD, Type.FLOAT_TYPE },
                { Opcodes.DADD, Type.DOUBLE_TYPE },
                { Opcodes.ISUB, Type.INT_TYPE },
                { Opcodes.LSUB, Type.LONG_TYPE },
                { Opcodes.FSUB, Type.FLOAT_TYPE },
                { Opcodes.DSUB, Type.DOUBLE_TYPE },
                { Opcodes.IMUL, Type.INT_TYPE },
                { Opcodes.LMUL, Type.LONG_TYPE },
                { Opcodes.FMUL, Type.FLOAT_TYPE },
                { Opcodes.DMUL, Type.DOUBLE_TYPE },
                { Opcodes.IDIV, Type.INT_TYPE },
                { Opcodes.LDIV, Type.LONG_TYPE },
                { Opcodes.FDIV, Type.FLOAT_TYPE },
                { Opcodes.DDIV, Type.DOUBLE_TYPE },
                { Opcodes.IREM, Type.INT_TYPE },
                { Opcodes.LREM, Type.LONG_TYPE },
                { Opcodes.FREM, Type.FLOAT_TYPE },
                { Opcodes.DREM, Type.DOUBLE_TYPE },
                { Opcodes.ISHL, Type.INT_TYPE },
                { Opcodes.LSHL, Type.LONG_TYPE },
                { Opcodes.ISHR, Type.INT_TYPE },
                { Opcodes.LSHR, Type.LONG_TYPE },
                { Opcodes.IUSHR, Type.INT_TYPE },
                { Opcodes.LUSHR, Type.LONG_TYPE },
                { Opcodes.IAND, Type.INT_TYPE },
                { Opcodes.LAND, Type.LONG_TYPE },
                { Opcodes.IOR, Type.INT_TYPE },
                { Opcodes.LOR, Type.LONG_TYPE },
                { Opcodes.IXOR, Type.INT_TYPE },
                { Opcodes.LXOR, Type.LONG_TYPE },
                { Opcodes.LCMP, Type.INT_TYPE },
                { Opcodes.FCMPL, Type.INT_TYPE },
                { Opcodes.FCMPG, Type.INT_TYPE },
                { Opcodes.DCMPL, Type.INT_TYPE },
                { Opcodes.DCMPG, Type.INT_TYPE }
        });
    }

    private final InsnNode insn;

    private final Type expected;

    public ADefUseInterpreterBinaryOperationShouldReturnMergeValue(final int op, final Type expected) {
        this.insn = new InsnNode(op);
        this.expected = expected;
    }

    @Test
    public void AssertThatADefUseInterpreterBinaryOperationReturnsMergeValueCorrectly() {
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final Merge value = (Merge) interpreter.binaryOperation(insn, null, null);
        Assert.assertEquals(expected, value.type);
    }

}
