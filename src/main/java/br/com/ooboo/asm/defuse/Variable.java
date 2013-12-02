package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

public class Variable implements Value {

	public final Type type;

	public Variable(final Type type) {
		this.type = type;
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.singletonList(this);
	}

	@Override
	public int getSize() {
		return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
	}

}
