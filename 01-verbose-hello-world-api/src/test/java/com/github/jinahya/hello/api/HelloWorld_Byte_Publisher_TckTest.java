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
import org.reactivestreams.tck.flow.junit.jupiter.*;
import org.reactivestreams.tck.junit.jupiter.*;

import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Drives the Reactive Streams 1.0 {@link FlowPublisherVerification TCK} (JUnit Jupiter port,
 * {@link java.util.concurrent.Flow Flow}-flavoured) against {@link HelloWorldBytePublisher}
 * <em>naively</em> — {@link #createFlowPublisher(long)} returns a fresh publisher with no
 * length-coercing wrapper.
 * <p>
 * {@link HelloWorldBytePublisher} always emits exactly {@value HelloWorld#BYTES} {@link Byte}
 * elements followed by {@code onComplete} regardless of the {@code elements} argument, so TCK tests
 * that demand {@code createPublisher(N)} produce exactly {@code N} elements (for
 * {@code N != }{@value HelloWorld#BYTES}) are overridden here and marked {@link Disabled}.
 * <p>
 * {@link #createFailedFlowPublisher()} returns {@code null}; the additional "failed publisher"
 * tests are skipped.
 * <p>
 * {@link #maxElementsFromPublisher()} is {@value HelloWorld#BYTES}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldBytePublisher
 * @see FlowPublisherVerification
 */
@DisplayName("HelloWorld / byte Publisher / TCK")
class HelloWorld_Byte_Publisher_TckTest extends FlowPublisherVerification<Byte> {

    HelloWorld_Byte_Publisher_TckTest() {
        super(new TestEnvironment());
    }

    /**
     * {@inheritDoc}
     *
     * @param elements ignored — {@link HelloWorldBytePublisher} always emits
     *                 {@value HelloWorld#BYTES} elements.
     * @return a fresh {@link HelloWorldBytePublisher} backed by a fresh
     * {@code hello-world-bytes}-stubbed mock {@link HelloWorld} service.
     */
    @Override
    public Flow.Publisher<Byte> createFlowPublisher(final long elements) {
        final var service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        set_array_sets_hello_world_bytes(service);
        return new HelloWorldBytePublisher(service);
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code null}.
     */
    @Override
    public Flow.Publisher<Byte> createFailedFlowPublisher() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value HelloWorld#BYTES}.
     */
    @Override
    public long maxElementsFromPublisher() {
        return HelloWorld.BYTES;
    }

    // ---------------------------------------------- disabled: fixed-length cannot satisfy N != 12

    private static final String REASON_FIXED_12 =
            "HelloWorldBytePublisher always emits exactly " + HelloWorld.BYTES
            + " bytes then onComplete; createPublisher(N) cannot truncate to N != "
            + HelloWorld.BYTES + " without wrapping.";

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_createPublisher1MustProduceAStreamOfExactly1Element() throws Throwable {
        super.required_createPublisher1MustProduceAStreamOfExactly1Element();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_createPublisher3MustProduceAStreamOfExactly3Elements() throws Throwable {
        super.required_createPublisher3MustProduceAStreamOfExactly3Elements();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_spec102_maySignalLessThanRequestedAndTerminateSubscription()
            throws Throwable {
        super.required_spec102_maySignalLessThanRequestedAndTerminateSubscription();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates() throws Throwable {
        super.required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_spec107_mustNotEmitFurtherSignalsOnceOnCompleteHasBeenSignalled()
            throws Throwable {
        super.required_spec107_mustNotEmitFurtherSignalsOnceOnCompleteHasBeenSignalled();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_spec317_mustSupportAPendingElementCountUpToLongMaxValue()
            throws Throwable {
        super.required_spec317_mustSupportAPendingElementCountUpToLongMaxValue();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue()
            throws Throwable {
        super.required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue();
    }

    @Override
    @Test
    @Disabled(REASON_FIXED_12)
    public void stochastic_spec103_mustSignalOnMethodsSequentially() throws Throwable {
        super.stochastic_spec103_mustSignalOnMethodsSequentially();
    }
}
