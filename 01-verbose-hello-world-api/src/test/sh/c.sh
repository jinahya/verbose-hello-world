#!/usr/bin/env bash

###
# #%L
# verbose-hello-world-api
# %%
# Copyright (C) 2018 - 2026 Jinahya, Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
#
# (Re)builds every C program under src/test/c via the module Makefile, runs each
# produced binary, and writes its stdout to target/c/<binary>.txt.
#
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# src/test/sh -> src/test -> src -> <module root>
module_dir="$(cd "${script_dir}/../../.." && pwd)"
target_dir="${module_dir}/target/c"
out_dir="${module_dir}/txt"

echo "==> (re)making C binaries"
make -C "${module_dir}" clean-c
make -C "${module_dir}" c

echo
echo "==> executing binaries, updating txt/<binary>.txt"
mkdir -p "${out_dir}"
for bin in "${target_dir}"/*; do
  [[ -f "${bin}" && -x "${bin}" ]] || continue   # skip dirs / non-executables
  case "${bin}" in *.txt) continue ;; esac        # skip output files
  name="$(basename "${bin}")"
  out="${out_dir}/${name}.txt"
  "${bin}" >"${out}"
  printf "  %-10s -> %s (%s lines)\n" \
    "${name}" "${out#"${module_dir}/"}" "$(wc -l <"${out}" | tr -d ' ')"
done

echo
echo "==> done"
