#!/bin/sh
find . -type d | grep main | grep --include \*.java -r -h 'import static ' | sort | uniq
