package com.github.jinahya.hello.api._java_util_stream;

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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Stream__Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @SuppressWarnings({"unchecked"})
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var consumer = i.getArgument(0, Consumer.class);
            final var mapper = i.getArgument(1, Function.class);
            for (final var b : HelloWorldTestUtils.hello_world_byte_array()) {
                consumer.accept(mapper.apply(b));
            }
            return consumer;
        }).when(service()).acceptEach(ArgumentMatchers.notNull(),
                                      ArgumentMatchers.notNull());
        Mockito.doAnswer(i -> {
            final var consumer = i.getArgument(0, IntConsumer.class);
            for (final var b : HelloWorldTestUtils.hello_world_byte_array()) {
                consumer.accept(b);
            }
            return consumer;
        }).when(service()).acceptEach(ArgumentMatchers.notNull());
    }
    // ---------------------------------------------------------------------------------------------

    @DisplayName("Stream.Builder")
    @Nested
    class StreamBuilder_Test {

        @DisplayName("""
                <Stream.Builder<T>> implements <Consumer<T>>,
                and can be passed to <acceptEach(consumer, mapper)>""")
        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var builder = Stream.<String>builder();
            final Function<Byte, String> mapper = b -> String.valueOf((char) b.byteValue());
            // -------------------------------------------------------------------------------- when
            final var stream = service.acceptEach(builder, mapper).build();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(
                    HelloWorldTestUtils.hello_world_string(),
                    stream.collect(Collectors.joining())
            );
        }
    }

    @DisplayName("IntStream.Builder")
    @Nested
    class IntStreamBuilder_Test {

        @DisplayName("""
                <IntStream.Builder> implements <IntConsumer>,
                and can be passed to <acceptEach(consumer)>""")
        @Test
        void __() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var builder = IntStream.builder();
            // -------------------------------------------------------------------------------- when
            final var stream = service.acceptEach(builder).build();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(
                    HelloWorldTestUtils.hello_world_string(),
                    stream.mapToObj(Character::toString).collect(Collectors.joining())
            );
        }
    }
}
