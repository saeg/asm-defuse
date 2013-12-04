package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.List;

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

	public List<Variable> getVariables() {
		return Collections.emptyList();
	}

	@Override
	public int getSize() {
		return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
	}

}
