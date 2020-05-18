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
import org.objectweb.asm.Type;

public class LocalTest {

    private Local local;

    @Before
    public void setUp() {
        local = new Local(Type.INT_TYPE, 1);
    }

    @Test
    public void AttributesAreCorrect() {
        Assert.assertEquals(1, local.var);
    }

    @Test
    public void EqualsASelfReturnTrue() {
        Assert.assertTrue(local.equals(local));
    }

    @Test
    public void EqualsANullReturnsFalse() {
        Assert.assertFalse(local.equals(null));
    }

    @Test
    public void OnEqualsDifferentClassReturnsFalse() {
        final Local other = Mockito.mock(Local.class);
        Assert.assertFalse(local.equals(other));
    }

    @Test
    public void EqualsButDifferentVarReturnFalse() {
        final Local other = new Local(Type.INT_TYPE, 2);
        Assert.assertFalse(local.equals(other));
    }

    @Test
    public void EqualsReturnTrue() {
        final Local other = new Local(Type.INT_TYPE, 1);
        Assert.assertTrue(local.equals(other));
    }

    @Test
    public void EqualsButDifferentVarReturnOtherHash() {
        final Local other = new Local(Type.INT_TYPE, 2);
        Assert.assertNotEquals(local.hashCode(), other.hashCode());
    }

    @Test
    public void EqualsReturnSameHash() {
        final Local other = new Local(Type.INT_TYPE, 1);
        Assert.assertEquals(local.hashCode(), other.hashCode());
    }

    @Test
    public void LocalToString() {
        Assert.assertEquals("L@1", local.toString());
    }

}
