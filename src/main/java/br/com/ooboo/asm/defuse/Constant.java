package br.com.ooboo.asm.defuse;

public final class Constant extends Value {

	public static final Value WORD = new Constant(1);
	public static final Value DWORD = new Constant(2);

	public final int size;

	private Constant(final int size) {
		super(null);
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
