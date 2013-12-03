package br.com.ooboo.asm.defuse;

import org.objectweb.asm.Type;

public class Local extends Variable {

	public final int var;

	public Local(final Type type, final int var) {
		super(type);
		this.var = var;
	}

	@Override
	public String toString() {
		return String.format("L@%d", var);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + var;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Local other = (Local) obj;

		if (var != other.var)
			return false;

		return true;
	}

}
