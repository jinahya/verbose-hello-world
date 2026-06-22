#!/usr/bin/env bash

###
# #%L
# verbose-hello-world-app
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
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mvn -f "${script_dir}/03-verbose-hello-world-appc/pom.xml" -q compile exec:exec \
  -Dexec.executable=java \
  -Dexec.args='--enable-preview -cp %classpath com.github.jinahya.hello.appc_.HelloWorldMain'
