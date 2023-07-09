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

import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile) write(RandomAccessFile file)}
 * method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_06_Write_RandomAccessFile_Test
 */
@DisplayName("write(RandomAccessFile file) arguments")
@Slf4j
class HelloWorld_06_Write_RandomAccessFile_Arguments_Test extends _HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile file) write(RandomAccessFile file)} method
     * throws a {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var file = (RandomAccessFile) null;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(NullPointerException.class, () -> service.write(file));
    }
}
