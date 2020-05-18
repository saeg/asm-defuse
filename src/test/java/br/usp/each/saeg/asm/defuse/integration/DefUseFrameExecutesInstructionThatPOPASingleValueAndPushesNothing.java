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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing extends
        DefUseFrameExecuteAbstractTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { Opcodes.IFEQ },
                { Opcodes.IFNE },
                { Opcodes.IFLT },
                { Opcodes.IFGE },
                { Opcodes.IFGT },
                { Opcodes.IFLE },
                { Opcodes.TABLESWITCH },
                { Opcodes.LOOKUPSWITCH },
                { Opcodes.IRETURN },
                { Opcodes.LRETURN },
                { Opcodes.FRETURN },
                { Opcodes.DRETURN },
                { Opcodes.ARETURN },
                { Opcodes.ATHROW },
                { Opcodes.MONITORENTER },
                { Opcodes.MONITOREXIT },
                { Opcodes.IFNULL },
                { Opcodes.IFNONNULL }
        });
    }

    private Variable variable;

    private final AbstractInsnNode insn;

    @Before
    public void setUp() {
        variable = Mockito.mock(Variable.class);
    }

    public DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing(final int op) {
        super(new DefUseFrame(0, 1));
        switch (op) {
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
            insn = new JumpInsnNode(op, null);
            break;
        case Opcodes.TABLESWITCH:
            insn = new TableSwitchInsnNode(0, 0, null);
            break;
        case Opcodes.LOOKUPSWITCH:
            insn = new LookupSwitchInsnNode(null, null, null);
            break;
        default:
            insn = new InsnNode(op);
        }
    }

    @Test
    public void test1() {
        pushValue().thatUseVariables(variable);
        execute(insn);
        assertDef();
        assertUses(variable);
    }

    @Test
    public void test2() {
        pushValue();
        execute(insn);
        assertDef();
        assertUses();
    }

}
