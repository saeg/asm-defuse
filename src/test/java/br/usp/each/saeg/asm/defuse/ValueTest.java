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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

public class ValueTest {

    private Value value;

    @Before
    public void setUp() {
        value = new Value(Type.INT_TYPE);
    }

    @Test
    public void SizeOfLongIsTwo() {
        final Value value = new Value(Type.LONG_TYPE);
        Assert.assertEquals(2, value.getSize());
    }

    @Test
    public void SizeOfDoubleIsTwo() {
        final Value value = new Value(Type.DOUBLE_TYPE);
        Assert.assertEquals(2, value.getSize());
    }

    @Test
    public void SizeOfVoidIsZero() {
        final Value value = new Value(Type.VOID_TYPE);
        Assert.assertEquals(0, value.getSize());
    }

    @Test
    public void SizeOfUNINITIALIZED_VALUEIsOne() {
        Assert.assertEquals(1, Value.UNINITIALIZED_VALUE.getSize());
    }

    @Test
    public void SizeOfAnyOtherTypeIsOne() {
        Assert.assertEquals(1, new Value(Type.BOOLEAN_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.CHAR_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.BYTE_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.SHORT_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.INT_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.FLOAT_TYPE).getSize());
        Assert.assertEquals(1, new Value(Type.getObjectType("java/lang/String")).getSize());
        Assert.assertEquals(1, new Value(Type.getType("Ljava/lang/String;")).getSize());
        Assert.assertEquals(1, new Value(Type.getType("[I")).getSize());
    }

    @Test
    public void DefinedValuesHaveCorrectType() {
        Assert.assertEquals(null, Value.UNINITIALIZED_VALUE.type);
        Assert.assertEquals(Type.INT_TYPE, Value.INT_VALUE.type);
        Assert.assertEquals(Type.FLOAT_TYPE, Value.FLOAT_VALUE.type);
        Assert.assertEquals(Type.LONG_TYPE, Value.LONG_VALUE.type);
        Assert.assertEquals(Type.DOUBLE_TYPE, Value.DOUBLE_VALUE.type);
        Assert.assertEquals(Type.getObjectType("java/lang/Object"), Value.REFERENCE_VALUE.type);
    }

    @Test
    public void VariablesSetIsEmpty() {
        Assert.assertTrue(value.getVariables().isEmpty());
    }

    @Test
    public void VariablesSetIsUnmodifiable() {
        MatcherAssert.assertThat(value.getVariables(), sameInstance(Collections.EMPTY_SET));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenTypeIsNull() {
        new Value(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenSetOfInstructionsIsNull() {
        new Value(Type.INT_TYPE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenTypeIsAMethodType() {
        new Value(Type.getMethodType("()V"));
    }

    @Test
    public void withReturnANewValue() {
        MatcherAssert.assertThat(value.with(mock(AbstractInsnNode.class)), not(sameInstance(value)));
    }

    @Test
    public void withReturnANewValueWithSameType() {
        Assert.assertEquals(value.type, value.with(mock(AbstractInsnNode.class)).type);
    }

    @Test
    public void EqualsASelfReturnTrue() {
        Assert.assertTrue(value.equals(value));
    }

    @Test
    public void EqualsANullReturnsFalse() {
        Assert.assertFalse(value.equals(null));
    }

    @Test
    public void DifferentClassReturnsFalseOnEquals() {
        final Value other = mock(Value.class);
        Assert.assertFalse(value.equals(other));
    }

    @Test
    public void DifferentSetOfInstructionsReturnsFalseOnEquals() {
        final Value other = new Value(Type.INT_TYPE,
                Collections.singleton(mock(AbstractInsnNode.class)));
        Assert.assertFalse(value.equals(other));
    }

    @Test
    public void EqualsReturnTrue() {
        final Value other = new Value(Type.INT_TYPE);
        Assert.assertTrue(value.equals(other));
    }

    @Test
    public void DifferentTypeReturnFalseOnEquals() {
        final Value other = new Value(Type.LONG_TYPE);
        Assert.assertFalse(value.equals(other));
    }

    @Test
    public void EqualsReturnSameHashCode() {
        final Value other = new Value(Type.INT_TYPE);
        Assert.assertEquals(value.hashCode(), other.hashCode());
    }

    @Test
    public void DifferentTypeReturnDifferentHashCode() {
        final Value other = new Value(Type.LONG_TYPE);
        Assert.assertNotEquals(value.hashCode(), other.hashCode());
    }

    @Test
    public void DifferentSetOfInstructionsReturnDifferentHashCode() {
        final Value other = new Value(Type.INT_TYPE,
                Collections.singleton(mock(AbstractInsnNode.class)));
        Assert.assertNotEquals(value.hashCode(), other.hashCode());
    }

    @Test
    public void EqualsDoNotThrowAnExceptionWhenTypeIsNull() {
        Value.UNINITIALIZED_VALUE.equals(Value.INT_VALUE);
    }

    @Test
    public void HashCodeDoNotThrowAnExceptionWhenTypeIsNull() {
        Value.UNINITIALIZED_VALUE.hashCode();
    }

    @Test
    public void UNINITIALIZED_VALUEValueToString() {
        Assert.assertEquals(".", Value.UNINITIALIZED_VALUE.toString());
    }

    @Test
    public void REFERENCE_VALUEValueToString() {
        Assert.assertEquals("R", Value.REFERENCE_VALUE.toString());
    }

    @Test
    public void ValueToString() {
        final Type type = Type.getObjectType("SomeValue");
        final Value value = new Value(type);
        Assert.assertEquals("LSomeValue;", value.toString());
    }

}
