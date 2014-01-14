package br.com.ooboo.asm.defuse.integration;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.com.ooboo.asm.defuse.DefUseAnalyzer;
import br.com.ooboo.asm.defuse.DefUseAnalyzer.RDSet;
import br.com.ooboo.asm.defuse.DefUseFrame;
import br.com.ooboo.asm.defuse.Local;
import br.com.ooboo.asm.defuse.Variable;

public class DefUseAnalyzerTest {

	private DefUseAnalyzer analyzer;

	private MethodNode mn;

	@Before
	public void setUp() {
		analyzer = new DefUseAnalyzer();
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

	@Test
	public void testSucessors() throws AnalyzerException {
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
	public void testDefinitionOfLocalVariable() throws AnalyzerException {
		analyzer.analyze("Owner", mn);

		final DefUseFrame[] frames = analyzer.getDefUseFrames();
		final int n = frames.length;
		final Variable[] defs = new Variable[n];

		// Default
		for (int i = 0; i < n; i++) {
			defs[i] = Variable.NONE;
		}

		// Set instructions that define a local variable
		defs[1] = new Local(Type.INT_TYPE, 2);
		defs[4] = new Local(Type.INT_TYPE, 2);
		defs[6] = new Local(Type.INT_TYPE, 3);
		defs[19] = new Local(Type.INT_TYPE, 3);
		defs[21] = new Local(Type.INT_TYPE, 2);

		// Assert
		for (int i = 0; i < n; i++) {
			Assert.assertEquals("Instruction: " + i, defs[i], frames[i].getDefinition());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testUsesOfLocalVariables() throws AnalyzerException {
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
		analyzer.analyze("Owner", mn);
		final Variable[] variables = analyzer.getVariables();
		Assert.assertEquals(new Local(Type.INT_TYPE, 0), variables[0]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 1), variables[1]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 2), variables[2]);
		Assert.assertEquals(new Local(Type.INT_TYPE, 3), variables[3]);
		Assert.assertEquals(4, variables.length);
	}

	@Test
	public void testReachingDefinitionsGen() throws AnalyzerException {
		analyzer.analyze("Owner", mn);

		final RDSet[] rdSets = analyzer.getRDSets();
		final int vars = analyzer.getVariables().length;
		final int n = mn.instructions.size();
		final BitSet[] gens = new BitSet[n];

		// Default
		for (int i = 0; i < n; i++) {
			gens[i] = new BitSet(n * vars);
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
			Assert.assertEquals("Instruction: " + i, gens[i], rdSets[i].gen);
		}

	}

	@Test
	public void testReachingDefinitionsKill() throws AnalyzerException {
		analyzer.analyze("Owner", mn);

		final RDSet[] rdSets = analyzer.getRDSets();
		final int vars = analyzer.getVariables().length;
		final int n = mn.instructions.size();
		final BitSet[] kills = new BitSet[n];

		// Default
		for (int i = 0; i < n; i++) {
			kills[i] = new BitSet(n * vars);
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
			Assert.assertEquals("Instruction: " + i, kills[i], rdSets[i].kill);
		}

	}

	private void set(final BitSet set, final int insn, final int var, final int vars) {
		set.set(insn * vars + var);
	}
}
