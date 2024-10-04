#!/bin/sh
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
