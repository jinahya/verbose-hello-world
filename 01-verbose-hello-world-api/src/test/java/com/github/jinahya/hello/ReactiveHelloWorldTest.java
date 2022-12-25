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
import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;

/**
 * An abstract class for testing methods defined in {@link ReactiveHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@ExtendWith({MockitoExtension.class})
//@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@Slf4j
abstract class ReactiveHelloWorldTest extends AbstractHelloWorldTest<ReactiveHelloWorld> {

    ReactiveHelloWorldTest() {
        super(ReactiveHelloWorld.class);
    }

    @Test
    void __() {
        ReactiveHelloWorld service = new ReactiveHelloWorld() {
            @Override
            public byte[] set(byte[] array, int index) {
                return array;
            }
        };
        service.subscribe(new Flow.Subscriber<String>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(1);
            }

            @Override
            public void onNext(String item) {
                log.info("item: {}", item);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("message: {}", throwable.getMessage(), throwable);
            }

            @Override
            public void onComplete() {
                log.info("completed");
            }
        });
    }
}
