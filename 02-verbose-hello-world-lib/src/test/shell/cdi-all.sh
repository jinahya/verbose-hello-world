#!/usr/bin/env bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
profiles=(
  cdi-se-openwebbeans
  cdi-se-openwebbeans-junit5
  cdi-se-weld
  cdi-se-weld-junit5
)
for profile in "${profiles[@]}"; do
  echo "------------------------------------------------------------------------"
  echo "${profile}"
  "${script_dir}/${profile}.sh"
done
