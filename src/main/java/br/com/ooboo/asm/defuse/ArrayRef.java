package br.com.ooboo.asm.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

public class ArrayRef extends Value {

	public final List<? extends Value> counts;

	public ArrayRef(final Type type, final List<? extends Value> counts) {
		super(type);
		if (type.getSort() != Type.ARRAY) {
			throw new IllegalArgumentException("Invalid value type: " + type);
		}
		this.counts = counts;
	}

	public ArrayRef(final Type type, final Value count) {
		this(type, Collections.singletonList(count));
	}

	@Override
	public List<Variable> getVariables() {
		final List<Variable> values = new ArrayList<Variable>();
		for (final Value value : counts) {
			values.addAll(value.getVariables());
		}
		return Collections.unmodifiableList(values);
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("%s%s", getClass().getSimpleName(), counts);
	}

}
