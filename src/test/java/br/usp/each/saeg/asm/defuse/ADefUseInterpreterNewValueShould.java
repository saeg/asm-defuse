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

import static org.hamcrest.CoreMatchers.sameInstance;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;

public class ADefUseInterpreterNewValueShould {

    private DefUseInterpreter interpreter;

    @Before
    public void setUp() {
        interpreter = new DefUseInterpreter();
    }

    @Test
    public void ReturnUninitializedValueWhenTypeIsNull() {
        final Value v = interpreter.newValue(null);
        MatcherAssert.assertThat(v, sameInstance(Value.UNINITIALIZED_VALUE));
    }

    @Test
    public void ReturnNullWhenTypeIsVoid() {
        final Value v = interpreter.newValue(Type.VOID_TYPE);
        Assert.assertNull(v);
    }

    @Test
    public void ReturnIntWhenTypeIsBoolean() {
        final Value v = interpreter.newValue(Type.BOOLEAN_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.INT_VALUE));
    }

    @Test
    public void ReturnIntWhenTypeIsChar() {
        final Value v = interpreter.newValue(Type.CHAR_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.INT_VALUE));
    }

    @Test
    public void ReturnIntWhenTypeIsByte() {
        final Value v = interpreter.newValue(Type.BYTE_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.INT_VALUE));
    }

    @Test
    public void ReturnIntWhenTypeIsShort() {
        final Value v = interpreter.newValue(Type.SHORT_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.INT_VALUE));
    }

    @Test
    public void ReturnIntWhenTypeIsInt() {
        final Value v = interpreter.newValue(Type.INT_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.INT_VALUE));
    }

    @Test
    public void ReturnFloatWhenTypeIsFloat() {
        final Value v = interpreter.newValue(Type.FLOAT_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.FLOAT_VALUE));
    }

    @Test
    public void ReturnLongWhenTypeIsLong() {
        final Value v = interpreter.newValue(Type.LONG_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.LONG_VALUE));
    }

    @Test
    public void ReturnDoubleWhenTypeIsDouble() {
        final Value v = interpreter.newValue(Type.DOUBLE_TYPE);
        MatcherAssert.assertThat(v, sameInstance(Value.DOUBLE_VALUE));
    }

    @Test
    public void ReturnReferenceWhenTypeIsArray() {
        final Value v = interpreter.newValue(Type.getType("[I"));
        MatcherAssert.assertThat(v, sameInstance(Value.REFERENCE_VALUE));
    }

    @Test
    public void ReturnReferenceWhenTypeIsObject() {
        final Value v = interpreter.newValue(Type.getType("Ljava.lang.String;"));
        MatcherAssert.assertThat(v, sameInstance(Value.REFERENCE_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenTypeIsInvalid() {
        interpreter.newValue(Type.getType("(I)V"));
    }

}
