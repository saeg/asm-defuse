package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class DefUseAnalyzer extends Analyzer<Value> {

	private final DefUseInterpreter interpreter;

	private DefUseFrame[] duframes;

	private Variable[] variables;

	private Set<Integer>[] successors;

	private Set<Integer>[] predecessors;

	private RDSet[] rdSets;

	private DefUseChain[] chains;

	private int[][] bBlocks;

	private int[] leaders;

	private int n;

	public DefUseAnalyzer() {
		this(new DefUseInterpreter());
	}

	private DefUseAnalyzer(final DefUseInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Frame<Value>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {

		n = m.instructions.size();
		duframes = new DefUseFrame[n];
		rdSets = new RDSet[n];
		bBlocks = new int[n][];
		leaders = new int[n];
		Arrays.fill(leaders, -1);
		successors = (Set<Integer>[]) new Set<?>[n];
		predecessors = (Set<Integer>[]) new Set<?>[n];
		for (int i = 0; i < n; i++) {
			successors[i] = new LinkedHashSet<Integer>();
			predecessors[i] = new LinkedHashSet<Integer>();
		}

		final Frame<Value>[] frames = super.analyze(owner, m);
		final Set<Variable> vars = new LinkedHashSet<Variable>();

		final Type[] args = Type.getArgumentTypes(m.desc);
		int local = 0;
		if ((m.access & ACC_STATIC) == 0) {
			final Type ctype = Type.getObjectType(owner);
			vars.add(new Local(ctype, local++));
		}
		for (int i = 0; i < args.length; ++i) {
			vars.add(new Local(args[i], local++));
			if (args[i].getSize() == 2) {
				local++;
			}
		}
		final int nargs = (m.access & ACC_STATIC) == 0 ? args.length + 1 : args.length;

		AbstractInsnNode insn;

		for (int i = 0; i < n; i++) {
			if (frames[i] == null) {
				duframes[i] = DefUseFrame.NONE;
			} else {
				duframes[i] = new DefUseFrame(frames[i]);
			}
			insn = m.instructions.get(i);
			switch (insn.getType()) {
			case AbstractInsnNode.LABEL:
			case AbstractInsnNode.LINE:
			case AbstractInsnNode.FRAME:
				break;
			default:
				if (duframes[i] != DefUseFrame.NONE) {
					duframes[i].execute(insn, interpreter);
					vars.addAll(duframes[i].getDefinitions());
					vars.addAll(duframes[i].getUses());
				}
				break;
			}
			for (final Variable var : duframes[i].getUses()) {
				if (var instanceof ObjectField) {
					Value root = ((ObjectField) var).value;
					while (root instanceof ObjectField) {
						root = ((ObjectField) root).value;
					}
					if (root instanceof Local) {
						final Local fieldLocal = (Local) root;
						for (final AbstractInsnNode fieldDef : duframes[i].getLocal(fieldLocal.var).insns) {
							final int index = m.instructions.indexOf(fieldDef);
							duframes[index].addDef(var);
						}
						if (duframes[i].getLocal(fieldLocal.var).insns.isEmpty()) {
							duframes[0].addDef(var);
						}
					}
				}
			}
		}
		variables = vars.toArray(new Variable[vars.size()]);

		if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
			chains = new DefUseChain[0];
			return (Frame<Value>[]) new Frame<?>[0];
		}

		for (int i = 0; i < variables.length; i++) {
			final Variable def = variables[i];
			if (i < nargs || def instanceof StaticField) {
				duframes[0].addDef(def);
			}
		}
		for (int i = 0; i < n; i++) {
			rdSets[i] = new RDSet(n, variables);
			for (final Variable def : duframes[i].getDefinitions()) {
				rdSets[i].gen(i, def);
				for (int j = 0; j < n; j++) {
					for (final Variable other : duframes[j].getDefinitions()) {
						if (i != j && def.equals(other)) {
							rdSets[i].kill(j, def);
						}
					}
				}
			}
		}

		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < n; i++) {

				rdSets[i].in.clear();
				for (final int pred : predecessors[i]) {
					rdSets[i].in.addAll(rdSets[pred].out);
				}

				final Set<Integer> oldout = new HashSet<Integer>(rdSets[i].out);
				final Set<Integer> temp = new HashSet<Integer>(rdSets[i].in);
				temp.removeAll(rdSets[i].kill);
				rdSets[i].out.clear();
				rdSets[i].out.addAll(rdSets[i].gen);
				rdSets[i].out.addAll(temp);

				if (!rdSets[i].out.equals(oldout)) {
					changed = true;
				}
			}
		}

		final List<DefUseChain> chains = new ArrayList<DefUseChain>();
		for (int i = 0; i < n; i++) {
			for (final Variable use : duframes[i].getUses()) {
				for (int j = 0; j < n; j++) {
					if (rdSets[i].in(j, use)) {
						chains.add(new DefUseChain(j, i, indexOf(use)));
					}
				}
			}
		}
		this.chains = chains.toArray(new DefUseChain[chains.size()]);

		final int[] queue = new int[n];
		int top = 0;
		int basicBlock = 0;
		queue[top++] = 0;
		final IntList list = new IntList();

		while (top > 0) {
			int i = queue[--top];
			leaders[i] = basicBlock;
			list.add(i);
			while (successors[i].size() == 1) {
				final int child = successors[i].iterator().next();
				if (predecessors[child].size() == 1) {
					i = child;
					leaders[i] = basicBlock;
					list.add(i);
				} else {
					break;
				}
			}
			bBlocks[basicBlock] = list.toArray();
			list.clear();
			basicBlock++;
			for (final int successor : successors[i]) {
				if (leaders[successor] == -1)
					queue[top++] = successor;
			}
		}
		bBlocks = Arrays.copyOf(bBlocks, basicBlock);

		return frames;
	}

	@Override
	protected void newControlFlowEdge(final int insn, final int successor) {
		successors[insn].add(successor);
		predecessors[successor].add(insn);
	}

	@Override
	protected boolean newControlFlowExceptionEdge(final int insn, final int successor) {
		// ignoring exception flow
		return false;
	}

	public DefUseFrame[] getDefUseFrames() {
		return duframes;
	}

	public Variable[] getVariables() {
		return variables;
	}

	public RDSet[] getRDSets() {
		return rdSets;
	}

	public DefUseChain[] getDefUseChains() {
		return chains;
	}

	public int[] getSuccessors(final int insn) {
		return toArray(successors[insn]);
	}

	public int[] getPredecessors(final int insn) {
		return toArray(predecessors[insn]);
	}

	public int[] getLeaders() {
		return leaders;
	}

	public int[] getBasicBlock(final int id) {
		return bBlocks[id];
	}

	private int indexOf(final Variable var) {
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].equals(var))
				return i;
		}
		throw new IllegalStateException("Invalid variable:" + var);
	}

	private int[] toArray(final Set<Integer> set) {
		final int[] array = new int[set.size()];
		final Iterator<Integer> it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			array[i++] = it.next();
		}
		return array;
	}

	public static class RDSet {

		private final Set<Integer> in;
		private final Set<Integer> out;
		private final Set<Integer> gen;
		private final Set<Integer> kill;

		private final Variable[] vars;

		public RDSet(final int insns, final Variable[] variables) {
			in = new HashSet<Integer>();
			out = new HashSet<Integer>();
			gen = new HashSet<Integer>();
			kill = new HashSet<Integer>();
			vars = variables;
		}

		public Set<Integer> gen() {
			return Collections.unmodifiableSet(gen);
		}

		public void gen(final int insn, final Variable var) {
			gen.add(insn * vars.length + indexOf(var));
		}

		public Set<Integer> kill() {
			return Collections.unmodifiableSet(kill);
		}

		public void kill(final int insn, final Variable var) {
			kill.add(insn * vars.length + indexOf(var));
		}

		public boolean in(final int insn, final Variable var) {
			return in.contains(insn * vars.length + indexOf(var));
		}

		private int indexOf(final Variable var) {
			for (int i = 0; i < vars.length; i++) {
				if (vars[i].equals(var))
					return i;
			}
			throw new IllegalStateException("Invalid variable:" + var);
		}

	}

}
