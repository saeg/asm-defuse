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

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class ADefUseInterpreterNewOperationShouldThrownAnException {

    private DefUseInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new DefUseInterpreter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenOpcodeIsInvalid() {
        final AbstractInsnNode insn = new InsnNode(Opcodes.DUP);
        interpreter.newOperation(insn);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenLdcTypeSortIsInvalid() {
        final AbstractInsnNode insn = new LdcInsnNode(Type.getType("V"));
        interpreter.newOperation(insn);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenLdcConstantIsInvalid() {
        final AbstractInsnNode insn = new LdcInsnNode(new Object());
        interpreter.newOperation(insn);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void WhenInstructionIsJSR() {
        final AbstractInsnNode insn = new JumpInsnNode(Opcodes.JSR, null);
        interpreter.newOperation(insn);
    }

}
