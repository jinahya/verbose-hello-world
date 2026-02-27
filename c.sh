#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
C_SRC_DIR="$SCRIPT_DIR/01-verbose-hello-world-api/src/main/c"

for src in "$C_SRC_DIR"/*.c; do
    name="$(basename "$src" .c)"
    exe="$SCRIPT_DIR/$name"
    echo "==> Compiling: $src"
    cc -std=c99 -Wall -Wextra -o "$exe" "$src" -lm
    echo "==> Running: $exe"
    "$exe"
    echo
done
