package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-app4
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

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * A class for producing (and disposing) instances of {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldProvider {

    /**
     * Produces an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     * @see #dispose(HelloWorld)
     */
    @Produces
    public HelloWorld produce() {
        var loader = ServiceLoader.load(HelloWorld.class);
        var iterator = loader.iterator();
        var bean = iterator.next(); // NoSuchElementException
        log.debug("producing {}...", bean);
        return bean;
    }

    /**
     * Disposes specified instance of {@link HelloWorld} interface.
     *
     * @param bean the instance of {@link HelloWorld} interface to dispose.
     * @see #produce()
     */
    void dispose(@Disposes HelloWorld bean) {
        log.debug("disposing {}...", bean);
    }
}
