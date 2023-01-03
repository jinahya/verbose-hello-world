#!/bin/sh
find . -type d | grep test | grep --include \*.java -r -h 'import lombok' | sort | uniq
