package br.com.ooboo.asm.defuse;

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

}
