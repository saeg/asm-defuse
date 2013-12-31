package br.com.ooboo.asm.defuse;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.Frame;

public class DefUseAnalyzer extends Analyzer<Value> {

	public DefUseAnalyzer() {
		super(new DefUseInterpreter());
	}

	@Override
	protected Frame<Value> newFrame(final int nLocals, final int nStack) {
		return new DefUseFrame(nLocals, nStack);
	}

	@Override
	protected Frame<Value> newFrame(final Frame<? extends Value> src) {
		return new DefUseFrame(src);
	}

}
