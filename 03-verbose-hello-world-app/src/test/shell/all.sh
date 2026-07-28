#!/usr/bin/env bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
apps=(
  appa
  appb
  appc
  appd
)
for app in "${apps[@]}"; do
  echo "------------------------------------------------------------------------"
  echo "${app}"
  "${script_dir}/${app}.sh"
done
