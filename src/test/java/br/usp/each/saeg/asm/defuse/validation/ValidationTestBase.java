/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2016 Roberto Araujo (roberto.andrioli@gmail.com)
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
package br.usp.each.saeg.asm.defuse.validation;

import static br.usp.each.saeg.asm.defuse.validation.ValidationTestUtil.getClassNode;
import static br.usp.each.saeg.asm.defuse.validation.ValidationTestUtil.getMethodNode;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.SourceDefUseAnalyzer;

public abstract class ValidationTestBase {

    private final Class<?> target;

    private final ClassNode cn;

    private final MethodNode mn;

    /**
     * Set-up variables
     */

    private InsnList insns;

    private Source source;

    private int[] lines;

    private int firstLine;

    private List<LocalVariableNode> localVariableNodes;

    private final SourceDefUseAnalyzer analyzer;

    public ValidationTestBase(final Class<?> target, final String name, final String desc) throws IOException {
        this.target = target;
        this.cn = getClassNode(target);
        this.mn = getMethodNode(cn, name, desc);
        this.analyzer = new SourceDefUseAnalyzer();
    }

    @Before
    public void setUp() throws IOException, AnalyzerException {
        analyzer.analyze(cn.name, mn);
        insns = mn.instructions;
        source = Source.getSourceFor(target);
        lines = analyzer.getLines();
        firstLine = lines[0];
        localVariableNodes = mn.localVariables;
    }

    public void assertDefs(final String tag, final String... variables) {
        final int nr = source.getLineNumber(tag);
        for (final String name : variables) {
            Assert.assertTrue(analyzer.getDefs()[nr - firstLine].contains(localDef(name, nr)));
        }
    }

    public void assertReDefs(final String tag, final String... variables) {
        final int nr = source.getLineNumber(tag);
        for (final String name : variables) {
            Assert.assertTrue(analyzer.getDefs()[nr - firstLine].contains(localReDef(name, nr)));
        }
    }

    public void assertUses(final String tag, final String... variables) {
        final int nr = source.getLineNumber(tag);
        for (final String name : variables) {
            Assert.assertTrue(analyzer.getCUses()[nr - firstLine].contains(localReDef(name, nr)));
        }
    }

    public void assertPredicateUses(final String tag, final String... variables) {
        final int nr = source.getLineNumber(tag);
        for (final String name : variables) {
            Assert.assertTrue(analyzer.getPUses()[nr - firstLine].contains(localReDef(name, nr)));
        }
    }

    private Local localDef(final String name, final int line) {
        for (final LocalVariableNode lvn : localVariableNodes) {
            if (lvn.name.equals(name)) {
                final int i = insns.indexOf(lvn.start) - 1;
                if (i == -1 || lines[i] == line) {
                    return new Local(Type.getType(lvn.desc), lvn.index);
                }
            }
        }
        throw new NoSuchElementException("Unknown variable: " + name + " at line " + line);
    }

    private Local localReDef(final String name, final int line) {
        for (final LocalVariableNode lvn : localVariableNodes) {
            if (lvn.name.equals(name)) {
                for (int i = insns.indexOf(lvn.start); i < insns.indexOf(lvn.end); i++) {
                    if (lines[i] == line) {
                        return new Local(Type.getType(lvn.desc), lvn.index);
                    }
                }
            }
        }
        throw new NoSuchElementException("Unknown variable: " + name + " at line " + line);
    }

}
