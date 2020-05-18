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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

public class Value implements org.objectweb.asm.tree.analysis.Value {

    public static final Value UNINITIALIZED_VALUE = new Value();
    public static final Value INT_VALUE = new Value(Type.INT_TYPE);
    public static final Value FLOAT_VALUE = new Value(Type.FLOAT_TYPE);
    public static final Value LONG_VALUE = new Value(Type.LONG_TYPE);
    public static final Value DOUBLE_VALUE = new Value(Type.DOUBLE_TYPE);
    public static final Value REFERENCE_VALUE = new Value(Type.getObjectType("java/lang/Object"));

    public final Type type;

    public final Set<AbstractInsnNode> insns;

    // Private constructor, used only by UNINITIALIZED_VALUE
    private Value() {
        type = null;
        insns = Collections.emptySet();
    }

    public Value(final Type type) {
        this(type, Collections.<AbstractInsnNode> emptySet());
    }

    public Value(final Type type, final Set<AbstractInsnNode> insns) {
        if (type == null) {
            throw new IllegalArgumentException("Type can't be null");
        }
        if (insns == null) {
            throw new IllegalArgumentException("Set of instructions can't be null");
        }
        if (type.getSort() == Type.METHOD) {
            throw new IllegalArgumentException("Type can't be METHOD");
        }
        this.type = type;
        this.insns = insns;
    }

    public Value with(final AbstractInsnNode insn) {
        return new Value(type, Collections.singleton(insn));
    }

    public Set<Variable> getVariables() {
        return Collections.emptySet();
    }

    @Override
    public int getSize() {
        return type != null ? type.getSize() : 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + insns.hashCode();
        return prime * result + ((type == null) ? 0 : type.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final Value other = (Value) obj;

        if (type == null) {
            // if type is null returns false
            // we just have ONE instance with type null (UNINITIALIZED_VALUE)
            // the first command of this method takes care of this case :)
            return false;
        } else if (!type.equals(other.type))
            return false;

        // Don't care about NPE, field *insns* is always different from null.
        if (!insns.equals(other.insns))
            return false;

        return true;
    }

    @Override
    public String toString() {
        if (this == UNINITIALIZED_VALUE) {
            return ".";
        } else if (this == REFERENCE_VALUE) {
            return "R";
        } else {
            return type.getDescriptor();
        }
    }

}
