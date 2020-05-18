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

import java.util.Collections;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MaxMethodNode extends MethodNode {

    public MaxMethodNode() {
        super(Opcodes.ASM6);

        // set-up

        desc = "([II)I";
        maxLocals = 4;
        maxStack = 2;
        access = Opcodes.ACC_STATIC;
        tryCatchBlocks = Collections.emptyList();

        // instructions

        /* 00 */instructions.add(new InsnNode(Opcodes.ICONST_0));
        /* 01 */instructions.add(new VarInsnNode(Opcodes.ISTORE, 2));
        /* 02 */instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        /* 03 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        /* 04 */instructions.add(new IincInsnNode(2, 1));
        /* 05 */instructions.add(new InsnNode(Opcodes.IALOAD));
        /* 06 */instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));
        /*    */final LabelNode backLoop = new LabelNode();
        /* 07 */instructions.add(backLoop);
        /* 08 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        /* 09 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        /*    */final LabelNode breakLoop = new LabelNode();
        /* 10 */instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, breakLoop));
        /* 11 */instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        /* 12 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        /* 13 */instructions.add(new InsnNode(Opcodes.IALOAD));
        /* 14 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        /*    */final LabelNode jump = new LabelNode();
        /* 15 */instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLE, jump));
        /* 16 */instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        /* 17 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        /* 18 */instructions.add(new InsnNode(Opcodes.IALOAD));
        /* 19 */instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));
        /* 20 */instructions.add(jump);
        /* 21 */instructions.add(new IincInsnNode(2, 1));
        /* 22 */instructions.add(new JumpInsnNode(Opcodes.GOTO, backLoop));
        /* 23 */instructions.add(breakLoop);
        /* 24 */instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
        /* 25 */instructions.add(new InsnNode(Opcodes.IRETURN));
    }

}
