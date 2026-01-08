package com.github.jinahya.hello.lib;

/*-
 * #%L
 * verbose-hello-world-lib
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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorldImpl} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class __HelloWorld_WrapTest extends __HelloWorld__Test {

    @Override
    Stream<HelloWorld> services() {
        return Stream.of(
                new HelloWorldWrap()
        );
    }

    @Test
    void __ArrayIsNull() {
        try {
            ByteBuffer.wrap(null, 0, 0);
        } catch (final Exception t) {
            t.printStackTrace();
        }
    }

    @Test
    void __IndexIsNegative() {
        try {
            ByteBuffer.wrap(new byte[0], -1, 0);
        } catch (final Exception t) {
            t.printStackTrace();
        }
    }

    @Test
    void __1() {
        try {
            ByteBuffer.wrap(new byte[12], 1, 12);
        } catch (final Exception t) {
            t.printStackTrace();
        }
    }

    @Test
    void __2() {
        try {
            ByteBuffer.wrap(new byte[11], 0, 12);
        } catch (final Exception t) {
            t.printStackTrace();
        }
    }
}
