package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Put_SequencedMap_IntFunction_Function__Test
        extends HelloWorldTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var map = new LinkedHashMap<Integer, Byte>();
        // ------------------------------------------------------------------------------------ when
        service.put(map, i -> i, Function.identity());
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, map.size());
        var i = 0;
        for (final var entry : map.entrySet()) {
            Assertions.assertEquals(i, entry.getKey());
            Assertions.assertEquals(expected[i], entry.getValue());
            i++;
        }
    }
}
