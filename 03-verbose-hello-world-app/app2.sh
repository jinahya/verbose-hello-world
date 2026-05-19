#!/usr/bin/env bash
set -euo pipefail
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mvn -f "${script_dir}/02-verbose-hello-world-app2/pom.xml" -q compile exec:exec \
  -Dexec.executable=java \
  -Dexec.args='--enable-preview -cp %classpath com.github.jinahya.hello.app2_.HelloWorldMain'
