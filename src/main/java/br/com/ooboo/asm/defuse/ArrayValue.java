package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Type;

public class ArrayValue extends Value {

	public final Value arref;

	public final Value index;

	public ArrayValue(final Type type, final Value arref, final Value index) {
		super(type);
		this.arref = arref;
		this.index = index;
	}

	@Override
	public Set<Variable> getVariables() {
		final Set<Variable> values = new LinkedHashSet<Variable>();
		values.addAll(arref.getVariables());
		values.addAll(index.getVariables());
		return Collections.unmodifiableSet(values);
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", arref, index);
	}

}
