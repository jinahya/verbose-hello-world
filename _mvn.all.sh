#!/bin/sh
declare -a profiles=("cdi-se-openwebbeans" "cdi-se-openwebbeans-junit5" "cdi-se-weld" "cdi-se-weld-junit5" "di-dagger" "di-guice" "di-hk2" "di-spring")
for profile in "${profiles[@]}"
do
   mvn -P"$profile" clean test
done

