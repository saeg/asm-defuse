package br.com.ooboo.asm.defuse;

import static org.hamcrest.CoreMatchers.sameInstance;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
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

	@Test(expected = IllegalArgumentException.class)
	public void CopyOperationShouldThrowAnExceptionWhenOpcodeIsInvalid() {
		final TypeInsnNode insn = new TypeInsnNode(Opcodes.NEW, "Ljava/lang/String;");
		interpreter.copyOperation(insn, null);
	}

	@Test
	public void UnaryOperationShouldReturnSameValueWhenOpcodeIsINEG() {
		final InsnNode insn = new InsnNode(Opcodes.INEG);
		final Value value = new Value(Type.INT_TYPE);
		final Value op = interpreter.unaryOperation(insn, value);
		Assert.assertThat(op, sameInstance(value));
	}

	@Test
	public void UnaryOperationShouldReturnSameValueWhenOpcodeIsLNEG() {
		final InsnNode insn = new InsnNode(Opcodes.LNEG);
		final Value value = new Value(Type.LONG_TYPE);
		final Value op = interpreter.unaryOperation(insn, value);
		Assert.assertThat(op, sameInstance(value));
	}

	@Test
	public void UnaryOperationShouldReturnSameValueWhenOpcodeIsFNEG() {
		final InsnNode insn = new InsnNode(Opcodes.FNEG);
		final Value value = new Value(Type.FLOAT_TYPE);
		final Value op = interpreter.unaryOperation(insn, value);
		Assert.assertThat(op, sameInstance(value));
	}

	@Test
	public void UnaryOperationShouldReturnSameValueWhenOpcodeIsDNEG() {
		final InsnNode insn = new InsnNode(Opcodes.DNEG);
		final Value value = new Value(Type.DOUBLE_TYPE);
		final Value op = interpreter.unaryOperation(insn, value);
		Assert.assertThat(op, sameInstance(value));
	}

	@Test
	public void UnaryOperationShouldReturnAIntValueTypeWhenOpcodeIsIINC() {
		final Random rnd = new Random();
		final IincInsnNode insn = new IincInsnNode(rnd.nextInt(), rnd.nextInt());
		final Value op = interpreter.unaryOperation(insn, Value.INT_VALUE);
		Assert.assertThat(op, sameInstance(Value.INT_VALUE));
	}

	@Test
	public void UnaryOperationShouldReturnObjectFieldWhenOpcodeIsGETFIELD() {
		final FieldInsnNode insn = new FieldInsnNode(Opcodes.GETFIELD, "Owner", "Name", "[I");
		final Value value = new Value(Type.getObjectType("Owner"));
		final ObjectField sfield = (ObjectField) interpreter.unaryOperation(insn, value);
		Assert.assertEquals(insn.owner, sfield.owner);
		Assert.assertEquals(insn.name, sfield.name);
		Assert.assertEquals(insn.desc, sfield.desc);
		Assert.assertThat(sfield.value, sameInstance(value));
	}

	@Test(expected = IllegalArgumentException.class)
	public void UnaryOperationShouldThrowAnExceptionWhenOpcodeIsInvalid() {
		final TypeInsnNode insn = new TypeInsnNode(Opcodes.NEW, "Ljava/lang/String;");
		interpreter.unaryOperation(insn, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void UnaryOperationShouldThrowAnExceptionWhenArrayTypeIsInvalid() {
		final IntInsnNode insn = new IntInsnNode(Opcodes.NEWARRAY, -1);
		interpreter.unaryOperation(insn, null);
	}

	@Test
	public void UnaryOperationShouldReturnArrayLengthWhenOpcodeIsARRAYLENGTH() {
		final InsnNode insn = new InsnNode(Opcodes.ARRAYLENGTH);
		final Value value = new Value(Type.getType("[I"));
		final ArrayLength length = (ArrayLength) interpreter.unaryOperation(insn, value);
		Assert.assertEquals(Type.INT_TYPE, length.type);
		Assert.assertEquals(value, length.value);
	}

	@Test
	public void UnaryOperationShouldReturnInstanceOfWhenOpcodeIsINSTANCEOF() {
		final TypeInsnNode insn = new TypeInsnNode(Opcodes.INSTANCEOF, "A");
		final Value value = new Value(Type.getType("LB;"));
		final InstanceOf iof = (InstanceOf) interpreter.unaryOperation(insn, value);
		Assert.assertEquals(Type.INT_TYPE, iof.type);
		Assert.assertEquals(value, iof.value);
	}

}
