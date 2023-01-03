#!/bin/sh
find . -type d | grep main | grep --include \*.java -r -h 'import lombok' | sort | uniq
