package br.com.ooboo.asm.defuse.integration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Invoke;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseFrameExecutePOPInstruction extends DefUseFrameExecuteAbstractTest {

	public DefUseFrameExecutePOPInstruction() {
		super(new DefUseFrame(0, 2));
	}

	private Variable variable1;
	private Variable variable2;

	@Before
	public void setUp() {
		variable1 = Mockito.mock(Variable.class);
		variable2 = Mockito.mock(Variable.class);
	}

	@Test
	public void POPRegularValue() {
		push(Value.class, 1).thatUseVariables(variable1);
		POP();
		assertDef();
		assertUses();
	}

	@Test
	public void POPInvokeValue() {
		push(Invoke.class, 1).thatUseVariables(variable1);
		POP();
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void POP2RegularValue() {
		push(Value.class, 2).thatUseVariables(variable1);
		POP2();
		assertDef();
		assertUses();
	}

	@Test
	public void POP2InvokeValue() {
		push(Invoke.class, 2).thatUseVariables(variable1);
		POP2();
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void POP2Case1() {
		push(Value.class, 1).thatUseVariables(variable1);
		push(Value.class, 1).thatUseVariables(variable2);
		POP2();
		assertDef();
		assertUses();
	}

	@Test
	public void POP2Case2() {
		push(Invoke.class, 1).thatUseVariables(variable1);
		push(Value.class, 1).thatUseVariables(variable2);
		POP2();
		assertDef();
		assertUses(variable1);
	}

	@Test
	public void POP2Case3() {
		push(Value.class, 1).thatUseVariables(variable1);
		push(Invoke.class, 1).thatUseVariables(variable2);
		POP2();
		assertDef();
		assertUses(variable2);
	}

	@Test
	public void POP2Case4() {
		push(Invoke.class, 1).thatUseVariables(variable1);
		push(Invoke.class, 1).thatUseVariables(variable2);
		POP2();
		assertDef();
		assertUses(variable1, variable2);
	}

	private void POP() {
		execute(new InsnNode(Opcodes.POP));
	}

	private void POP2() {
		execute(new InsnNode(Opcodes.POP2));
	}

}
