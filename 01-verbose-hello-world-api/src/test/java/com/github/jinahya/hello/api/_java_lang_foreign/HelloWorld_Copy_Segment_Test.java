package com.github.jinahya.hello.api._java_lang_foreign;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

class HelloWorld_Copy_Segment_Test extends HelloWorldTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        stub_set_array_will_return_the_array();
        try (var arena = Arena.ofConfined()) {
            final var segment = arena.allocate(HelloWorld.BYTES);
            try (var mockedStatic = Mockito.mockStatic(MemorySegment.class,
                                                       Mockito.CALLS_REAL_METHODS)) {
                // ---------------------------------------------------------------------------- when
                final var result = service.copy(segment);
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(segment, result);
                final var array = verify_set_array12_invoked_once();
                mockedStatic.verify(() -> MemorySegment.copy(
                        Mockito.same(array),
                        Mockito.eq(0),
                        Mockito.same(segment),
                        Mockito.eq(ValueLayout.JAVA_BYTE),
                        Mockito.eq(0L),
                        Mockito.eq(array.length)
                ));
            }
        }
    }
}
