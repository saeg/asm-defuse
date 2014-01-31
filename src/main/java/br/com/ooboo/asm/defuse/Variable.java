package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.Set;

import org.objectweb.asm.Type;

public class Variable extends Value {

	public Variable(final Type type) {
		super(type);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.singleton(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return this == obj;
	}

}
