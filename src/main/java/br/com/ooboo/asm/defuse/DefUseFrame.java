package br.com.ooboo.asm.defuse;

import org.objectweb.asm.tree.analysis.Frame;

public class DefUseFrame extends Frame<Value> {

	public DefUseFrame(final int nLocals, final int nStack) {
		super(nLocals, nStack);
	}

	public DefUseFrame(final Frame<? extends Value> src) {
		super(src);
	}

}
