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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A class for testing constants defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class _HelloWorld_Constants_Test {

    /**
     * Asserts the value of {@link HelloWorld#BYTES} constant is equal to the actual number of bytes
     * of "{@code hello, world}" string encoded in {@link StandardCharsets#US_ASCII US-ASCII}
     * character set.
     *
     * @see StandardCharsets#US_ASCII
     * @see String#getBytes(Charset)
     */
    @DisplayName("BYTES == \"hello, world\"getBytes(\"ASCII\").length")
    @Test
    void BYTES_isEqualToActualNumberOfHelloWorldBytes_() {
        var expected = BYTES;
        var charset = US_ASCII;
        var actual = "hello, world".getBytes(charset).length;
        assertEquals(expected, actual);
    }
}
