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

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterUnaryOperationShouldCastCorrectly {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Opcodes.I2L, Type.INT_TYPE, Type.LONG_TYPE },
                { Opcodes.I2F, Type.INT_TYPE, Type.FLOAT_TYPE },
                { Opcodes.I2D, Type.INT_TYPE, Type.DOUBLE_TYPE },
                { Opcodes.L2I, Type.LONG_TYPE, Type.INT_TYPE },
                { Opcodes.L2F, Type.LONG_TYPE, Type.FLOAT_TYPE },
                { Opcodes.L2D, Type.LONG_TYPE, Type.DOUBLE_TYPE },
                { Opcodes.F2I, Type.FLOAT_TYPE, Type.INT_TYPE },
                { Opcodes.F2L, Type.FLOAT_TYPE, Type.LONG_TYPE },
                { Opcodes.F2D, Type.FLOAT_TYPE, Type.DOUBLE_TYPE },
                { Opcodes.D2I, Type.DOUBLE_TYPE, Type.INT_TYPE },
                { Opcodes.D2L, Type.DOUBLE_TYPE, Type.LONG_TYPE },
                { Opcodes.D2F, Type.DOUBLE_TYPE, Type.FLOAT_TYPE },
                { Opcodes.I2B, Type.INT_TYPE, Type.BYTE_TYPE },
                { Opcodes.I2C, Type.INT_TYPE, Type.CHAR_TYPE },
                { Opcodes.I2S, Type.INT_TYPE, Type.SHORT_TYPE },
                { Opcodes.CHECKCAST, Type.getObjectType("A"), Type.getObjectType("B")}
        });
    }

    private final AbstractInsnNode insn;

    private final Value value;

    private final Type expected;

    public ADefUseInterpreterUnaryOperationShouldCastCorrectly(
            final int opcode, final Type oldType, final Type newType) {
        if (opcode == Opcodes.CHECKCAST) {
            insn = new TypeInsnNode(opcode, newType.getInternalName());
        } else {
            insn = new InsnNode(opcode);
        }
        value = new Value(oldType);
        expected = newType;
    }

    @Test
    public void AssertThatADefUseInterpreterUnaryOperationDoCastCorrectly() {
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final ValueHolder cast = (ValueHolder) interpreter.unaryOperation(insn, value);
        if (insn.getOpcode() == Opcodes.CHECKCAST) {
            Assert.assertEquals(expected, cast.type);
        } else {
            MatcherAssert.assertThat(cast.type, sameInstance(expected));
        }
    }

}
