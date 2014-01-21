package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

	private IntList[] successorsList;
	private int[][] successors;

	private IntList[] predecessorsList;
	private int[][] predecessors;

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
	public Frame<Value>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {

		n = m.instructions.size();
		successors = new int[n][];
		predecessors = new int[n][];
		rdSets = new RDSet[n];
		bBlocks = new int[n][];
		leaders = new int[n];
		Arrays.fill(leaders, -1);
		successorsList = new IntList[n];
		predecessorsList = new IntList[n];
		for (int i = 0; i < n; i++) {
			successorsList[i] = new IntList();
			predecessorsList[i] = new IntList();
		}

		final Frame<Value>[] frames = super.analyze(owner, m);
		final DefUseFrame[] duframes = new DefUseFrame[frames.length];
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
		final int nargs = (m.access & ACC_STATIC) == 0 ? args.length + 1 : args.length + 0;

		AbstractInsnNode insn;

		for (int i = 0; i < n; i++) {
			if (frames[i] == null) {
				duframes[i] = new DefUseFrame(0, 0);
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
				duframes[i].execute(insn, interpreter);
				vars.add(duframes[i].getDefinition());
				vars.addAll(duframes[i].getUses());
				break;
			}
			successors[i] = successorsList[i].toArray();
			predecessors[i] = predecessorsList[i].toArray();
		}
		successorsList = null;
		predecessorsList = null;
		vars.remove(Variable.NONE);
		this.duframes = duframes;
		this.variables = vars.toArray(new Variable[vars.size()]);

		Variable def, other;
		for (int i = 0; i < n; i++) {
			rdSets[i] = new RDSet(n, variables);
			def = duframes[i].getDefinition();
			if (def != Variable.NONE) {
				rdSets[i].gen(i, def);
				for (int j = 0; j < n; j++) {
					other = duframes[j].getDefinition();
					if (i != j && def.equals(other)) {
						rdSets[i].kill(j, def);
					}
				}
			}
		}
		for (int i = 0; i < nargs; i++) {
			def = variables[i];
			rdSets[0].gen(0, def);
			for (int j = 1; j < n; j++) {
				other = duframes[j].getDefinition();
				if (def.equals(other)) {
					rdSets[0].kill(j, def);
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
			while (successors[i].length == 1) {
				if (predecessors[successors[i][0]].length == 1) {
					i = successors[i][0];
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
		successorsList[insn].add(successor);
		predecessorsList[successor].add(insn);
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
		return successors[insn];
	}

	public int[] getPredecessors(final int insn) {
		return predecessors[insn];
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

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("in:").append(printSet(in));
			builder.append("out:").append(printSet(out));
			builder.append("gen:").append(printSet(gen));
			builder.append("kill:").append(printSet(kill));
			return builder.toString();
		}

		private String printSet(final Set<Integer> set) {
			final StringBuilder builder = new StringBuilder();
			builder.append("{ ");
			for (final Integer i : set) {
				final int insn = i / vars.length;
				final int var = i % vars.length;
				builder.append(insn);
				builder.append(':');
				builder.append(vars[var]);
				builder.append(',');
			}
			builder.append(" }\n");
			return builder.toString();
		}
	}

}
