package br.com.ooboo.asm.defuse.integration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Local;
import br.com.ooboo.asm.defuse.ObjectField;
import br.com.ooboo.asm.defuse.StaticField;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseFrameExecuteDefUseInterpreter extends DefUseFrameExecuteAbstractTest {

	private Value value;
	private Value obj;
	private Variable variable1;
	private Variable variable2;

	public DefUseFrameExecuteDefUseInterpreter() {
		super(new DefUseFrame(2, 2));
	}

	@Before
	public void setUp() {
		value = new Value(Type.INT_TYPE);
		obj = new Value(Type.getObjectType("java/lang/Object"));
		variable1 = Mockito.mock(Variable.class);
		variable2 = Mockito.mock(Variable.class);
	}

	@Test
	public void StoreTest1() {
		push(value);
		execute(new VarInsnNode(Opcodes.ISTORE, 0));
		assertDef(new Local(value.type, 0));
		assertUses();
	}

	@Test
	public void StoreTest2() {
		push(value).thatUseVariables(variable1);
		execute(new VarInsnNode(Opcodes.ISTORE, 0));
		assertDef(new Local(value.type, 0));
		assertUses(variable1);
	}

	@Test
	public void PutStaticTest1() {
		push(value);
		execute(new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I"));
		assertDef(new StaticField("Owner", "name", "I"));
		assertUses();
	}

	@Test
	public void PutStaticTest2() {
		push(value).thatUseVariables(variable1);
		execute(new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I"));
		assertDef(new StaticField("Owner", "name", "I"));
		assertUses(variable1);
	}

	@Test
	public void PutFieldTest1() {
		obj = push(obj).get();
		push(value);
		execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
		assertDef(new ObjectField("Owner", "name", "I", obj));
		assertUses();
	}

	@Test
	public void PutFieldTest2() {
		obj = push(obj).thatUseVariables(variable1).get();
		push(value);
		execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
		assertDef(new ObjectField("Owner", "name", "I", obj));
		assertUses(variable1);
	}

	@Test
	public void PutFieldTest3() {
		obj = push(obj).get();
		push(value).thatUseVariables(variable2);
		execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
		assertDef(new ObjectField("Owner", "name", "I", obj));
		assertUses(variable2);
	}

	@Test
	public void PutFieldTest4() {
		obj = push(obj).thatUseVariables(variable1).get();
		push(value).thatUseVariables(variable2);
		execute(new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I"));
		assertDef(new ObjectField("Owner", "name", "I", obj));
		assertUses(variable1, variable2);
	}

	@Test
	public void IINCTest() {
		execute(new IincInsnNode(0, 1));
		final Local local = new Local(Type.INT_TYPE, 0);
		assertDef(local);
		assertUses(local);
	}

	@Test
	public void InvokeStaticTest1() {
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "()V"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest2() {
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)V"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest3() {
		push(value).thatUseVariables(variable1);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void InvokeStaticTest4() {
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "()I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest5() {
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest6() {
		push(value).thatUseVariables(variable1);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest7() {
		push(value).thatUseVariables(variable1);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(II)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeStaticTest8() {
		push(value).thatUseVariables(variable1);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(II)V"));
		assertDef();
		assertUses(variable1, variable2);
	}

	@Test
	public void InvokeTest1() {
		push(obj);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()V"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest2() {
		push(obj).thatUseVariables(variable1);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void InvokeTest3() {
		push(obj);
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest4() {
		push(obj).thatUseVariables(variable1);
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V"));
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void InvokeTest5() {
		push(obj);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V"));
		assertDef();
		assertUses(variable2);
	}

	@Test
	public void InvokeTest6() {
		push(obj).thatUseVariables(variable1);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V"));
		assertDef();
		assertUses(variable1, variable2);
	}

	@Test
	public void InvokeTest7() {
		push(obj);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest8() {
		push(obj).thatUseVariables(variable1);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest9() {
		push(obj);
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest10() {
		push(obj).thatUseVariables(variable1);
		push(value);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest11() {
		push(obj);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

	@Test
	public void InvokeTest12() {
		push(obj).thatUseVariables(variable1);
		push(value).thatUseVariables(variable2);
		execute(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I"));
		assertDef();
		assertUses();
	}

}
