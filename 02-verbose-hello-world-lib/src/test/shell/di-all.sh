#!/usr/bin/env bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
profiles=(
  di-avaje
  di-dagger
  di-guice
  di-hk2
  di-micronaut
  di-spring
)
for profile in "${profiles[@]}"; do
  echo "------------------------------------------------------------------------"
  echo "${profile}"
  "${script_dir}/${profile}.sh"
done
