package br.com.ooboo.asm.defuse;

import org.objectweb.asm.Type;

public final class ObjectRef extends Value {

	public ObjectRef(final String desc) {
		super(Type.getType(desc));
		if (type.getSort() != Type.OBJECT) {
			throw new IllegalArgumentException(desc + " is not a object descriptor");
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
