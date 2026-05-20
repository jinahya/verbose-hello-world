#!/usr/bin/env bash
set -euo pipefail
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mvn -f "${script_dir}/../../../pom.xml" -q -Pdi-guice -Dtest=HelloWorldDiGuiceTest clean test
