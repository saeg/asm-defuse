/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2014 Roberto Araujo (roberto.andrioli@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.ooboo.asm.defuse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class DefUseAnalyzer extends Analyzer<Value> {

	private final DefUseInterpreter interpreter;

	private DefUseFrame[] duframes;

	private Variable[] variables;

	private Set<Integer>[] successors;

	private Set<Integer>[] predecessors;

	private int[][] bBlocks;

	private int[] leaders;

	private int n;

	public DefUseAnalyzer() {
		this(new DefUseInterpreter());
	}

	private DefUseAnalyzer(final DefUseInterpreter interpreter) {
		super(interpreter);
		this.interpreter = interpreter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Frame<Value>[] analyze(final String owner, final MethodNode m) throws AnalyzerException {

		n = m.instructions.size();
		duframes = new DefUseFrame[n];
		bBlocks = new int[n][];
		leaders = new int[n];
		Arrays.fill(leaders, -1);
		successors = (Set<Integer>[]) new Set<?>[n];
		predecessors = (Set<Integer>[]) new Set<?>[n];
		for (int i = 0; i < n; i++) {
			successors[i] = new LinkedHashSet<Integer>();
			predecessors[i] = new LinkedHashSet<Integer>();
		}

		final Frame<Value>[] frames = super.analyze(owner, m);
		final Set<Variable> vars = new LinkedHashSet<Variable>();

		final Type[] args = Type.getArgumentTypes(m.desc);
		int local = 0;
		if ((m.access & ACC_STATIC) == 0) {
			final Type ctype = Type.getObjectType(owner);
			vars.add(new Local(ctype, local++));
		}
		for (int i = 0; i < args.length; ++i) {
			vars.add(new Local(args[i], local++));
			if (args[i].getSize() == 2) {
				local++;
			}
		}
		final int nargs = (m.access & ACC_STATIC) == 0 ? args.length + 1 : args.length;

		for (int i = 0; i < n; i++) {
			final AbstractInsnNode insn = m.instructions.get(i);
			if (frames[i] == null) {
				duframes[i] = DefUseFrame.NONE;
			} else {
				duframes[i] = new DefUseFrame(frames[i], isPredicate(insn.getOpcode()));
			}
			switch (insn.getType()) {
			case AbstractInsnNode.LABEL:
			case AbstractInsnNode.LINE:
			case AbstractInsnNode.FRAME:
				break;
			default:
				if (duframes[i] != DefUseFrame.NONE) {
					duframes[i].execute(insn, interpreter);
					vars.addAll(duframes[i].getDefinitions());
					vars.addAll(duframes[i].getUses());
				}
				break;
			}
			for (final Variable var : duframes[i].getUses()) {
				if (var instanceof ObjectField) {
					final Value root = ((ObjectField) var).getRoot();
					if (root instanceof Local) {
						final Local l = (Local) root;
						for (final AbstractInsnNode def : duframes[i].getLocal(l.var).insns) {
							final int index = m.instructions.indexOf(def);
							duframes[index].addDef(var);
						}
						if (duframes[i].getLocal(l.var).insns.isEmpty()) {
							duframes[0].addDef(var);
						}
					}
				}
			}
		}
		variables = vars.toArray(new Variable[vars.size()]);

		if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
			return (Frame<Value>[]) new Frame<?>[0];
		}

		for (int i = 0; i < variables.length; i++) {
			final Variable def = variables[i];
			if (i < nargs || def instanceof StaticField) {
				duframes[0].addDef(def);
			}
		}

		final boolean[] queued = new boolean[n];
		final int[] queue = new int[n];
		int top = 0;
		int basicBlock = 0;
		queue[top++] = 0;
		queued[0] = true;
		final IntList list = new IntList();

		while (top > 0) {
			int i = queue[--top];
			leaders[i] = basicBlock;
			list.add(i);
			while (successors[i].size() == 1) {
				final int child = successors[i].iterator().next();
				if (leaders[child] != -1) {
					break;
				}
				if (predecessors[child].size() == 1) {
					i = child;
					leaders[i] = basicBlock;
					list.add(i);
				} else {
					break;
				}
			}
			bBlocks[basicBlock] = list.toArray();
			list.clear();
			basicBlock++;
			for (final int succ : successors[i]) {
				if (!queued[succ]) {
					queue[top++] = succ;
					queued[succ] = true;
				}
			}
		}
		bBlocks = Arrays.copyOf(bBlocks, basicBlock);

		return frames;
	}

	@Override
	protected void newControlFlowEdge(final int insn, final int successor) {
		successors[insn].add(successor);
		predecessors[successor].add(insn);
	}

	@Override
	protected boolean newControlFlowExceptionEdge(final int insn, final int successor) {
		// ignoring exception flow
		return false;
	}

	public DefUseFrame[] getDefUseFrames() {
		return duframes;
	}

	public Variable[] getVariables() {
		return variables;
	}

	public int[] getSuccessors(final int insn) {
		return toArray(successors[insn]);
	}

	public int[][] getSuccessors() {
		final int[][] successors = new int[n][];
		for (int i = 0; i < n; i++) {
			successors[i] = getSuccessors(i);
		}
		return successors;
	}

	public int[] getPredecessors(final int insn) {
		return toArray(predecessors[insn]);
	}

	public int[][] getPredecessors() {
		final int[][] predecessors = new int[n][];
		for (int i = 0; i < n; i++) {
			predecessors[i] = getPredecessors(i);
		}
		return predecessors;
	}

	public int[] getLeaders() {
		return leaders;
	}

	public int[] getBasicBlock(final int id) {
		return bBlocks[id];
	}

	public int[][] getBasicBlocks() {
		return bBlocks;
	}

	private int[] toArray(final Set<Integer> set) {
		final int[] array = new int[set.size()];
		final Iterator<Integer> it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			array[i++] = it.next();
		}
		return array;
	}

	private boolean isPredicate(final int opcode) {
		if (opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE)
			return true;

		if (opcode == Opcodes.TABLESWITCH ||
			opcode == Opcodes.LOOKUPSWITCH ||
			opcode == Opcodes.IFNULL ||
			opcode == Opcodes.IFNONNULL) {
			return true;
		}
		return false;
	}

}
