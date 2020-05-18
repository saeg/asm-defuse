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
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;

public class ObjectFieldTest {

    @Test
    public void HaveAReferenceValue() {
        final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", Value.REFERENCE_VALUE);
        Assert.assertNotNull(ofield.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenReferenceValueIsNotObject() {
        new ObjectField("pkg/Owner", "Name", "I", Value.INT_VALUE);
    }

    @Test
    public void ObjectFieldToString() {
        final Value v = Mockito.spy(new Value(Type.getObjectType("java/lang/Object")));
        Mockito.when(v.toString()).thenReturn("value");
        final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", v);
        Assert.assertEquals("value.pkg.Owner.Name", ofield.toString());
    }

    @Test
    public void EqualsButDifferentReferenceValueReturnFalse() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final Value v2 = new Value(Type.getObjectType("java/lang/String"));
        final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
        final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
        Assert.assertFalse(ofield1.equals(ofield2));
    }

    @Test
    public void EqualsReturnTrue() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final Value v2 = new Value(Type.getObjectType("java/lang/Object"));
        final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
        final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
        Assert.assertTrue(ofield1.equals(ofield2));
    }

    @Test
    public void DifferentReturnFalse() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final Value v2 = new Value(Type.getObjectType("java/lang/Object2"));
        final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
        final ObjectField ofield2 = new ObjectField("pkg/Owner2", "Name2", "[I", v2);
        Assert.assertFalse(ofield1.equals(ofield2));
    }

    @Test
    public void EqualsButDifferentReferenceValueReturnOtherHash() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final Value v2 = new Value(Type.getObjectType("java/lang/String"));
        final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
        final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
        Assert.assertNotEquals(ofield1.hashCode(), ofield2.hashCode());
    }

    @Test
    public void EqualsReturnSameHash() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final Value v2 = new Value(Type.getObjectType("java/lang/Object"));
        final ObjectField ofield1 = new ObjectField("pkg/Owner", "Name", "I", v1);
        final ObjectField ofield2 = new ObjectField("pkg/Owner", "Name", "I", v2);
        Assert.assertEquals(ofield1.hashCode(), ofield2.hashCode());
    }

    @Test
    public void VariablesListIsUnmodifiable() {
        final Value v1 = new Value(Type.getObjectType("java/lang/Object"));
        final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", v1);
        assertSuperClasss("UnmodifiableCollection", ofield.getVariables());
    }

    @Test
    public void VariableListContainsVariablesFromReferenceValue() {
        final Variable local = new Local(Type.INT_TYPE, 0);
        final Value ref = Mockito.spy(new Value(Type.getObjectType("pkg/Owner")));
        Mockito.when(ref.getVariables()).thenReturn(Collections.singleton(local));
        final ObjectField ofield = new ObjectField("pkg/Owner", "Name", "I", ref);
        Assert.assertTrue(ofield.getVariables().contains(local));
    }

    @Test
    public void VariableListContainsSelfWhenRootIsALocal() {
        final Variable local = new Local(Type.getObjectType("pkg/Ref"), 0);
        final ObjectField ofield = new ObjectField("pkg/Ref", "name", "I", local);
        Assert.assertTrue(ofield.getVariables().contains(ofield));
    }

    @Test
    public void VariableListContainsSelfWhenRootIsAStaticField() {
        final Variable sfield = new StaticField("Owner", "name", "Lpkg/Ref;");
        final ObjectField ofield = new ObjectField("pkg/Ref", "name", "I", sfield);
        Assert.assertTrue(ofield.getVariables().contains(ofield));
    }

    @Test
    public void VariableListTest() {
        final Variable local = new Local(Type.getObjectType("Ref1"), 0);
        final ObjectField ref1 = new ObjectField("Ref1", "name", "LRef2;", local);
        final ObjectField ref2 = new ObjectField("Ref2", "name", "I", ref1);
        final Set<Variable> variables = ref2.getVariables();
        Assert.assertTrue(variables.contains(local));
        Assert.assertTrue(variables.contains(ref1));
        Assert.assertTrue(variables.contains(ref2));
    }

    private static void assertSuperClasss(final String name, final Object o) {
        Class<?> clazz = o.getClass();
        while (!Object.class.equals(clazz)) {
            if (clazz.getSimpleName().equals(name))
                return;
            clazz = clazz.getSuperclass();
        }
        Assert.fail();
    }

}
