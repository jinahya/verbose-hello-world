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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.hello.HelloWorldTestUtils.print;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteBuffer.wrap;

/**
 * A class for testing {@link HelloWorldTestUtils} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldTestUtilsTest {

    @Nested
    class NonDirectTest {

        @Test
        void drawBuffer__Empty() {
            print(allocate(0));
        }

        @Test
        void drawBuffer__One() {
            print(allocate(1));
        }

        @Test
        void drawBuffer__Two() {
            print(allocate(2));
        }

        @Test
        void drawBuffer__Six() {
            print(allocate(6));
        }

        @Test
        void drawBuffer__20() {
            var buffer = wrap(new byte[20]);
            var slice = buffer.slice(3, 14);
            slice.position(3);
            slice.limit(slice.limit() - 4);
            print(slice);
        }
    }

    @Nested
    class DirectTest {

        @Test
        void drawBuffer__Empty() {
            print(allocateDirect(0));
        }

        @Test
        void drawBuffer__One() {
            print(allocateDirect(1));
        }

        @Test
        void drawBuffer__Two() {
            print(allocateDirect(2));
        }

        @Test
        void drawBuffer__Six() {
            print(allocateDirect(6));
        }

        @Test
        void drawBuffer__20() {
            var buffer = allocateDirect(20);
            var slice = buffer.slice(3, 14);
            slice.position(3);
            slice.limit(slice.limit() - 4);
            print(slice);
        }
    }
}
