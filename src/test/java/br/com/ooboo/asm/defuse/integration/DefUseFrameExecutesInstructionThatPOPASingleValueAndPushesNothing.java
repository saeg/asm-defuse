package br.com.ooboo.asm.defuse.integration;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Variable;

@RunWith(Parameterized.class)
public class DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing extends
		DefUseFrameExecuteAbstractTest {

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
				{ Opcodes.MONITORENTER },
				{ Opcodes.MONITOREXIT },
				{ Opcodes.IFNULL },
				{ Opcodes.IFNONNULL }
		});
	}

	private Variable variable;

	private final AbstractInsnNode insn;

	@Before
	public void setUp() {
		variable = Mockito.mock(Variable.class);
	}

	public DefUseFrameExecutesInstructionThatPOPASingleValueAndPushesNothing(final int op) {
		super(new DefUseFrame(0, 1));
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
	public void test1() {
		pushValue().thatUseVariables(variable);
		execute(insn);
		assertDef();
		assertUses(variable);
	}

	@Test
	public void test2() {
		pushValue();
		execute(insn);
		assertDef();
		assertUses();
	}

}
