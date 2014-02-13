package br.com.ooboo.asm.defuse;

import static br.com.ooboo.asm.defuse.ArrayUtils.indexOf;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class DepthFirstDefUseChainSearch {

	private final DefUseAnalyzer analyzer;

	private DefUseFrame[] frames;

	private Variable[] variables;

	private int n;

	public DepthFirstDefUseChainSearch(final DefUseAnalyzer analyzer) {
		this.analyzer = analyzer;
	}

	public DefUseChain[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
		analyzer.analyze(owner, m);
		frames = analyzer.getDefUseFrames();
		variables = analyzer.getVariables();
		n = frames.length;

		final List<DefUseChain> list = new ArrayList<DefUseChain>();
		for (int i = 0; i < n; i++) {
			for (final Variable def : frames[i].getDefinitions()) {
				DFS(def, i, list);
			}
		}
		return list.toArray(new DefUseChain[list.size()]);
	}

	/*
	 * The search visits every instruction j which is syntactically reachable
	 * from i by some definition-clear path.
	 */
	private void DFS(final Variable def, final int i, final List<DefUseChain> list) {
		final boolean[] visited = new boolean[n];
		final boolean[] queued = new boolean[n];
		final int[] queue = new int[n];
		int top = 0;
		for (final int succ : analyzer.getSuccessors(i)) {
			queue[top++] = succ;
		}
		while (top > 0) {

			final int j = queue[--top];

			// mark as visited
			visited[j] = true;

			if (frames[j].getUses().contains(def)) {
				// reaching definition
				if (frames[j].predicate) {
					for (final int succ : analyzer.getSuccessors(j)) {
						list.add(new DefUseChain(i, j, succ, indexOf(variables, def)));
					}
				} else {
					list.add(new DefUseChain(i, j, indexOf(variables, def)));
				}
			}
			if (frames[j].getDefinitions().contains(def)) {
				// backtrack
				continue;
			}

			for (final int succ : analyzer.getSuccessors(j)) {
				if (!visited[succ] && !queued[succ]) {
					queue[top++] = succ;
					queued[succ] = true;
				}
			}
		}
	}

}
