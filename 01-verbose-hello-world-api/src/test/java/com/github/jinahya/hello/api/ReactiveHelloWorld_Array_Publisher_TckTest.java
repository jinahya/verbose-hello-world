package com.github.jinahya.hello.api;

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

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.reactivestreams.*;
import org.reactivestreams.tck.junit.jupiter.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Drives the Reactive Streams 1.0 {@link PublisherVerification TCK} (JUnit Jupiter port) against
 * {@link ReactiveHelloWorldArrayPublisher} <em>naively</em> — {@link #createPublisher(long)}
 * returns a fresh publisher with no length-coercing wrapper.
 * <p>
 * {@link ReactiveHelloWorldArrayPublisher} is open-ended (it never naturally completes), so
 * {@link #maxElementsFromPublisher()} returns {@link Long#MAX_VALUE}, which tells the TCK to skip
 * every test that requires a natural {@code onComplete}.
 * <p>
 * {@link #createFailedPublisher()} returns {@code null}; the additional "failed publisher" tests
 * are skipped.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldArrayPublisher
 * @see PublisherVerification
 */
@DisplayName("ReactiveHelloWorld / array Publisher / TCK")
class ReactiveHelloWorld_Array_Publisher_TckTest extends PublisherVerification<byte[]> {

    ReactiveHelloWorld_Array_Publisher_TckTest() {
        super(new TestEnvironment());
    }

    /**
     * {@inheritDoc}
     *
     * @param elements ignored — {@link ReactiveHelloWorldArrayPublisher} is open-ended.
     * @return a fresh {@link ReactiveHelloWorldArrayPublisher} backed by a fresh
     * {@code hello-world-bytes}-stubbed mock {@link HelloWorld} service.
     */
    @Override
    public Publisher<byte[]> createPublisher(final long elements) {
        final var service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        set_array_sets_hello_world_bytes(service);
        return new ReactiveHelloWorldArrayPublisher(
                new ReactiveHelloWorldBytePublisher(service)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code null}.
     */
    @Override
    public Publisher<byte[]> createFailedPublisher() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link Long#MAX_VALUE} — {@link ReactiveHelloWorldArrayPublisher} never naturally
     * completes.
     */
    @Override
    public long maxElementsFromPublisher() {
        return Long.MAX_VALUE;
    }

    // ------------------------ disabled: request(n <= 0) validation intentionally omitted (Rule 3.9)

    private static final String REASON_TRUSTS_REQUEST =
            "ReactiveHelloWorldArrayPublisher's class javadoc documents that it trusts well-behaved "
            + "externals and does not defend against request(n <= 0) (Rule 3.9).";

    @Override
    @Test
    @Disabled(REASON_TRUSTS_REQUEST)
    public void required_spec309_requestZeroMustSignalIllegalArgumentException() throws Throwable {
        super.required_spec309_requestZeroMustSignalIllegalArgumentException();
    }

    @Override
    @Test
    @Disabled(REASON_TRUSTS_REQUEST)
    public void required_spec309_requestNegativeNumberMustSignalIllegalArgumentException()
            throws Throwable {
        super.required_spec309_requestNegativeNumberMustSignalIllegalArgumentException();
    }
}
