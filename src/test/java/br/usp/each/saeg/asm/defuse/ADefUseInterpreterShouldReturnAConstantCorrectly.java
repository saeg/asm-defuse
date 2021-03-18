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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterShouldReturnAConstantCorrectly {

    @Parameters
    public static Collection<Object[]> data() {
        final Random rnd = new Random();
        return Arrays.asList(
            new Object[][] {
                    // null
                    { Opcodes.ACONST_NULL, Value.REFERENCE_VALUE, null },
                    // -1, 0, 1, 2, 3, 4, 5
                    { Opcodes.ICONST_M1, Value.INT_VALUE, null },
                    { Opcodes.ICONST_0, Value.INT_VALUE, null },
                    { Opcodes.ICONST_1, Value.INT_VALUE, null },
                    { Opcodes.ICONST_2, Value.INT_VALUE, null },
                    { Opcodes.ICONST_3, Value.INT_VALUE, null },
                    { Opcodes.ICONST_4, Value.INT_VALUE, null },
                    { Opcodes.ICONST_5, Value.INT_VALUE, null },
                    // 0L, 1L
                    { Opcodes.LCONST_0, Value.LONG_VALUE, null },
                    { Opcodes.LCONST_1, Value.LONG_VALUE, null },
                    // 0f, 1f, 2f
                    { Opcodes.FCONST_0, Value.FLOAT_VALUE, null },
                    { Opcodes.FCONST_1, Value.FLOAT_VALUE, null },
                    { Opcodes.FCONST_2, Value.FLOAT_VALUE, null },
                    // 0d, 1d
                    { Opcodes.DCONST_0, Value.DOUBLE_VALUE, null },
                    { Opcodes.DCONST_1, Value.DOUBLE_VALUE, null },
                    // bipush, sipush
                    { Opcodes.BIPUSH, Value.INT_VALUE, null },
                    { Opcodes.SIPUSH, Value.INT_VALUE, null },
                    // ldc Integer and Float
                    { Opcodes.LDC, Value.INT_VALUE, rnd.nextInt() },
                    { Opcodes.LDC, Value.FLOAT_VALUE, rnd.nextFloat() },
                    // ldc Long and Double
                    { Opcodes.LDC, Value.LONG_VALUE, rnd.nextLong() },
                    { Opcodes.LDC, Value.DOUBLE_VALUE, rnd.nextDouble() },
                    // ldc String
                    { Opcodes.LDC, Value.REFERENCE_VALUE, "String" },
                    // object type
                    { Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("Ljava.lang.Object;") },
                    // array type
                    { Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("[I") },
                    // method type
                    { Opcodes.LDC, Value.REFERENCE_VALUE, Type.getType("()I") },
                    // method handle
                    { Opcodes.LDC, Value.REFERENCE_VALUE, new Handle(0, "", "", "", false) }

            }
        );
    }

    private final AbstractInsnNode insn;

    private final Value expected;

    public ADefUseInterpreterShouldReturnAConstantCorrectly(final int opcode, final Value v, final Object arg) {
        if (opcode >= Opcodes.ACONST_NULL && opcode <= Opcodes.DCONST_1) {
            insn = new InsnNode(opcode);
        } else if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
            insn = new IntInsnNode(opcode, opcode);
        } else { // if (opcode == Opcodes.LDC)
            insn = new LdcInsnNode(arg);
        }
        expected = v;
    }

    @Test
    public void AssertThatNewOperationReturnsAConstantCorrectly() {
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final Value op = interpreter.newOperation(insn);
        MatcherAssert.assertThat(op, sameInstance(expected));
    }

}
