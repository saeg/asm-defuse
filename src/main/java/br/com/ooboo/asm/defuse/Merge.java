package br.com.ooboo.asm.defuse;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

public class Merge extends Value {

	public final Value value1;

	public final Value value2;

	public Merge(final Type type, final Value value1, final Value value2) {
		super(type);
		this.value1 = value1;
		this.value2 = value2;
	}

	public Merge(final Type type, final Value v, final Value w, final Set<AbstractInsnNode> insns) {
		super(type, insns);
		value1 = v;
		value2 = w;
	}

	@Override
	public Set<Variable> getVariables() {
		final Set<Variable> values = new LinkedHashSet<Variable>();
		values.addAll(value1.getVariables());
		values.addAll(value2.getVariables());
		return Collections.unmodifiableSet(values);
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", value1, value2);
	}

}
