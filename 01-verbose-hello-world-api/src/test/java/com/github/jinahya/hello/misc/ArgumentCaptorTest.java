package com.github.jinahya.hello.misc;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ArgumentCaptorTest {

    private static class Some {

        void a(int a) {
        }

        void b() {
            a(0);
        }
    }

    @Test
    void test() {
        var some = spy(new Some());
        some.b();
        var captor = ArgumentCaptor.forClass(int.class);
        verify(some, times(1)).a(captor.capture());
        var a = captor.getValue();
        assertEquals(0, a);
    }
}
