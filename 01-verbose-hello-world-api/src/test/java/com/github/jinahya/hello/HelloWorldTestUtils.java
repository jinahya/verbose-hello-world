package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@ExtendWith({MockitoExtension.class})
//@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@Slf4j
final class HelloWorldTestUtils {

    static void drawBuffer(ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        var padding = 11;
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%1$" + padding + "s: %2$s%n", "buffer", buffer);
        System.out.printf("%1$" + padding + "s: %2$d%n", "remaining", buffer.remaining());
        System.out.printf("%1$" + padding + "s: %2$b%n", "direct", buffer.isDirect());
        System.out.printf("%1$" + padding + "s: %2$b%n", "hasArray", buffer.hasArray());
        if (buffer.hasArray()) {
            System.out.printf("%1$" + padding + "s: %2$d%n", "arrayOffset", buffer.arrayOffset());
        }
        System.out.println("---------------------------------------------------------------------");

        var arrayOffset = buffer.hasArray() ? buffer.arrayOffset() : 0;
        var pstring = String.format("%1$s(%2$d)", "pos", buffer.position());
        var ppadding = padding + arrayOffset + buffer.position() + 2;
        System.out.printf("%1$" + (ppadding + pstring.length() ) + "s", pstring);

        var lstring = String.format("%1$s(%2$d)", "lim", buffer.limit());
        var lpadding = padding + arrayOffset + buffer.limit() - ppadding + 2;
        System.out.printf("%1$" + (lpadding + 1) + "s", lstring);

        System.out.printf("%n");

        System.out.printf("%1$" + (ppadding + 1) + "c", '↓');
        System.out.printf("%1$" + lpadding + "c", '↓');

        System.out.printf("%n");
        System.out.printf("%1$" + padding + "s: ", "buffer");
        for (int i = 0; i < arrayOffset; i++) {
            System.out.print(' ');
        }
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.print('-');
        }
        System.out.printf(" %1$c cap(%2$d)", '←', buffer.capacity());
        System.out.printf("%n");

        System.out.printf("%n");
        if (buffer.hasArray()) {
            var ostring = String.format("%1$s(%2$d)", "arrayOffset", buffer.arrayOffset());
            var opadding = padding + arrayOffset + 2;
            System.out.printf("%1$" + (opadding + ostring.length()) + "s", ostring);
            System.out.printf("%n");
            System.out.printf("%1$" + (opadding + 1) + "c", '↓');
            System.out.printf("%n");
            var array = buffer.array();
            System.out.printf("%1$" + padding + "s: ", "array");
            for (int i = 0; i < buffer.array().length; i++) {
                System.out.print('-');
            }
            System.out.printf(" %1$c len(%2$d)", '←', array.length);
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------");
    }

    HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
