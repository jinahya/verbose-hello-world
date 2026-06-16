package com.github.jinahya.hello.api._java_util_function;

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
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#accept(Consumer, Function) accept(consumer, mapper)}
 * method with a real {@link Consumer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("accept(consumer, mapper)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Accept_Consumer_Mapper__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var consumer = i.getArgument(0, Consumer.class);
            final var mapper = i.getArgument(1, Function.class);
            hello_world_byte_stream().map(mapper).forEach(consumer);
            return consumer;
        }).when(service()).accept(any(), any());
    }

    /**
     * Verifies that the {@link HelloWorld#accept(Consumer, Function) accept(consumer, mapper)}
     * method writes the {@code "hello, world"} bytes through a
     * {@link ByteArrayOutputStream#write(int) ByteArrayOutputStream::write} consumer.
     */
    @DisplayName("ByteArrayOutputStream::write")
    @Test
    void __() throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            service().accept((Consumer<Integer>) baos::write, Integer::valueOf);
            baos.flush();
            assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING,
                         new String(baos.toByteArray(), StandardCharsets.US_ASCII));
        }
    }
}
