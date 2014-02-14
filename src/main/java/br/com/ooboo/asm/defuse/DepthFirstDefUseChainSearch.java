package br.com.ooboo.asm.defuse;

import static br.com.ooboo.asm.defuse.ArrayUtils.indexOf;

import java.util.ArrayList;
import java.util.List;

public class DepthFirstDefUseChainSearch implements DefUseChainSearch {

	private DefUseFrame[] frames;

	private Variable[] variables;

	private int[][] successors;

	private int n;

	@Override
	public DefUseChain[] search(final DefUseFrame[] frames, final Variable[] variables,
			final int[][] adjacencyListSucc, final int[][] adjacencyListPred) {

		this.frames = frames;
		this.variables = variables;
		successors = adjacencyListSucc;
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
		final boolean[] queued = new boolean[n];
		final int[] queue = new int[n];
		int top = 0;
		for (final int succ : successors[i]) {
			queue[top++] = succ;
			queued[succ] = true;
		}
		while (top > 0) {

			final int j = queue[--top];

			// is not necessary remove queued mark (since a node is visited only
			// once). We use the queued mark to indicate that a node has already
			// been visited or will be visited soon.

			if (frames[j].getUses().contains(def)) {
				// reaching definition
				if (frames[j].predicate) {
					for (final int succ : successors[j]) {
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

			for (final int succ : successors[j]) {
				if (!queued[succ]) {
					queue[top++] = succ;
					queued[succ] = true;
				}
			}
		}
	}

}
