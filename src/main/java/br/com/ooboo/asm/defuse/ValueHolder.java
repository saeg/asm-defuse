package br.com.ooboo.asm.defuse;

import java.util.Set;

import org.objectweb.asm.Type;

public class ValueHolder extends Value {

	public final Value value;

	public ValueHolder(final Type type, final Value value) {
		super(type);
		this.value = value;
	}

	@Override
	public Set<Variable> getVariables() {
		return value.getVariables();
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", type, value);
	}

}
