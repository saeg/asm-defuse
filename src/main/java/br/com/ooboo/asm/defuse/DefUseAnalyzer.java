package br.com.ooboo.asm.defuse;

import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class DefUseAnalyzer extends Analyzer<Value> {

	private final DefUseInterpreter interpreter;

	private DefUseFrame[] duframes;

	private Variable[] variables;

	public DefUseAnalyzer() {
		this(new DefUseInterpreter());
	}

	private DefUseAnalyzer(final DefUseInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
	}

	@Override
	public Frame<Value>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {

		final Frame<Value>[] frames = super.analyze(owner, m);
		final DefUseFrame[] duframes = new DefUseFrame[frames.length];
		final Set<Variable> variables = new LinkedHashSet<Variable>();

		AbstractInsnNode insn;

		for (int i = 0; i < frames.length; i++) {
			duframes[i] = new DefUseFrame(frames[i]);
			insn = m.instructions.get(i);
			switch (insn.getType()) {
			case AbstractInsnNode.LABEL:
			case AbstractInsnNode.LINE:
			case AbstractInsnNode.FRAME:
				break;
			default:
				duframes[i].execute(m.instructions.get(i), interpreter);
				variables.add(duframes[i].getDefinition());
				variables.addAll(duframes[i].getUses());
				break;
			}
		}

		variables.remove(Variable.NONE);
		this.duframes = duframes;
		this.variables = variables.toArray(new Variable[variables.size()]);
		return frames;
	}

	public DefUseFrame[] getDefUseFrames() {
		return duframes;
	}

	public Variable[] getVariables() {
		return variables;
	}

}
