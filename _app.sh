#!/usr/bin/env bash
set -uo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "==> packaging with generate-executables"
mvn -f "${script_dir}/pom.xml" -q -Pgenerate-executables -DskipTests clean package

apps=(
  01-verbose-hello-world-appa
  02-verbose-hello-world-appb
  03-verbose-hello-world-appc
  04-verbose-hello-world-appd
)
variants=(
  jar-with-dependencies
  manual
  shaded
)

pass=0
fail=0
skip=0
echo
echo "==> executing produced jars"
for app in "${apps[@]}"; do
  for variant in "${variants[@]}"; do
    jar="${script_dir}/03-verbose-hello-world-app/${app}/target/${app#*-}-0.0.1-SNAPSHOT-${variant}.jar"
    label="${app}/${variant}"
    if [[ ! -f "${jar}" ]]; then
      printf "SKIP  %s (not produced)\n" "${label}"
      skip=$((skip + 1))
      continue
    fi
    out=$(java -jar "${jar}" 2>&1)
    if printf '%s' "${out}" | grep -q 'hello, world'; then
      printf "PASS  %s\n" "${label}"
      pass=$((pass + 1))
    else
      printf "FAIL  %s\n" "${label}"
      printf '%s\n' "${out}" | sed 's/^/        /' | head -5
      fail=$((fail + 1))
    fi
  done
done

echo
echo "==> summary: ${pass} passed, ${fail} failed, ${skip} skipped"
exit $(( fail > 0 ? 1 : 0 ))
