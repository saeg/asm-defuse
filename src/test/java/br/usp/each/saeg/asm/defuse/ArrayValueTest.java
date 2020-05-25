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
package br.usp.each.saeg.asm.defuse;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ArrayValueTest {

    private static final Type type = Type.getType("I");
    private static final Set<Variable> value1Vars = Collections.singleton((Variable) new VariableImpl(type));
    private static final Set<Variable> value2Vars = Collections.singleton((Variable) new VariableImpl(type));

    private ArrayValue value;

    @Before
    public void setUp() {
        final Value value1 = Mockito.mock(Value.class);
        final Value value2 = Mockito.mock(Value.class);
        Mockito.when(value1.toString()).thenReturn("A");
        Mockito.when(value2.toString()).thenReturn("B");
        Mockito.when(value1.getVariables()).thenReturn(value1Vars);
        Mockito.when(value2.getVariables()).thenReturn(value2Vars);
        value = new ArrayValue(type, value1, value2);
    }

    @Test
    public void ArrayRefToString() {
        Assert.assertEquals("A[B]", value.toString());
    }

    @Test
    public void VariableListContainsVariablesFromReferenceAndIndex() {
        final Set<Variable> vars = value.getVariables();
        Assert.assertEquals(value1Vars.size() + value2Vars.size(), vars.size());
        Assert.assertTrue(vars.containsAll(value1Vars));
        Assert.assertTrue(vars.containsAll(value2Vars));
    }

}
