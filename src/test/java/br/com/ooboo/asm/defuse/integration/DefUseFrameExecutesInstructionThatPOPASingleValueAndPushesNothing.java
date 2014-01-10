package br.com.ooboo.asm.defuse.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.DefUseInterpreter;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ Opcodes.IFEQ },
				{ Opcodes.IFNE },
				{ Opcodes.IFLT },
				{ Opcodes.IFGE },
				{ Opcodes.IFGT },
				{ Opcodes.IFLE },
				{ Opcodes.TABLESWITCH },
				{ Opcodes.LOOKUPSWITCH },
				{ Opcodes.IRETURN },
				{ Opcodes.LRETURN },
				{ Opcodes.FRETURN },
				{ Opcodes.DRETURN },
				{ Opcodes.ARETURN },
				{ Opcodes.ATHROW },
				{ Opcodes.ATHROW },
				{ Opcodes.MONITORENTER },
				{ Opcodes.MONITOREXIT },
				{ Opcodes.IFNULL },
				{ Opcodes.IFNONNULL }
		});
	}

	private DefUseInterpreter interpreter;

	private DefUseFrame frame;

	private Variable variable;

	private final AbstractInsnNode insn;

	@Before
	public void setUp() {
		interpreter = new DefUseInterpreter();
		frame = new DefUseFrame(0, 2);
		variable = new Variable(Type.INT_TYPE);
	}

	public DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing(final int op) {
		switch (op) {
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
		case Opcodes.IFLT:
		case Opcodes.IFGE:
		case Opcodes.IFGT:
		case Opcodes.IFLE:
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
			insn = new JumpInsnNode(op, null);
			break;
		case Opcodes.TABLESWITCH:
			insn = new TableSwitchInsnNode(0, 0, null);
			break;
		case Opcodes.LOOKUPSWITCH:
			insn = new LookupSwitchInsnNode(null, null, null);
			break;
		default:
			insn = new InsnNode(op);
		}
	}

	@Test
	public void test() {
		push();
		execute();
		Assert.assertTrue(frame.getUses().contains(variable));
	}

	// ----------------- //
	// Auxiliary methods //
	// ------------------//

	private void push() {

		final Value mock = mock(Value.class);

		final Set<Variable> variables = new HashSet<Variable>();
		variables.add(variable);

		when(mock.getVariables()).thenReturn(variables);

		frame.push(mock);

	}

	private void execute() {
		try {
			frame.execute(insn, interpreter);
		} catch (final AnalyzerException e) {
			throw new RuntimeException(e);
		}
	}

}
