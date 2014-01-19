package br.com.ooboo.asm.defuse;

public class IntList {

	private int val;

	private IntList next;

	public IntList() {
		val = 0;
	}

	private IntList(final int v) {
		val = v;
	}

	public void add(final int v) {
		final IntList currentNext = next;
		val++;
		next = new IntList(v);
		next.next = currentNext;
	}

	public int[] toArray() {
		final int[] values = new int[val];
		IntList current = next;
		for (int i = val - 1; i >= 0; i--) {
			values[i] = current.val;
			current = current.next;
		}
		return values;
	}

	public int length() {
		return val;
	}

	public void clear() {
		IntList x = this;
		val = 0;
		while (x != null) {
			final IntList next = x.next;
			x.next = null;
			x = next;
		}
	}

}
