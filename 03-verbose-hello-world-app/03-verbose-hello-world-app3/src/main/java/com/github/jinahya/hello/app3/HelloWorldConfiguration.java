package com.github.jinahya.hello.app3;

/*-
 * #%L
 * verbose-hello-world-app3
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

import com.github.jinahya.hello.api.*;
import org.springframework.context.annotation.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * A configuration for providing {@link HelloWorld}, {@link AsynchronousHelloWorld}, and
 * {@link HelloWorldMain} beans.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Configuration
class HelloWorldConfiguration {

    HelloWorldConfiguration() {
        super();
    }

    @Bean
    HelloWorld helloWorld() {
        return ServiceLoader.load(HelloWorld.class).iterator().next();
    }

    @Bean
    AsynchronousHelloWorld asynchronousHelloWorld(final HelloWorld service) {
        return new ExecutorAsynchronousHelloWorld(service, ForkJoinPool.commonPool());
    }

    @Bean
    HelloWorldMain helloWorldMain(final AsynchronousHelloWorld service) {
        return new HelloWorldMain(service);
    }
}
