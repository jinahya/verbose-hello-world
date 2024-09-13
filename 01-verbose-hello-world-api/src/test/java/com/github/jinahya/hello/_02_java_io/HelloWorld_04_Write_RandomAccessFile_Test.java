package com.github.jinahya.hello._02_java_io;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class for testing {@link HelloWorld#write(RandomAccessFile) write(file)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(file)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_04_Write_RandomAccessFile_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(RandomAccessFile file) write(RandomAccessFile file)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the file argument is null"""
    )
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = (RandomAccessFile) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.write(file)> throws NullPointerException
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(file)
        );
    }

    /**
     * Verifies {@link HelloWorld#write(RandomAccessFile) write(file)} method invokes
     * {@link HelloWorld#set(byte[]) set(array)} method with an array of {@value HelloWorld#BYTES}
     * bytes, and invokes {@link RandomAccessFile#write(byte[])} method on the {@code file} argument
     * with the array.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> set(array[12]) -> file.write(array)")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.set(array)> returns the <array>
        BDDMockito.willAnswer(i -> i.getArgument(0))
                .given(service)
                .set(ArgumentMatchers.any());
        final var file = Mockito.mock(RandomAccessFile.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(file);
        // ------------------------------------------------------------------------------------ then
        // verify, <service.set(byte[12])> invoked, once
        final var arrayCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(service, Mockito.times(1)).set(arrayCaptor.capture());
        final var array = arrayCaptor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        // verify, <file.write(array)> invoked, once and only

        // assert, <result> is same as <file>
        Assertions.assertSame(file, result);
    }
}
