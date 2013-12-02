package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

public final class Constant implements Value {

	public static final Value WORD = new Constant(1);
	public static final Value DWORD = new Constant(2);

	public final int size;

	private Constant(final int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
