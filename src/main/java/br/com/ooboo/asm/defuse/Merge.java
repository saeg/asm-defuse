package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public List<Variable> getVariables() {
		final List<Variable> values = new ArrayList<Variable>();
		values.addAll(value1.getVariables());
		values.addAll(value2.getVariables());
		return Collections.unmodifiableList(values);
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", value1, value2);
	}

}
