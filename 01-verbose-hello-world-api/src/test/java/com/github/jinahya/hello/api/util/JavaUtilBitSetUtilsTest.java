package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.miscellaneous.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;
import java.util.*;

/**
 * A class for testing {@link JavaUtilBitSetUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("JavaUtilBitSetUtils")
@Slf4j
class JavaUtilBitSetUtilsTest {

    @DisplayName("should print an empty <BitSet> without error")
    @Test
    void print_EmptyBitSet() {
        final var bitset = new BitSet();
        _Java_Util_BitSet_TestUtils.print(bitset);
    }

    @DisplayName("should print a single-<long> <BitSet> without error")
    @Test
    void print_SingleLong() {
        final var bitset = BitSet.valueOf(new long[] {0xDEADBEEFL});
        _Java_Util_BitSet_TestUtils.print(bitset);
    }

    @DisplayName("should print a multi-<long> <BitSet> without error")
    @Test
    void print_MultipleLongs() {
        final var bitset = BitSet.valueOf(new long[] {0xCAFEBABEL, 0x0123456789ABCDEFL});
        _Java_Util_BitSet_TestUtils.print(bitset);
    }

    @DisplayName("should print a <BitSet> built from <hello, world> bytes without error")
    @Test
    void print_HelloWorldBytes() {
        final var bytes = "hello, world".getBytes(StandardCharsets.US_ASCII);
        final var bitset = BitSet.valueOf(bytes);
        _Java_Util_BitSet_TestUtils.print(bitset);
    }
}
