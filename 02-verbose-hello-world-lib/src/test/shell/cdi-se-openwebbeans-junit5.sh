#!/usr/bin/env bash
set -euo pipefail
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mvn -f "${script_dir}/../../../pom.xml" -q -Pcdi-se-openwebbeans-junit5 -Dtest=HelloWorldCdiSe_OpenWebBeans_Junit5_Test clean test
