package br.com.ooboo.asm.defuse;

import org.objectweb.asm.Type;

public final class ObjectRef extends Value {

	public ObjectRef(final Type type) {
		super(type);
		if (type.getSort() != Type.OBJECT) {
			throw new IllegalArgumentException(type.getDescriptor() + " is not a object descriptor");
		}
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), type.getClassName());
	}

}
