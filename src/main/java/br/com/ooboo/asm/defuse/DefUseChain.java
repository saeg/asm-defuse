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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		DefUseChain other = (DefUseChain) obj;

		if (def != other.def || use != other.use || target != other.target || var != other.var) {
			return false;
		}
		return true;
	}

}
