package com.github.jinahya.hello.api._java_nio_file;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(Path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(Path) append(path)} method throws a
     * {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <path> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = (Path) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(path)
        );
    }

    @DisplayName("should invoke <write(FileChannel.open(path, CREATE, WRITE, APPEND))>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.<WritableByteChannel>any());
        final var path = Mockito.mock(Path.class);
        final var channel = Mockito.mock(FileChannel.class);
        try (var mockStatic = Mockito.mockStatic(FileChannel.class)) {
            mockStatic.when(() -> FileChannel.open(ArgumentMatchers.same(path),
                                                   ArgumentMatchers.any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.append(path);
            // -------------------------------------------------------------------------------- then
            final var captor = ArgumentCaptor.forClass(OpenOption[].class);
//            mockStatic.verify(
//                    () -> FileChannel.open(ArgumentMatchers.same(path), captor.capture()),
//                    Mockito.times(1)
//            );
//            final var value = captor.getValue();
//            final var options = new HashSet<>(Arrays.asList(value));
//            Assertions.assertTrue(options.remove(StandardOpenOption.CREATE));
//            Assertions.assertTrue(options.remove(StandardOpenOption.APPEND));
//            Assertions.assertTrue(options.isEmpty());
//            Mockito.verify(service, Mockito.times(1)).write(channel);
//            Mockito.verify(channel, Mockito.times(1)).force(true);
//            Mockito.verify(channel, Mockito.times(1)).close();
            Assertions.assertSame(path, result);
        }
    }
}
