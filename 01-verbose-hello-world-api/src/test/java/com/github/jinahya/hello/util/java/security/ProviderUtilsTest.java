package com.github.jinahya.hello.util.java.security;

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
