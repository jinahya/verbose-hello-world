#!/bin/zsh
declare -A profilesAndTests=(
  ["cdi-se-openwebbeans"]="HelloWorldCdiSeOpenWebBeansTest"
  ["cdi-se-weld"]="HelloWorldCdiSeWeldTest"
  ["di-dagger"]="HelloWorldDiDaggerTest"
  ["di-guice"]="HelloWorldDiGuiceTest"
  ["di-hk2"]="HelloWorldDiHk2Test"
  ["di-spring"]="HelloWorldDiSpringTest"
#  ["weld-junit5"]="HelloWorldWeldJunit5Test"
)

for profile test in "${(kv)profilesAndTests[@]}"; do
  echo ------------------------------------------------------------------------
  echo $profile / $test
  mvn -q -P"$profile" -Dtest="$test" clean test
done
