package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class DefUseChainAnalyzer {

	private final DefUseAnalyzer analyzer;

	private DefUseFrame[] frames;

	private Variable[] variables;

	private RDSet[] rdSets;

	private DefUseChain[] chains;

	private int n;

	public DefUseChainAnalyzer(final DefUseAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public DefUseChain[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
		analyzer.analyze(owner, m);

		init();
		computeInOut();
		reachingDefinitions();
		computeDefUseChains();

		return chains;
	}

	private void init() {
		frames = analyzer.getDefUseFrames();
		variables = analyzer.getVariables();
		n = frames.length;
	}

	private void computeInOut() {
		rdSets = new RDSet[n];
		for (int i = 0; i < n; i++) {
			rdSets[i] = new RDSet(variables);
			for (final Variable def : frames[i].getDefinitions()) {
				rdSets[i].gen(i, def);
				for (int j = 0; j < n; j++) {
					for (final Variable other : frames[j].getDefinitions()) {
						if (i != j && def.equals(other)) {
							rdSets[i].kill(j, def);
						}
					}
				}
			}
		}
	}

	private void reachingDefinitions() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < n; i++) {

				rdSets[i].in.clear();
				for (final int pred : analyzer.getPredecessors(i)) {
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
	}

	private void computeDefUseChains() {
		final List<DefUseChain> chains = new ArrayList<DefUseChain>();
		for (int i = 0; i < n; i++) {
			for (final Variable use : frames[i].getUses()) {
				for (int j = 0; j < n; j++) {
					if (rdSets[i].in(j, use)) {
						chains.add(new DefUseChain(j, i, indexOf(use)));
					}
				}
			}
		}
		this.chains = chains.toArray(new DefUseChain[chains.size()]);
	}

	private int indexOf(final Variable var) {
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].equals(var))
				return i;
		}
		throw new IllegalStateException("Invalid variable:" + var);
	}

	public RDSet[] getRDSets() {
		return rdSets;
	}

	public DefUseChain[] getDefUseChains() {
		return chains;
	}

	public static class RDSet {

		private final Set<Integer> in;
		private final Set<Integer> out;
		private final Set<Integer> gen;
		private final Set<Integer> kill;

		private final Variable[] vars;

		public RDSet(final Variable[] variables) {
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
