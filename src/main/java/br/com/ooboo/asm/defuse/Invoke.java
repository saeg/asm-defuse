package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

public class Invoke extends Value {

	public final List<? extends Value> values;

	public Invoke(final Type returnType, final List<? extends Value> values) {
		super(returnType);
		this.values = values;
	}

	@Override
	public int getSize() {
		if (type.equals(Type.VOID_TYPE)) {
			return 0;
		}
		return super.getSize();
	}

	@Override
	public List<Variable> getVariables() {
		final List<Variable> values = new ArrayList<Variable>();
		for (final Value value : this.values) {
			values.addAll(value.getVariables());
		}
		return Collections.unmodifiableList(values);
	}

	@Override
	public String toString() {
		return String.format("%s%s", getClass().getSimpleName(), values);
	}

}
