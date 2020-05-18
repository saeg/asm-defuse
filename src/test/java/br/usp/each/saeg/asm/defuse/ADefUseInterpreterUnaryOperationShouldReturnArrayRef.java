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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

@RunWith(Parameterized.class)
public class ADefUseInterpreterUnaryOperationShouldReturnArrayRef {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Opcodes.NEWARRAY, Opcodes.T_BOOLEAN, Type.getType("[Z") },
                { Opcodes.NEWARRAY, Opcodes.T_CHAR, Type.getType("[C") },
                { Opcodes.NEWARRAY, Opcodes.T_BYTE, Type.getType("[B") },
                { Opcodes.NEWARRAY, Opcodes.T_SHORT, Type.getType("[S") },
                { Opcodes.NEWARRAY, Opcodes.T_INT, Type.getType("[I") },
                { Opcodes.NEWARRAY, Opcodes.T_FLOAT, Type.getType("[F") },
                { Opcodes.NEWARRAY, Opcodes.T_DOUBLE, Type.getType("[D") },
                { Opcodes.NEWARRAY, Opcodes.T_LONG, Type.getType("[J") },
                { Opcodes.ANEWARRAY, "java/lang/String", Type.getType("[Ljava/lang/String;") }
        });
    }

    private final AbstractInsnNode insn;

    private final Type expected;

    public ADefUseInterpreterUnaryOperationShouldReturnArrayRef(
            final int opcode, final Object type, final Type expected) {

        if (opcode == Opcodes.NEWARRAY) {
            insn = new IntInsnNode(opcode, (Integer) type);
        } else {
            insn = new TypeInsnNode(opcode, (String) type);
        }
        this.expected = expected;
    }

    @Test
    public void AssertThatADefUseInterpreterUnaryOperationReturnArrayRef() {
        final Value ignore = null;
        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final ArrayRef ref = (ArrayRef) interpreter.unaryOperation(insn, ignore);
        Assert.assertEquals(expected, ref.type);
    }

}
