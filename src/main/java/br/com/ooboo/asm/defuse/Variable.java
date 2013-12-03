package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

public abstract class Variable extends Value {

	public Variable(final Type type) {
		super(type);
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.singletonList(this);
	}

}
