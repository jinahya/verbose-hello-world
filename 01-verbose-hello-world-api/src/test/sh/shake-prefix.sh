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
set -euo pipefail

# cross-check for HelloWorld_Update_MessageDigest__Test#__SHAKE_prefix(bitStrength)

readonly INPUT='hello, world'

shake_base64() {
    printf '%s' "${INPUT}" | openssl dgst -"$1" -xoflen "$2" -binary | base64 | tr -d '\n'
}

printf '%-20s %3d %s\n' SHAKE128-128 128 "$(shake_base64 shake128 16)"
printf '%-20s %3d %s\n' SHAKE128-256 256 "$(shake_base64 shake128 32)"
printf '%-20s %3d %s\n' SHAKE256-256 256 "$(shake_base64 shake256 32)"
printf '%-20s %3d %s\n' SHAKE256-512 512 "$(shake_base64 shake256 64)"
