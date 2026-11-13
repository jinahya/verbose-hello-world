package com.github.jinahya.hello.api;

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

import org.mockito.*;

import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.util.Objects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A collection of test-side helpers shared across the {@link JdbcHelloWorld} test classes.
 * <p>
 * The <strong>mock stubbing helpers</strong> — the {@code <verb>_<argType>_<does>(service)}
 * methods — stub the {@link JdbcHelloWorld#set(byte[], int) set(array, index)} method, the only
 * method on which every default method of the {@link JdbcHelloWorld} interface relies, on a
 * {@linkplain Mockito#mock(Class) mock} service, then return the same service for chaining.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld__TestUtils
 */
@SuppressWarnings({
        "java:S101"
})
final class JdbcHelloWorld__TestUtils {

    private static <T extends JdbcHelloWorld> T requireMock(final T object) {
        requireNonNull(object, "object is null");
        if (!mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the given mock object's {@link JdbcHelloWorld#set(byte[], int) set(array, index)}
     * method to just return the {@code array}.
     *
     * @param service the mock object whose
     *                {@link JdbcHelloWorld#set(byte[], int) set(array, index)} method needs to be
     *                stubbed.
     * @param <T>     service type parameter
     * @return the given {@code service}.
     * @see #set_array_index_sets_hello_world_bytes(JdbcHelloWorld)
     */
    static <T extends JdbcHelloWorld> T set_array_index_returns_the_array(final T service) {
        requireMock(service);
        doAnswer(returnsFirstArg())
                .when(service)
                .set(ArgumentMatchers.<byte[]>notNull(), anyInt());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link JdbcHelloWorld#set(byte[], int) set(array, index)}
     * method to fill {@value JdbcHelloWorldConstants#BYTES} bytes of the {@code array}, starting at the
     * {@code index}, with {@linkplain ThreadLocalRandom random bytes} and return the array.
     *
     * @param service the mock service.
     * @param <T>     service type parameter
     * @return the given {@code service}.
     * @see #set_array_index_sets_hello_world_bytes(JdbcHelloWorld)
     */
    static <T extends JdbcHelloWorld> T set_array_index_sets_random_bytes(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            final var index = i.getArgument(1, Integer.class);
            final var random = new byte[JdbcHelloWorldConstants.BYTES];
            ThreadLocalRandom.current().nextBytes(random);
            System.arraycopy(random, 0, array, index, JdbcHelloWorldConstants.BYTES);
            return array;
        }).when(service).set(ArgumentMatchers.<byte[]>notNull(), anyInt());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link JdbcHelloWorld#set(byte[], int) set(array, index)}
     * method to set an actual {@code hello, world} bytes to the {@code array}, starting at the
     * {@code index}, and returns the array.
     *
     * @param service the mock service.
     * @param <T>     service type parameter
     * @return the given {@code service}.
     * @see #set_array_index_returns_the_array(JdbcHelloWorld)
     */
    static <T extends JdbcHelloWorld> T set_array_index_sets_hello_world_bytes(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            final var index = i.getArgument(1, Integer.class);
            System.arraycopy(hello_world_byte_array(), 0, array, index, JdbcHelloWorldConstants.BYTES);
            return array;
        }).when(service).set(ArgumentMatchers.<byte[]>notNull(), anyInt());
        return service;
    }

    /**
     * Verifies that the specified mock service's
     * {@link JdbcHelloWorld#set(byte[], int) set(array, index)} method is invoked once with a
     * {@value JdbcHelloWorldConstants#BYTES}-byte array and {@code 0}, and returns the captured array.
     *
     * @param service the mock service.
     * @return captured array.
     */
    static byte[] set_array12_index0_invoked_once(final JdbcHelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(byte[].class);
        verify(service, times(1)).set(captor.capture(), eq(0));
        final var array = captor.getValue();
        assertNotNull(array);
        assertEquals(JdbcHelloWorldConstants.BYTES, array.length);
        return array;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    private JdbcHelloWorld__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
