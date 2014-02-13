package br.com.ooboo.asm.defuse;

public class ArrayUtils {

	private ArrayUtils() {
		// No instances
	}

	public static <T> int indexOf(final T[] array, final Object object) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(object))
				return i;
		}
		return -1;
	}

}
