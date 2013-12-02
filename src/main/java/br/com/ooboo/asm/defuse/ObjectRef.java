package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

public final class ObjectRef implements Value {

	public final String desc;

	public ObjectRef(final String desc) {
		this.desc = desc;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), desc);
	}

}
