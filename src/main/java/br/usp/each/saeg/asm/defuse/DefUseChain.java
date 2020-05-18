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

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

import java.util.LinkedHashSet;
import java.util.List;

public class DefUseChain {

    public final int def;

    public final int use;

    public final int target;

    public final int var;

    public DefUseChain(final int def, final int use, final int var) {
        this.def = def;
        this.use = use;
        this.var = var;
        target = -1;
    }

    public DefUseChain(final int def, final int use, final int target, final int var) {
        this.def = def;
        this.use = use;
        this.target = target;
        this.var = var;
    }

    public boolean isComputationalChain() {
        return target == -1;
    }

    public boolean isPredicateChain() {
        return target != -1;
    }

    @Override
    public String toString() {
        if (isComputationalChain()) {
            return String.format("(%d, %d, %d)", def, use, var);
        } else {
            return String.format("(%d, (%d,%d), %d)", def, use, target, var);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final DefUseChain other = (DefUseChain) obj;

        if (def != other.def || use != other.use || target != other.target || var != other.var) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + def;
        result = prime * result + use;
        result = prime * result + target;
        result = prime * result + var;
        return result;
    }

    public static DefUseChain[] globals(final DefUseChain[] chains,
            final int[] leaders, final int[][] basicBlocks) {

        int count = 0;
        final DefUseChain[] globals = new DefUseChain[chains.length];
        for (final DefUseChain c : chains) {
            if (isGlobal(c, leaders, basicBlocks)) {
                globals[count++] = c;
            }
        }
        return copyOf(globals, count);
    }

    public static DefUseChain[] locals(final DefUseChain[] chains,
            final int[] leaders, final int[][] basicBlocks) {

        int count = 0;
        final DefUseChain[] locals = new DefUseChain[chains.length];
        for (final DefUseChain c : chains) {
            if (isLocal(c, leaders, basicBlocks)) {
                locals[count++] = c;
            }
        }
        return copyOf(locals, count);
    }

    public static DefUseChain[] toBasicBlock(final DefUseChain[] chains,
            final int[] leaders, final int[][] basicBlocks) {

        int count = 0;
        final DefUseChain[] bbChains = new DefUseChain[chains.length];
        for (final DefUseChain c : chains) {
            if (isGlobal(c, leaders, basicBlocks)) {
                bbChains[count++] = toBasicBlock(c, leaders);
            }
        }
        final List<DefUseChain> l = asList(copyOf(bbChains, count));
        return new LinkedHashSet<DefUseChain>(l).toArray(new DefUseChain[0]);
    }

    public static boolean isGlobal(final DefUseChain chain,
            final int[] leaders, final int[][] basicBlocks) {

        boolean global = true;
        if (chain.isComputationalChain() && leaders[chain.def] == leaders[chain.use]) {
            // definition and use occurs in same basic block
            for (final int i : basicBlocks[leaders[chain.def]]) {
                if (i == chain.use) {
                    // use occurs before definition
                    break;
                }
                if (i == chain.def) {
                    // use occurs after definition
                    global = false;
                    break;
                }
            }
        }
        return global;
    }

    public static boolean isLocal(final DefUseChain chain,
            final int[] leaders, final int[][] basicBlocks) {

        return !isGlobal(chain, leaders, basicBlocks);
    }

    public static DefUseChain toBasicBlock(final DefUseChain chain, final int[] leaders) {
        return new DefUseChain(leaders[chain.def], leaders[chain.use],
                chain.isPredicateChain() ? leaders[chain.target] : -1, chain.var);
    }

}
