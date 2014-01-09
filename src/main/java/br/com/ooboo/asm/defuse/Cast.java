package br.com.ooboo.asm.defuse;

import java.util.Set;

import org.objectweb.asm.Type;

public class Cast extends Value {

	public final Value value;

	public Cast(final Type type, final Value value) {
		super(type);
		this.value = value;
	}

	@Override
	public Set<Variable> getVariables() {
		return value.getVariables();
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
