package br.com.ooboo.asm.defuse.integration;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.DefUseInterpreter;
import br.com.ooboo.asm.defuse.Local;
import br.com.ooboo.asm.defuse.ObjectField;
import br.com.ooboo.asm.defuse.StaticField;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseFrameExecuteDefUseInterpreter {

	private DefUseFrame frame;

	private Value value;
	private Value obj;
	private Variable variable1;
	private Variable variable2;

	private AbstractInsnNode insn;

	@Before
	public void setUp() {
		frame = new DefUseFrame(2, 2);
		value = new Value(Type.INT_TYPE);
		obj = new Value(Type.getObjectType("java/lang/Object"));
		variable1 = mock(Variable.class);
		variable2 = mock(Variable.class);
	}

	@Test
	public void StoreTest1() {
		insn = new VarInsnNode(Opcodes.ISTORE, 0);
		push(value);
		execute();
		Assert.assertEquals(new Local(value.type, 0), frame.getDefinition());
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void StoreTest2() {
		insn = new VarInsnNode(Opcodes.ISTORE, 0);
		push(value, variable1);
		execute();
		Assert.assertEquals(new Local(value.type, 0), frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void PutStaticTest1() {
		insn = new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I");
		push(value);
		execute();
		Assert.assertEquals(new StaticField("Owner", "name", "I"), frame.getDefinition());
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void PutStaticTest2() {
		insn = new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I");
		push(value, variable1);
		execute();
		Assert.assertEquals(new StaticField("Owner", "name", "I"), frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void PutFieldTest1() {
		insn = new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I");
		obj = push(obj);
		push(value);
		execute();
		Assert.assertEquals(new ObjectField("Owner", "name", "I", obj), frame.getDefinition());
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void PutFieldTest2() {
		insn = new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I");
		obj = push(obj, variable1);
		push(value);
		execute();
		Assert.assertEquals(new ObjectField("Owner", "name", "I", obj), frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void PutFieldTest3() {
		insn = new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I");
		obj = push(obj);
		push(value, variable2);
		execute();
		Assert.assertEquals(new ObjectField("Owner", "name", "I", obj), frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void PutFieldTest4() {
		insn = new FieldInsnNode(Opcodes.PUTFIELD, "Owner", "name", "I");
		obj = push(obj, variable1);
		push(value, variable2);
		execute();
		Assert.assertEquals(new ObjectField("Owner", "name", "I", obj), frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void IINCTest() {
		insn = new IincInsnNode(0, 1);
		execute();
		final Local local = new Local(Type.INT_TYPE, 0);
		Assert.assertEquals(local, frame.getDefinition());
		Assert.assertTrue(frame.getUses().contains(local));
	}

	@Test
	public void InvokeStaticTest1() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "()V");
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest2() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)V");
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest3() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)V");
		push(value, variable1);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void InvokeStaticTest4() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "()I");
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest5() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)I");
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest6() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(I)I");
		push(value, variable1);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest7() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(II)I");
		push(value, variable1);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeStaticTest8() {
		insn = new MethodInsnNode(Opcodes.INVOKESTATIC, "Owner", "name", "(II)V");
		push(value, variable1);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void InvokeTest1() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()V");
		push(obj);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest2() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()V");
		push(obj, variable1);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void InvokeTest3() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V");
		push(obj);
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest4() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V");
		push(obj, variable1);
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
	}

	@Test
	public void InvokeTest5() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V");
		push(obj);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void InvokeTest6() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)V");
		push(obj, variable1);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().contains(variable1));
		Assert.assertTrue(frame.getUses().contains(variable2));
	}

	@Test
	public void InvokeTest7() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()I");
		push(obj);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest8() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "()I");
		push(obj, variable1);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest9() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I");
		push(obj);
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest10() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I");
		push(obj, variable1);
		push(value);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest11() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I");
		push(obj);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	@Test
	public void InvokeTest12() {
		insn = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Owner", "name", "(I)I");
		push(obj, variable1);
		push(value, variable2);
		execute();
		Assert.assertThat(frame.getDefinition(), sameInstance(Variable.NONE));
		Assert.assertTrue(frame.getUses().isEmpty());
	}

	// ----------------- //
	// Auxiliary methods //
	// ------------------//

	private Value push(final Value value, final Variable... uses) {

		final Value mock = spy(value);

		final Set<Variable> variables = new HashSet<Variable>();

		if (uses.length > 0) {
			variables.addAll(Arrays.asList(uses));
		}

		when(mock.getVariables()).thenReturn(variables);

		frame.push(mock);

		return mock;

	}

	private void execute() {
		try {
			frame.execute(insn, new DefUseInterpreter());
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

}
