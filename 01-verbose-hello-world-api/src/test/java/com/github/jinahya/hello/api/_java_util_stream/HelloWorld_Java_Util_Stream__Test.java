package com.github.jinahya.hello.api._java_util_stream;

import com.github.jinahya.hello.api.*;
import lombok.*;
import org.junit.jupiter.api.*;

import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class HelloWorld_Java_Util_Stream__Test extends HelloWorld__Test {

    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __stubService() {
        doAnswer(i -> {
            final var consumer = i.getArgument(0, Consumer.class);
            final var mapper = i.getArgument(1, Function.class);
            hello_world_byte_stream().map(mapper).forEach(consumer);
            return consumer;
        }).when(service()).accept(any(), any());
    }

    @Nested
    class StreamBuilder__Test {

        @Test
        void __() {
            final var builder = Stream.<Byte>builder();
            service().accept(builder, Function.identity());
            assertEquals(
                    hello_world_byte_stream().toList(),
                    builder.build().toList()
            );
        }
    }

    @Nested
    class IntStreamBuilder__Test {

        @Test
        void __() {
            final var builder = IntStream.builder();
            service().accept((Consumer<Integer>) builder::add, Byte::intValue);
            assertArrayEquals(
                    hello_world_byte_stream().mapToInt(Byte::intValue).toArray(),
                    builder.build().toArray()
            );
        }
    }
}
