/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2020 Roberto Araujo (roberto.andrioli@gmail.com)
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
package br.usp.each.saeg.asm.defuse.integration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.mockito.Mockito;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

public class DefUseFrameExecuteAbstractTest {

    public final DefUseFrame frame;

    public DefUseFrameExecuteAbstractTest(final DefUseFrame frame) {
        this.frame = frame;
    }

    public ValuePushed pushValue() {
        final Value mock = Mockito.mock(Value.class);
        frame.push(mock);
        return new ValuePushed(mock);
    }

    public ValuePushed push(final Class<? extends Value> clazz, final int size) {
        final Value mock = Mockito.mock(clazz);
        Mockito.when(mock.getSize()).thenReturn(size);
        frame.push(mock);
        return new ValuePushed(mock);
    }

    public ValuePushed push(final Value value) {
        final Value mock = Mockito.spy(value);
        frame.push(mock);
        return new ValuePushed(mock);
    }

    public void execute(final AbstractInsnNode insn) {
        try {
            frame.execute(insn, new DefUseInterpreter());
        } catch (final AnalyzerException e) {
            throw new RuntimeException(e);
        }
    }

    public void assertDef(final Variable... vars) {
        Assert.assertEquals(vars.length, frame.getDefinitions().size());
        for (final Variable variable : vars) {
            Assert.assertTrue(frame.getDefinitions().contains(variable));
        }
    }

    public void assertUses(final Variable... vars) {
        Assert.assertEquals(vars.length, frame.getUses().size());
        for (final Variable variable : vars) {
            Assert.assertTrue(frame.getUses().contains(variable));
        }
    }

    public class ValuePushed {

        private final Value value;

        public ValuePushed(final Value value) {
            this.value = value;
        }

        public ValuePushed thatUseVariables(final Variable... vars) {
            final Set<Variable> variables = new HashSet<Variable>();
            variables.addAll(Arrays.asList(vars));
            Mockito.when(value.getVariables()).thenReturn(variables);
            return this;
        }

        public Value get() {
            return value;
        }

    }

}
