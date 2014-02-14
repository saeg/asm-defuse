package br.com.ooboo.asm.defuse;

import java.util.Arrays;

public class DefUseChain {

	public final int def;

	public final int use;

	public final int target;

	public final int var;

	public DefUseChain(final int def, final int use, final int var) {
		this.def = def;
		this.use = use;
		this.var = var;
		target = -1;
	}

	public DefUseChain(final int def, final int use, final int target, final int var) {
		this.def = def;
		this.use = use;
		this.target = target;
		this.var = var;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final DefUseChain other = (DefUseChain) obj;

		if (def != other.def || use != other.use || target != other.target || var != other.var) {
			return false;
		}
		return true;
	}

	public static DefUseChain[] globals(final DefUseChain[] chains, 
			final int[] leaders, final int[][] basicBlocks) {

		int count = 0;
		final DefUseChain[] globals = new DefUseChain[chains.length];
		for (final DefUseChain c : chains) {
			boolean global = true;
			if (leaders[c.def] == leaders[c.use]) {
				// definition and use occurs in same basic block
				for (final int i : basicBlocks[leaders[c.def]]) {
					if (i == c.use) {
						// use occurs before definition
						break;
					}
					if (i == c.def) {
						// use occurs after definition
						global = false;
						break;
					}
				}
			}
			if (global)
				globals[count++] = c;
		}
		return Arrays.copyOf(globals, count);
	}

}
