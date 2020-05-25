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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ArrayRefTest {

    private Value value;

    private ArrayRef ref;

    @Before
    public void setUp() {
        value = Mockito.mock(Value.class);
        Mockito.when(value.toString()).thenReturn("value");
        Mockito.when(value.getVariables()).thenReturn(new HashSet<Variable>(0));
        ref = new ArrayRef(Type.getType("[I"), value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenTypeIsInvalid() {
        new ArrayRef(Type.getType("I"), value);
    }

    @Test
    public void SizeIsOne() {
        Assert.assertEquals(1, ref.getSize());
    }

    @Test
    public void ArrayRefToString() {
        Assert.assertEquals("ArrayRef[value]", ref.toString());
    }

    @Test
    public void VariableListContainsVariablesFromCounts() {
        final Type aType = Type.getType("I");
        final Variable var1 = new VariableImpl(aType);
        final Variable var2 = new VariableImpl(aType);
        final List<Variable> valueVars = Arrays.asList(var1, var2);
        final ArrayRef arref = new ArrayRef(Type.getType("[I"), valueVars);
        final Set<Variable> vars = arref.getVariables();
        Assert.assertEquals(var1.getVariables().size() + var2.getVariables().size(), vars.size());
        Assert.assertTrue(vars.containsAll(var1.getVariables()));
        Assert.assertTrue(vars.containsAll(var2.getVariables()));
        Assert.assertFalse(vars.isEmpty());
    }

}
