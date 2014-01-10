package br.com.ooboo.asm.defuse;

import org.objectweb.asm.Type;

public class ArrayLength extends ValueHolder {

	public ArrayLength(final Value value) {
		super(Type.INT_TYPE, value);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), value);
	}

}
