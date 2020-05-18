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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing extends
        DefUseFrameExecuteAbstractTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Opcodes.IF_ICMPEQ },
                { Opcodes.IF_ICMPNE },
                { Opcodes.IF_ICMPLT },
                { Opcodes.IF_ICMPGE },
                { Opcodes.IF_ICMPGT },
                { Opcodes.IF_ICMPLE },
                { Opcodes.IF_ACMPEQ },
                { Opcodes.IF_ACMPNE }
        });
    }

    private Variable variable1;
    private Variable variable2;

    private final AbstractInsnNode insn;

    @Before
    public void setUp() {
        variable1 = Mockito.mock(Variable.class);
        variable2 = Mockito.mock(Variable.class);
    }

    public DefUseFrameExecutesInstructionThatPOPTwoValuesAndPushesNothing(final int op) {
        super(new DefUseFrame(0, 2));
        insn = new JumpInsnNode(op, null);
    }

    @Test
    public void test1() {
        pushValue();
        pushValue();
        execute(insn);
        assertDef();
        assertUses();
    }

    @Test
    public void test2() {
        pushValue().thatUseVariables(variable1);
        pushValue();
        execute(insn);
        assertDef();
        assertUses(variable1);
    }

    @Test
    public void test3() {
        pushValue();
        pushValue().thatUseVariables(variable2);
        execute(insn);
        assertDef();
        assertUses(variable2);
    }

    @Test
    public void test4() {
        pushValue().thatUseVariables(variable1);
        pushValue().thatUseVariables(variable2);
        execute(insn);
        assertDef();
        assertUses(variable1, variable2);
    }

}
