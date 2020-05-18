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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Invoke;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

public class DefUseFrameExecutePOPInstruction extends DefUseFrameExecuteAbstractTest {

    public DefUseFrameExecutePOPInstruction() {
        super(new DefUseFrame(0, 2));
    }

    private Variable variable1;
    private Variable variable2;

    @Before
    public void setUp() {
        variable1 = Mockito.mock(Variable.class);
        variable2 = Mockito.mock(Variable.class);
    }

    @Test
    public void POPRegularValue() {
        push(Value.class, 1).thatUseVariables(variable1);
        POP();
        assertDef();
        assertUses();
    }

    @Test
    public void POPInvokeValue() {
        push(Invoke.class, 1).thatUseVariables(variable1);
        POP();
        assertDef();
        assertUses(variable1);
    }

    @Test
    public void POP2RegularValue() {
        push(Value.class, 2).thatUseVariables(variable1);
        POP2();
        assertDef();
        assertUses();
    }

    @Test
    public void POP2InvokeValue() {
        push(Invoke.class, 2).thatUseVariables(variable1);
        POP2();
        assertDef();
        assertUses(variable1);
    }

    @Test
    public void POP2Case1() {
        push(Value.class, 1).thatUseVariables(variable1);
        push(Value.class, 1).thatUseVariables(variable2);
        POP2();
        assertDef();
        assertUses();
    }

    @Test
    public void POP2Case2() {
        push(Invoke.class, 1).thatUseVariables(variable1);
        push(Value.class, 1).thatUseVariables(variable2);
        POP2();
        assertDef();
        assertUses(variable1);
    }

    @Test
    public void POP2Case3() {
        push(Value.class, 1).thatUseVariables(variable1);
        push(Invoke.class, 1).thatUseVariables(variable2);
        POP2();
        assertDef();
        assertUses(variable2);
    }

    @Test
    public void POP2Case4() {
        push(Invoke.class, 1).thatUseVariables(variable1);
        push(Invoke.class, 1).thatUseVariables(variable2);
        POP2();
        assertDef();
        assertUses(variable1, variable2);
    }

    private void POP() {
        execute(new InsnNode(Opcodes.POP));
    }

    private void POP2() {
        execute(new InsnNode(Opcodes.POP2));
    }

}
