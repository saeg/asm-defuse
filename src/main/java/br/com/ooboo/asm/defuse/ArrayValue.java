package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public List<Variable> getVariables() {
		final List<Variable> values = new ArrayList<Variable>();
		values.addAll(arref.getVariables());
		values.addAll(index.getVariables());
		return Collections.unmodifiableList(values);
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), arref, index);
	}

}
