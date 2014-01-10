package br.com.ooboo.asm.defuse;

import org.objectweb.asm.Type;

public class Cast extends ValueHolder {

	public Cast(final Type type, final Value value) {
		super(type, value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
