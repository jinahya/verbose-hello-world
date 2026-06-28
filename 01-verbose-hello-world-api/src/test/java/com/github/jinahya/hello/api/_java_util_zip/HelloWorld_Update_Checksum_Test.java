package com.github.jinahya.hello.api._java_util_zip;

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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#update(Checksum) update(checksum)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("update(checksum)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Checksum_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method throws a
     * {@link NullPointerException} when the {@code checksum} argument is {@code null}.
     */
    @DisplayName("throws NPE / checksum is null")
    @Test
    void _ThrowNullPointerException_ChecksumIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Checksum checksum = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.update(checksum));
    }

    /**
     * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} once and passes the array to
     * {@link Checksum#update(byte[]) checksum.update(array)}, and returns the {@code checksum}.
     */
    @DisplayName("happy path")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_returns_the_array(service());
        final var checksum = mock(Checksum.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(checksum);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once(service);
        verify(checksum, times(1)).update(array);
        verifyNoMoreInteractions(checksum);
        assertSame(checksum, result);
    }
}
