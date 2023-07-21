#!/bin/zsh
declare -A profilesAndTests=(
#  ["cdi-se-openwebbeans"]="HelloWorldCdiSeOpenWebBeansTest"
  ["cdi-se-weld"]="HelloWorldCdiSeWeldTest"
  ["di-dagger"]="HelloWorldDiDaggerTest"
  ["di-guide"]="HelloWorldDiGuiceTest"
  ["di-hk2"]="HelloWorldDiHk2Test"
  ["di-spring"]="HelloWorldDiSpringTest"
#  ["weld-junit5"]="HelloWorldWeldJunit5Test"
)

for key in "${(kv)profilesAndTests[@]}"; do
  ../mvnw -q -P"$key" -Dtest="$value" clean test
done
