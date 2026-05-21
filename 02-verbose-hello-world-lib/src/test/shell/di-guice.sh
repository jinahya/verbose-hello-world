#!/usr/bin/env bash
set -euo pipefail
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mvn -f "${script_dir}/../../../pom.xml" -q -Pdi-guice -Dtest=HelloWorldDi_Guice_Test clean test
