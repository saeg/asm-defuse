package br.com.ooboo.asm.defuse;

import java.util.List;

import org.objectweb.asm.Type;

public class InstanceOf extends Value {

	public final Value value;

	public InstanceOf(final Value value) {
		super(Type.INT_TYPE);
		this.value = value;
	}

	@Override
	public List<Variable> getVariables() {
		return value.getVariables();
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", getClass().getSimpleName(), value);
	}

}
