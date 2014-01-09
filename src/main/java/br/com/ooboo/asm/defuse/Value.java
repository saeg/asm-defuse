package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.Set;

import org.objectweb.asm.Type;

public class Value implements org.objectweb.asm.tree.analysis.Value {

	public static final Value UNINITIALIZED_VALUE = new Value();
	public static final Value INT_VALUE = new Value(Type.INT_TYPE);
	public static final Value FLOAT_VALUE = new Value(Type.FLOAT_TYPE);
	public static final Value LONG_VALUE = new Value(Type.LONG_TYPE);
	public static final Value DOUBLE_VALUE = new Value(Type.DOUBLE_TYPE);
	public static final Value REFERENCE_VALUE = new Value(Type.getObjectType("java/lang/Object"));

	public final Type type;

	// Private constructor, used only by UNINITIALIZED_VALUE
	private Value() {
		type = null;
	}

	public Value(final Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Type can't be null");
		}
		this.type = type;
	}

	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public int getSize() {
		return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
	}

	@Override
	public int hashCode() {
		return 31 + ((type == null) ? 0 : type.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Value other = (Value) obj;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;

		return true;
	}

	@Override
	public String toString() {
		if (this == UNINITIALIZED_VALUE) {
			return ".";
		} else if (this == REFERENCE_VALUE) {
			return "R";
		} else {
			return type.getDescriptor();
		}
	}

}
