package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Add_Collection_Function__Test
        extends HelloWorldTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var collection = new ArrayList<Integer>();
        // ------------------------------------------------------------------------------------ when
        service.add(collection, Byte::intValue);
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, collection.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals((int) expected[i], (int) collection.get(i));
        }
    }
}
