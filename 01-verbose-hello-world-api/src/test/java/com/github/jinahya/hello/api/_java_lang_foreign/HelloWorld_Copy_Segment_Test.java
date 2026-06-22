package com.github.jinahya.hello.api._java_lang_foreign;

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
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.foreign.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#copy(java.lang.foreign.MemorySegment) copy(segment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("copy(segment)")
class HelloWorld_Copy_Segment_Test extends HelloWorld__Test {

    /**
     * Verifies that the method invokes {@link HelloWorld#set(byte[])} with an array of
     * {@value HelloWorld#BYTES} bytes, copies the {@code array} to the {@code segment}, and returns
     * the {@code segment}.
     */
    @DisplayName("happy path")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        // MemorySegment is a sealed interface — Mockito can't mock the instance; use a real
        // heap-backed segment of exact BYTES so the byteSize() guard passes naturally.
        final var segment = MemorySegment.ofArray(new byte[HelloWorld.BYTES]);
        try (var mockedStatic = mockStatic(MemorySegment.class, Mockito.CALLS_REAL_METHODS)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.copy(segment);
            // -------------------------------------------------------------------------------- then
            assertSame(segment, result);
            final var array = set_array12_invoked_once(service);
            mockedStatic.verify(
                    () -> MemorySegment.copy(
                            array, 0, segment, ValueLayout.JAVA_BYTE, 0L, array.length),
                    times(1)
            );
            mockedStatic.verifyNoMoreInteractions();
        }
    }
}
