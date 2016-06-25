/**
 * asm-defuse: asm powered by definitions/uses analysis
 * Copyright (c) 2013, 2016 Roberto Araujo (roberto.andrioli@gmail.com)
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
package br.usp.each.saeg.asm.defuse.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Source {

    public static Source getSourceFor(final Class<?> type) throws IOException {
        final String file = "src/test/java/" + type.getName().replace('.', '/') + ".java";
        return new Source(new FileReader(file));
    }

    private static final Pattern TAG_PATTERN = Pattern.compile("\\$line-(.*)\\$");

    private final List<String> lines = new ArrayList<String>();

    private final Map<String, Integer> tags = new HashMap<String, Integer>();

    public Source(final Reader reader) throws IOException {
        final BufferedReader buffer = new BufferedReader(reader);
        for (String l = buffer.readLine(); l != null; l = buffer.readLine()) {
            addLine(l);
        }
        buffer.close();
    }

    private void addLine(final String l) {
        lines.add(l);
        final Matcher m = TAG_PATTERN.matcher(l);
        if (m.find()) {
            final String tag = m.group(1);
            if (tags.put(tag, lines.size()) != null) {
                throw new IllegalArgumentException("Duplicate tag: " + tag);
            }
        }
    }

    public int getLineNumber(final String tag) throws NoSuchElementException {
        final Integer nr = tags.get(tag);
        if (nr == null) {
            throw new NoSuchElementException("Unknown tag: " + tag);
        }
        return nr;
    }

}
