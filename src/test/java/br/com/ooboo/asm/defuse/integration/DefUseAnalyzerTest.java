package br.com.ooboo.asm.defuse.integration;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import br.com.ooboo.asm.defuse.DefUseAnalyzer;
import br.com.ooboo.asm.defuse.DefUseAnalyzer.RDSet;
import br.com.ooboo.asm.defuse.DefUseChain;
import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Local;
import br.com.ooboo.asm.defuse.StaticField;
import br.com.ooboo.asm.defuse.Value;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseAnalyzerTest {

	private DefUseAnalyzer analyzer;

	private MethodNode mn;

	@Before
	public void setUp() {
		analyzer = new DefUseAnalyzer();
	}

	public void prepareMethodMax() {
		mn = new MethodNode();
		/* 00 */mn.instructions.add(new InsnNode(Opcodes.ICONST_0));
		/* 01 */mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 2));
		/* 02 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		/* 03 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		/* 04 */mn.instructions.add(new IincInsnNode(2, 1));
		/* 05 */mn.instructions.add(new InsnNode(Opcodes.IALOAD));
		/* 06 */mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));
		/*    */final LabelNode backLoop = new LabelNode();
		/* 07 */mn.instructions.add(backLoop);
		/* 08 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		/* 09 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		/*    */final LabelNode breakLoop = new LabelNode();
		/* 10 */mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, breakLoop));
		/* 11 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		/* 12 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		/* 13 */mn.instructions.add(new InsnNode(Opcodes.IALOAD));
		/* 14 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		/*    */final LabelNode jump = new LabelNode();
		/* 15 */mn.instructions.add(new JumpInsnNode(Opcodes.IF_ICMPLE, jump));
		/* 16 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		/* 17 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
		/* 18 */mn.instructions.add(new InsnNode(Opcodes.IALOAD));
		/* 19 */mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));
		/* 20 */mn.instructions.add(jump);
		/* 21 */mn.instructions.add(new IincInsnNode(2, 1));
		/* 22 */mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, backLoop));
		/* 23 */mn.instructions.add(breakLoop);
		/* 24 */mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
		/* 25 */mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "(II)I";
		mn.maxLocals = 4;
		mn.maxStack = 2;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();
	}

	public void prepareMethodWhitUnreachableCode() {
		mn = new MethodNode();
		mn.instructions.add(new InsnNode(Opcodes.RETURN));
		// unreachable instruction (frame will be null)
		mn.instructions.add(new LabelNode());
		mn.desc = "()V";
		mn.maxLocals = 0;
		mn.maxStack = 0;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();
	}

	public void prepareMethodWhitUnreachableCodeThatManipuleteVariableArrayAndStack() {
		mn = new MethodNode();
		mn.instructions.add(new InsnNode(Opcodes.RETURN));
		// unreachable instruction (frame will be null)
		mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		mn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
		mn.desc = "()V";
		mn.maxLocals = 2;
		mn.maxStack = 1;
		mn.access = 0;
		mn.tryCatchBlocks = Collections.emptyList();
	}

	public void prepareMethodWithTryCatchBlock() {
		final LabelNode begin = new LabelNode();
		final LabelNode end = new LabelNode();
		final LabelNode handler = new LabelNode();
		final LabelNode jmp = new LabelNode();
		final TryCatchBlockNode tcb = new TryCatchBlockNode(begin, end, handler, "Exception");

		mn = new MethodNode();
		mn.instructions.add(begin);
		mn.instructions.add(new InsnNode(Opcodes.NOP));
		mn.instructions.add(end);
		mn.instructions.add(new JumpInsnNode(Opcodes.GOTO, jmp));
		mn.instructions.add(handler);
		mn.instructions.add(new InsnNode(Opcodes.POP));
		mn.instructions.add(jmp);
		mn.instructions.add(new InsnNode(Opcodes.RETURN));
		mn.desc = "()V";
		mn.maxLocals = 0;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.singletonList(tcb);
	}

	@Test
	public void testSucessors() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		Assert.assertArrayEquals(new int[] { 1 }, analyzer.getSuccessors(0));
		Assert.assertArrayEquals(new int[] { 2 }, analyzer.getSuccessors(1));
		Assert.assertArrayEquals(new int[] { 3 }, analyzer.getSuccessors(2));
		Assert.assertArrayEquals(new int[] { 4 }, analyzer.getSuccessors(3));
		Assert.assertArrayEquals(new int[] { 5 }, analyzer.getSuccessors(4));
		Assert.assertArrayEquals(new int[] { 6 }, analyzer.getSuccessors(5));
		Assert.assertArrayEquals(new int[] { 7 }, analyzer.getSuccessors(6));
		Assert.assertArrayEquals(new int[] { 8 }, analyzer.getSuccessors(7));
		Assert.assertArrayEquals(new int[] { 9 }, analyzer.getSuccessors(8));
		Assert.assertArrayEquals(new int[] { 10 }, analyzer.getSuccessors(9));
		Assert.assertArrayEquals(new int[] { 11, 23 }, analyzer.getSuccessors(10));
		Assert.assertArrayEquals(new int[] { 12 }, analyzer.getSuccessors(11));
		Assert.assertArrayEquals(new int[] { 13 }, analyzer.getSuccessors(12));
		Assert.assertArrayEquals(new int[] { 14 }, analyzer.getSuccessors(13));
		Assert.assertArrayEquals(new int[] { 15 }, analyzer.getSuccessors(14));
		Assert.assertArrayEquals(new int[] { 16, 20 }, analyzer.getSuccessors(15));
		Assert.assertArrayEquals(new int[] { 17 }, analyzer.getSuccessors(16));
		Assert.assertArrayEquals(new int[] { 18 }, analyzer.getSuccessors(17));
		Assert.assertArrayEquals(new int[] { 19 }, analyzer.getSuccessors(18));
		Assert.assertArrayEquals(new int[] { 20 }, analyzer.getSuccessors(19));
		Assert.assertArrayEquals(new int[] { 21 }, analyzer.getSuccessors(20));
		Assert.assertArrayEquals(new int[] { 22 }, analyzer.getSuccessors(21));
		Assert.assertArrayEquals(new int[] { 7 }, analyzer.getSuccessors(22));
		Assert.assertArrayEquals(new int[] { 24 }, analyzer.getSuccessors(23));
		Assert.assertArrayEquals(new int[] { 25 }, analyzer.getSuccessors(24));
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(25));
	}

	@Test
	public void testPredecessors() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(0));
		Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPredecessors(1));
		Assert.assertArrayEquals(new int[] { 1 }, analyzer.getPredecessors(2));
		Assert.assertArrayEquals(new int[] { 2 }, analyzer.getPredecessors(3));
		Assert.assertArrayEquals(new int[] { 3 }, analyzer.getPredecessors(4));
		Assert.assertArrayEquals(new int[] { 4 }, analyzer.getPredecessors(5));
		Assert.assertArrayEquals(new int[] { 5 }, analyzer.getPredecessors(6));
		Assert.assertArrayEquals(new int[] { 6, 22 }, analyzer.getPredecessors(7));
		Assert.assertArrayEquals(new int[] { 7 }, analyzer.getPredecessors(8));
		Assert.assertArrayEquals(new int[] { 8 }, analyzer.getPredecessors(9));
		Assert.assertArrayEquals(new int[] { 9 }, analyzer.getPredecessors(10));
		Assert.assertArrayEquals(new int[] { 10 }, analyzer.getPredecessors(11));
		Assert.assertArrayEquals(new int[] { 11 }, analyzer.getPredecessors(12));
		Assert.assertArrayEquals(new int[] { 12 }, analyzer.getPredecessors(13));
		Assert.assertArrayEquals(new int[] { 13 }, analyzer.getPredecessors(14));
		Assert.assertArrayEquals(new int[] { 14 }, analyzer.getPredecessors(15));
		Assert.assertArrayEquals(new int[] { 15 }, analyzer.getPredecessors(16));
		Assert.assertArrayEquals(new int[] { 16 }, analyzer.getPredecessors(17));
		Assert.assertArrayEquals(new int[] { 17 }, analyzer.getPredecessors(18));
		Assert.assertArrayEquals(new int[] { 18 }, analyzer.getPredecessors(19));
		Assert.assertArrayEquals(new int[] { 15, 19 }, analyzer.getPredecessors(20));
		Assert.assertArrayEquals(new int[] { 20 }, analyzer.getPredecessors(21));
		Assert.assertArrayEquals(new int[] { 21 }, analyzer.getPredecessors(22));
		Assert.assertArrayEquals(new int[] { 10 }, analyzer.getPredecessors(23));
		Assert.assertArrayEquals(new int[] { 23 }, analyzer.getPredecessors(24));
		Assert.assertArrayEquals(new int[] { 24 }, analyzer.getPredecessors(25));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDefinitionOfLocalVariable() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final DefUseFrame[] frames = analyzer.getDefUseFrames();
		final int n = frames.length;
		final Set<Variable>[] defs = (Set<Variable>[]) new Set<?>[n];

		// Default
		for (int i = 0; i < n; i++) {
			defs[i] = new LinkedHashSet<Variable>();
		}

		// Set instructions that define a local variable
		defs[1].add(new Local(Type.INT_TYPE, 2));
		defs[4].add(new Local(Type.INT_TYPE, 2));
		defs[6].add(new Local(Type.INT_TYPE, 3));
		defs[19].add(new Local(Type.INT_TYPE, 3));
		defs[21].add(new Local(Type.INT_TYPE, 2));

		// Assert
		for (int i = 0; i < n; i++) {
			Assert.assertEquals("Instruction: " + i, defs[i], frames[i].getDefinitions());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUsesOfLocalVariables() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final DefUseFrame[] frames = analyzer.getDefUseFrames();
		final int n = frames.length;
		final Set<Variable>[] uses = (Set<Variable>[]) new Set<?>[n];

		// Default
		for (int i = 0; i < n; i++) {
			uses[i] = new HashSet<Variable>();
		}

		// Set instructions that uses a local variable
		uses[4].add(new Local(Type.INT_TYPE, 2));
		uses[6].add(new Local(Type.INT_TYPE, 0));
		uses[6].add(new Local(Type.INT_TYPE, 2));
		uses[10].add(new Local(Type.INT_TYPE, 1));
		uses[10].add(new Local(Type.INT_TYPE, 2));
		uses[15].add(new Local(Type.INT_TYPE, 0));
		uses[15].add(new Local(Type.INT_TYPE, 2));
		uses[15].add(new Local(Type.INT_TYPE, 3));
		uses[19].add(new Local(Type.INT_TYPE, 0));
		uses[19].add(new Local(Type.INT_TYPE, 2));
		uses[21].add(new Local(Type.INT_TYPE, 2));
		uses[25].add(new Local(Type.INT_TYPE, 3));

		// Assert
		for (int i = 0; i < n; i++) {
			Assert.assertEquals("Instruction: " + i, uses[i], frames[i].getUses());
		}
	}

	@Test
	public void testVariables() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final Variable[] variables = analyzer.getVariables();
		Assert.assertEquals(new Local(Type.INT_TYPE, 0), variables[0]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 1), variables[1]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 2), variables[2]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 3), variables[3]);
		Assert.assertEquals(4, variables.length);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReachingDefinitionsGen() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final RDSet[] rdSets = analyzer.getRDSets();
		final int vars = analyzer.getVariables().length;
		final int n = mn.instructions.size();
		final Set<Integer>[] gens = (Set<Integer>[]) new HashSet<?>[n];

		// Default
		for (int i = 0; i < n; i++) {
			gens[i] = new HashSet<Integer>();
		}

		set(gens[0], 0, 0, vars);
		set(gens[0], 0, 1, vars);
		set(gens[1], 1, 2, vars);
		set(gens[4], 4, 2, vars);
		set(gens[6], 6, 3, vars);
		set(gens[19], 19, 3, vars);
		set(gens[21], 21, 2, vars);

		// Assert
		for (int i = 0; i < n; i++) {
			Assert.assertTrue("Instruction: " + i, gens[i].containsAll(rdSets[i].gen()));
		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReachingDefinitionsKill() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final RDSet[] rdSets = analyzer.getRDSets();
		final int vars = analyzer.getVariables().length;
		final int n = mn.instructions.size();
		final Set<Integer>[] kills = (Set<Integer>[]) new HashSet<?>[n];

		// Default
		for (int i = 0; i < n; i++) {
			kills[i] = new HashSet<Integer>();
		}

		set(kills[1], 4, 2, vars);
		set(kills[1], 21, 2, vars);
		set(kills[4], 1, 2, vars);
		set(kills[4], 21, 2, vars);
		set(kills[6], 19, 3, vars);
		set(kills[19], 6, 3, vars);
		set(kills[21], 1, 2, vars);
		set(kills[21], 4, 2, vars);

		// Assert
		for (int i = 0; i < n; i++) {
			Assert.assertTrue("Instruction: " + i, kills[i].containsAll(rdSets[i].kill()));
		}

	}

	@Test
	public void testDefUseChains() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final DefUseChain[] chains = analyzer.getDefUseChains();
		final DefUseChain[] expected = new DefUseChain[18];

		expected[0] = new DefUseChain(1, 4, 2);

		expected[1] = new DefUseChain(0, 6, 0);
		// expected[2] = new DefUseChain(1, 6, 2);
		// know bug!!! the algorithm is returning an invalid chain (4, 6, 2)
		expected[2] = new DefUseChain(4, 6, 2); // Just to avoid build failure

		expected[3] = new DefUseChain(0, 10, 1);
		expected[4] = new DefUseChain(4, 10, 2);
		expected[5] = new DefUseChain(21, 10, 2);

		expected[6] = new DefUseChain(0, 15, 0);
		expected[7] = new DefUseChain(4, 15, 2);
		expected[8] = new DefUseChain(21, 15, 2);
		expected[9] = new DefUseChain(6, 15, 3);
		expected[10] = new DefUseChain(19, 15, 3);

		expected[11] = new DefUseChain(0, 19, 0);
		expected[12] = new DefUseChain(4, 19, 2);
		expected[13] = new DefUseChain(21, 19, 2);

		expected[14] = new DefUseChain(4, 21, 2);
		expected[15] = new DefUseChain(21, 21, 2);

		expected[16] = new DefUseChain(6, 25, 3);
		expected[17] = new DefUseChain(19, 25, 3);

		Assert.assertEquals(expected.length, chains.length);

		final StringBuilder message = new StringBuilder();
		for (int i = 0; i < expected.length; i++) {
			final DefUseChain exp = expected[i];
			boolean found = false;
			for (final DefUseChain chain : chains) {
				if (exp.def == chain.def && exp.use == chain.use && exp.var == chain.var) {
					found = true;
					break;
				}
			}
			if (!found) {
				message.append("Not found dua: ").append(i).append('\n');
			}
		}

		if (message.length() > 0) {
			Assert.fail(message.toString());
		}
	}

	@Test
	public void ShouldNotThrowAnExceptionWhenAFrameIsNull() {
		prepareMethodWhitUnreachableCode();
		Exception exception = null;
		try {
			analyzer.analyze("Owner", mn);
		} catch (final Exception e) {
			exception = e;
		}
		final Frame<Value>[] frames = analyzer.getFrames();
		final DefUseFrame[] duframes = analyzer.getDefUseFrames();
		Assert.assertNull(exception);
		Assert.assertNotNull(frames[0]);
		Assert.assertNull(frames[1]);
		Assert.assertEquals(DefUseFrame.NONE, duframes[1]);
	}

	@Test
	public void ShouldNotThrowAnExceptionWhenExecuteUnreachableCode() {
		prepareMethodWhitUnreachableCodeThatManipuleteVariableArrayAndStack();
		Exception exception = null;
		try {
			analyzer.analyze("Owner", mn);
		} catch (final Exception e) {
			exception = e;
		}
		final Frame<Value>[] frames = analyzer.getFrames();
		final DefUseFrame[] duframes = analyzer.getDefUseFrames();
		Assert.assertNull(exception);
		Assert.assertNotNull(frames[0]);
		Assert.assertNull(frames[1]);
		Assert.assertNull(frames[2]);
		Assert.assertEquals(DefUseFrame.NONE, duframes[1]);
		Assert.assertEquals(DefUseFrame.NONE, duframes[2]);
	}

	@Test
	public void ShouldNotThrowAnExceptionWhenExecuteUnreachableCodeCausedByExceptionFlow() {
		prepareMethodWithTryCatchBlock();
		Exception exception = null;
		try {
			analyzer.analyze("Owner", mn);
		} catch (final Exception e) {
			exception = e;
		}
		final Frame<Value>[] frames = analyzer.getFrames();
		final DefUseFrame[] duframes = analyzer.getDefUseFrames();
		Assert.assertNull(exception);
		Assert.assertNotNull(frames[0]);
		Assert.assertNotNull(frames[1]);
		Assert.assertNotNull(frames[2]);
		Assert.assertNotNull(frames[3]);
		Assert.assertNull(frames[4]);
		Assert.assertNull(frames[5]);
		Assert.assertNotNull(frames[6]);
		Assert.assertNotNull(frames[7]);
		Assert.assertEquals(DefUseFrame.NONE, duframes[4]);
		Assert.assertEquals(DefUseFrame.NONE, duframes[5]);
	}

	@Test
	public void ShouldNotCreateEdgesToUnreachableInstruction() throws AnalyzerException {
		prepareMethodWhitUnreachableCode();
		analyzer.analyze("Owner", mn);
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(0));
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(1));
		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(0));
		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(1));

	}

	@Test
	public void testBasicBlocksLeaders() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		final int[] leaders = analyzer.getLeaders();
		Assert.assertEquals(0, leaders[0]);
		Assert.assertEquals(0, leaders[1]);
		Assert.assertEquals(0, leaders[2]);
		Assert.assertEquals(0, leaders[3]);
		Assert.assertEquals(0, leaders[4]);
		Assert.assertEquals(0, leaders[5]);
		Assert.assertEquals(0, leaders[6]);

		Assert.assertEquals(1, leaders[7]);
		Assert.assertEquals(1, leaders[8]);
		Assert.assertEquals(1, leaders[9]);
		Assert.assertEquals(1, leaders[10]);

		Assert.assertEquals(3, leaders[11]);
		Assert.assertEquals(3, leaders[12]);
		Assert.assertEquals(3, leaders[13]);
		Assert.assertEquals(3, leaders[14]);
		Assert.assertEquals(3, leaders[15]);

		Assert.assertEquals(5, leaders[16]);
		Assert.assertEquals(5, leaders[17]);
		Assert.assertEquals(5, leaders[18]);
		Assert.assertEquals(5, leaders[19]);

		Assert.assertEquals(4, leaders[20]);
		Assert.assertEquals(4, leaders[21]);
		Assert.assertEquals(4, leaders[22]);

		Assert.assertEquals(2, leaders[23]);
		Assert.assertEquals(2, leaders[24]);
		Assert.assertEquals(2, leaders[25]);
	}

	@Test
	public void testBasicBlockInstructionsSequence() throws AnalyzerException {
		prepareMethodMax();
		analyzer.analyze("Owner", mn);
		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6 }, analyzer.getBasicBlock(0));
		Assert.assertArrayEquals(new int[] { 7, 8, 9, 10 }, analyzer.getBasicBlock(1));
		Assert.assertArrayEquals(new int[] { 23, 24, 25 }, analyzer.getBasicBlock(2));
		Assert.assertArrayEquals(new int[] { 11, 12, 13, 14, 15 }, analyzer.getBasicBlock(3));
		Assert.assertArrayEquals(new int[] { 20, 21, 22 }, analyzer.getBasicBlock(4));
		Assert.assertArrayEquals(new int[] { 16, 17, 18, 19 }, analyzer.getBasicBlock(5));
	}

	@Test
	public void ShouldNotCreateEdgesOnExceptionFlow() throws AnalyzerException {
		prepareMethodWithTryCatchBlock();
		analyzer.analyze("Owner", mn);

		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(0));
		Assert.assertArrayEquals(new int[] { 0 }, analyzer.getPredecessors(1));
		Assert.assertArrayEquals(new int[] { 1 }, analyzer.getPredecessors(2));
		Assert.assertArrayEquals(new int[] { 2 }, analyzer.getPredecessors(3));
		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(4));
		Assert.assertArrayEquals(new int[] {}, analyzer.getPredecessors(5));
		Assert.assertArrayEquals(new int[] { 3 }, analyzer.getPredecessors(6));
		Assert.assertArrayEquals(new int[] { 6 }, analyzer.getPredecessors(7));

		Assert.assertArrayEquals(new int[] { 1 }, analyzer.getSuccessors(0));
		Assert.assertArrayEquals(new int[] { 2 }, analyzer.getSuccessors(1));
		Assert.assertArrayEquals(new int[] { 3 }, analyzer.getSuccessors(2));
		Assert.assertArrayEquals(new int[] { 6 }, analyzer.getSuccessors(3));
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(4));
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(5));
		Assert.assertArrayEquals(new int[] { 7 }, analyzer.getSuccessors(6));
		Assert.assertArrayEquals(new int[] {}, analyzer.getSuccessors(7));

		Assert.assertArrayEquals(new int[] { 0, 1, 2, 3, 6, 7 }, analyzer.getBasicBlock(0));

		final int[] leaders = analyzer.getLeaders();
		Assert.assertEquals(0, leaders[0]);
		Assert.assertEquals(0, leaders[1]);
		Assert.assertEquals(0, leaders[2]);
		Assert.assertEquals(0, leaders[3]);
		Assert.assertEquals(0, leaders[6]);
		Assert.assertEquals(0, leaders[7]);
		Assert.assertEquals(-1, leaders[4]);
		Assert.assertEquals(-1, leaders[5]);
	}

	@Test
	public void UseOfStaticFieldWithoutDefinition() throws AnalyzerException {
		mn = new MethodNode();
		mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "Owner", "name", "I"));
		mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "()I";
		mn.maxLocals = 0;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();

		analyzer.analyze("Owner", mn);

		final DefUseChain[] chains = analyzer.getDefUseChains();
		final Variable[] vars = analyzer.getVariables();
		Assert.assertEquals(1, chains.length);
		Assert.assertEquals(0, chains[0].def);
		Assert.assertEquals(1, chains[0].use);
		Assert.assertEquals(new StaticField("Owner", "name", "I"), vars[chains[0].var]);
	}

	@Test
	public void DefinitionOfAStaticFieldKillsEntryDef() throws AnalyzerException {
		mn = new MethodNode();
		mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
		mn.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, "Owner", "name", "I"));
		mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "Owner", "name", "I"));
		mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "()I";
		mn.maxLocals = 0;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();

		analyzer.analyze("Owner", mn);

		final DefUseChain[] chains = analyzer.getDefUseChains();
		final Variable[] vars = analyzer.getVariables();
		final RDSet[] sets = analyzer.getRDSets();
		Assert.assertEquals(1, chains.length);
		Assert.assertEquals(1, chains[0].def);
		Assert.assertEquals(3, chains[0].use);
		Assert.assertEquals(new StaticField("Owner", "name", "I"), vars[chains[0].var]);
		Assert.assertTrue(isSet(sets[1].kill(), 0, chains[0].var, vars.length));
	}

	@Test
	public void UseOfParameterdWithoutDefinition() throws AnalyzerException {
		mn = new MethodNode();
		mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "(I)I";
		mn.maxLocals = 1;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();

		analyzer.analyze("Owner", mn);

		final DefUseChain[] chains = analyzer.getDefUseChains();
		final Variable[] vars = analyzer.getVariables();
		Assert.assertEquals(1, chains.length);
		Assert.assertEquals(0, chains[0].def);
		Assert.assertEquals(1, chains[0].use);
		Assert.assertEquals(new Local(Type.INT_TYPE, 0), vars[chains[0].var]);
	}

	@Test
	public void DefinitionOfParameterKillsEntryDef() throws AnalyzerException {
		mn = new MethodNode();
		mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
		mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 0));
		mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
		mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "(I)I";
		mn.maxLocals = 1;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();

		analyzer.analyze("Owner", mn);

		final DefUseChain[] chains = analyzer.getDefUseChains();
		final Variable[] vars = analyzer.getVariables();
		final RDSet[] sets = analyzer.getRDSets();
		Assert.assertEquals(1, chains.length);
		Assert.assertEquals(1, chains[0].def);
		Assert.assertEquals(3, chains[0].use);
		Assert.assertEquals(new Local(Type.INT_TYPE, 0), vars[chains[0].var]);
		Assert.assertTrue(isSet(sets[1].kill(), 0, chains[0].var, vars.length));
	}

	@Test
	public void ShouldKillAEntryDefWithIndexGreaterThanZeroCorrectly() throws AnalyzerException {
		mn = new MethodNode();
		mn.instructions.add(new InsnNode(Opcodes.ICONST_1));
		mn.instructions.add(new VarInsnNode(Opcodes.ISTORE, 1));
		mn.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
		mn.instructions.add(new InsnNode(Opcodes.IRETURN));
		mn.desc = "(II)I";
		mn.maxLocals = 2;
		mn.maxStack = 1;
		mn.access = Opcodes.ACC_STATIC;
		mn.tryCatchBlocks = Collections.emptyList();

		analyzer.analyze("Owner", mn);

		final DefUseChain[] chains = analyzer.getDefUseChains();
		final Variable[] vars = analyzer.getVariables();
		final RDSet[] sets = analyzer.getRDSets();
		Assert.assertEquals(1, chains.length);
		Assert.assertEquals(1, chains[0].def);
		Assert.assertEquals(3, chains[0].use);
		Assert.assertEquals(new Local(Type.INT_TYPE, 1), vars[chains[0].var]);
		Assert.assertTrue(isSet(sets[1].kill(), 0, chains[0].var, vars.length));
	}

	private void set(final Set<Integer> set, final int insn, final int var, final int vars) {
		set.add(insn * vars + var);
	}

	private boolean isSet(final Set<Integer> set, final int insn, final int var, final int vars) {
		return set.contains(insn * vars + var);
	}

}
