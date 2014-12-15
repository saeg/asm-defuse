package br.usp.each.saeg.asm.defuse.integration;

import java.util.Collections;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MaxMethodNode extends MethodNode {

    protected MaxMethodNode() {
        super(Opcodes.ASM5);

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
