#!/usr/bin/env bash
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
apps=(
  app1
  app2
  app3
  app4
)
for app in "${apps[@]}"; do
  echo "------------------------------------------------------------------------"
  echo "${app}"
  "${script_dir}/${app}.sh"
done
