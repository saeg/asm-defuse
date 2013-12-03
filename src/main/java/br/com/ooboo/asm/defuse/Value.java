package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

public abstract class Value implements org.objectweb.asm.tree.analysis.Value {

	public final Type type;

	public Value(final Type type) {
		this.type = type;
	}

	public List<Variable> getVariables() {
		return Collections.emptyList();
	}

	@Override
	public int getSize() {
		return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
	}

}
