package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public class DefUseInterpreterTest {

	@Test
	public void NewOperationShouldReturnAStaticFieldCorrectly() {
		FieldInsnNode insn = new FieldInsnNode(Opcodes.GETSTATIC, "Owner", "Name", "Desc");
		DefUseInterpreter interpreter = new DefUseInterpreter();
		StaticField sfield = (StaticField) interpreter.newOperation(insn);
		Assert.assertEquals(insn.owner, sfield.owner);
		Assert.assertEquals(insn.name, sfield.name);
		Assert.assertEquals(insn.desc, sfield.desc);
	}

}
