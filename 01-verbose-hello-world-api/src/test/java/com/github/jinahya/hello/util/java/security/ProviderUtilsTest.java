package com.github.jinahya.hello.util.java.security;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ProviderUtilsTest {

    @Test
    void getAllServices__() {
        final var set = ProviderUtils.getAllServices();
        set.forEach(s -> {
            log.debug("service: {}", s);
            log.debug("\tprovider: {}", s.getProvider());
            log.debug("\ttype: {}", s.getType());
            log.debug("\talgorithm: {}", s.getAlgorithm());
        });
        assertThat(set).isNotEmpty();
    }

    @Test
    void getAllServiceTypes__() {
        final var set = ProviderUtils.getAllServiceTypes();
        set.forEach(t -> {
            log.debug("service.type: {}", t);
        });
        assertThat(set).isNotEmpty();
    }
}
