package br.com.ooboo.asm.defuse;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
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
		final AbstractInsnNode insn = new InsnNode(Opcodes.JSR);
		interpreter.newOperation(insn);
	}

}
