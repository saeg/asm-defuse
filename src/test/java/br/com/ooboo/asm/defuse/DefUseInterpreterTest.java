package br.com.ooboo.asm.defuse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class DefUseInterpreterTest {

	private DefUseInterpreter interpreter;

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
	}

	@Test
	public void NewOperationShouldReturnAStaticFieldCorrectly() {
		final FieldInsnNode insn = new FieldInsnNode(Opcodes.GETSTATIC, "Owner", "Name", "[I");
		final StaticField sfield = (StaticField) interpreter.newOperation(insn);
		Assert.assertEquals(insn.owner, sfield.owner);
		Assert.assertEquals(insn.name, sfield.name);
		Assert.assertEquals(insn.desc, sfield.desc);
	}

	@Test
	public void NewOperationShouldReturnAnObjectRefCorrectly() {
		final TypeInsnNode insn = new TypeInsnNode(Opcodes.NEW, "Ljava/lang/String;");
		final ObjectRef ref = (ObjectRef) interpreter.newOperation(insn);
		Assert.assertEquals(insn.desc, ref.type.getDescriptor());
	}

}
