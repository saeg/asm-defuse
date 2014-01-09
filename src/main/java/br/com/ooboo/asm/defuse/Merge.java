package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Type;

public class Merge extends Value {

	public final Value value1;

	public final Value value2;

	public Merge(final Type type, final Value value1, final Value value2) {
		super(type);
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public Set<Variable> getVariables() {
		final Set<Variable> values = new LinkedHashSet<Variable>();
		values.addAll(value1.getVariables());
		values.addAll(value2.getVariables());
		return Collections.unmodifiableSet(values);
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", value1, value2);
	}

}
