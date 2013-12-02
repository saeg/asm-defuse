package br.com.ooboo.asm.defuse;

public final class StaticField extends Field {

	public StaticField(final String owner, final String name, final String desc) {
		super(owner, name, desc);
	}

	@Override
	public String toString() {
		return String.format("S@%s.%s", owner.replace("/", "."), name);
	}

}
