package com.github.jinahya.hello.api._java_util;

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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing {@link HelloWorld#set(BitSet, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(bitset, index)")
@Slf4j
class HelloWorld_Set_BitSet_Index_Test
        extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#set(BitSet, int) set(bitset, index)} method throws a
     * {@link NullPointerException} when the {@code bitset} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <bitset> argument is <null>")
    @Test
    void _ThrowNullPointerException_BitSetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var bitset = (BitSet) null;
        final var index = 0;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.set(bitset, index));
    }

    /**
     * Verifies that the {@link HelloWorld#set(BitSet, int) set(bitset, index)} method throws an
     * {@link IllegalArgumentException} when the {@code index} argument is negative.
     */
    @DisplayName("should throw an <IllegalArgumentException> when the <index> argument is negative")
    @Test
    void _ThrowIllegalArgumentException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var bitset = new BitSet();
        final var index = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 0);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(IllegalArgumentException.class, () -> service.set(bitset, index));
    }

    /**
     * Verifies that the {@link HelloWorld#set(BitSet, int) set(bitset, index)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} once and sets the bits of the given {@link BitSet}
     * in little-endian order from the array bytes, and returns the {@code bitset}.
     */
    @DisplayName("should invoke <set(byte[])> and set bits in little-endian order")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_sets_random_bytes(service());
        final var bitset = Mockito.spy(new BitSet(HelloWorld.BYTES << 3));
        final var index = 0;
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(bitset, index);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        assertArrayEquals(array, bitset.toByteArray());
        assertSame(bitset, result);
    }
}
