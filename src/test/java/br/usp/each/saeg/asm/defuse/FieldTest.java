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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FieldTest {

    private Field field;

    @Before
    public void setUp() {
        field = new Field("pkg/Owner", "Name", "[D");
    }

    @Test
    public void AttributesAreCorrect() {
        Assert.assertEquals("pkg/Owner", field.owner);
        Assert.assertEquals("Name", field.name);
        Assert.assertEquals("[D", field.desc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ThrowAnExceptionWhenDescriptorIsInvalid() {
        new StaticField("Owner", "Name", "Desc");
    }

    @Test
    public void EqualsASelfReturnTrue() {
        Assert.assertTrue(field.equals(field));
    }

    @Test
    public void EqualsANullReturnsFalse() {
        Assert.assertFalse(field.equals(null));
    }

    @Test
    public void OnEqualsDifferentClassReturnsFalse() {
        final Field other = Mockito.mock(Field.class);
        Assert.assertFalse(field.equals(other));
    }

    @Test
    public void EqualsButDifferentOwnerReturnFalse() {
        final Field other = new Field("pkg/Owner2", "Name", "[D");
        Assert.assertFalse(field.equals(other));
    }

    @Test
    public void EqualsButDifferentNameReturnFalse() {
        final Field other = new Field("pkg/Owner", "Name2", "[D");
        Assert.assertFalse(field.equals(other));
    }

    @Test
    public void EqualsButDifferentDescriptorReturnFalse() {
        final Field other = new Field("pkg/Owner", "Name", "I");
        Assert.assertFalse(field.equals(other));
    }

    @Test
    public void EqualsReturnTrue() {
        final Field other = new Field("pkg/Owner", "Name", "[D");
        Assert.assertTrue(field.equals(other));
    }

    @Test
    public void EqualsButDifferentOwnerReturnOtherHash() {
        final Field other = new Field("pkg/Owner2", "Name", "[D");
        Assert.assertNotEquals(field.hashCode(), other.hashCode());
    }

    @Test
    public void EqualsButDifferentNameReturnOtherHash() {
        final Field other = new Field("pkg/Owner", "Name2", "[D");
        Assert.assertNotEquals(field.hashCode(), other.hashCode());
    }

    @Test
    public void EqualsButDifferentDescriptorReturnOtherHash() {
        final Field other = new Field("pkg/Owner", "Name", "I");
        Assert.assertNotEquals(field.hashCode(), other.hashCode());
    }

    @Test
    public void EqualsReturnSameHash() {
        final Field other = new Field("pkg/Owner", "Name", "[D");
        Assert.assertEquals(field.hashCode(), other.hashCode());
    }

}
