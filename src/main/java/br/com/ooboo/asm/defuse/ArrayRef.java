package br.com.ooboo.asm.defuse;

import java.util.List;

import org.objectweb.asm.Type;

public class ArrayRef extends Value {

	public final Value count;

	public ArrayRef(final Type type, final Value count) {
		super(type);
		if (type.getSort() != Type.ARRAY) {
			throw new IllegalArgumentException("Invalid value type: " + type);
		}
		this.count = count;
	}

	@Override
	public List<Variable> getVariables() {
		return count.getVariables();
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), count);
	}

}
