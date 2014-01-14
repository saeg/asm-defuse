package br.com.ooboo.asm.defuse;

public class DefUseChain {

	public final int def;

	public final int use;

	public final int var;

	public DefUseChain(final int def, final int use, final int var) {
		this.def = def;
		this.use = use;
		this.var = var;
	}

}
