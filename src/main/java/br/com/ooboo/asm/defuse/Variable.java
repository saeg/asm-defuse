package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.Set;

import org.objectweb.asm.Type;

public class Variable extends Value {

	public static final Variable NONE = new Variable(Type.VOID_TYPE);

	public Variable(final Type type) {
		super(type);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.singleton(this);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

}
