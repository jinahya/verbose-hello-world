package com.github.jinahya.hello


import spock.lang.Shared
import spock.lang.Specification

class HelloWorldSpec extends Specification {

    // -----------------------------------------------------------------------------------------------------------------
    def setupSpec() { // runs once - before the first feature method
        mock = Spy()
        mock.set(_ as byte[], _ as int) >> { args -> args.get(0) }
    }

    def setup() { // runs before every feature method
    }

    def cleanup() { // runs after every feature method
    }

    def cleanupSpec() { // runs once - after the last feature method
        mock = null
    }

    // -----------------------------------------------------------------------------------------------------------------
    def "assert set(byte[]) throws NullPointerException when array is null"() {
        when:
        mock.set(null)
        then:
        NullPointerException npe = thrown()
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Shared
    HelloWorld mock;
}