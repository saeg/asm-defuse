package br.com.ooboo.asm.defuse;

public interface DefUseChainSearch {

	public DefUseChain[] search(final DefUseFrame[] frames, final Variable[] variables,
			final int[][] adjacencyListSucc, final int[][] adjacencyListPred);

}
