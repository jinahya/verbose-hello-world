# #!/bin/zsh
declare -A profilesAndTests=(
  ["cdi-se-openwebbeans"]="HelloWorldCdiSeOpenWebBeansTest"
  ["cdi-se-openwebbeans-junit5"]="HelloWorldCdiSeOpenWebBeansJunit5Test"
  ["cdi-se-weld"]="HelloWorldCdiSeWeldTest"
  ["cdi-se-weld-junit5"]="HelloWorldCdiSeWeldJunit5Test"
  ["di-dagger"]="HelloWorldDiDaggerTest"
  ["di-guice"]="HelloWorldDiGuiceTest"
  ["di-hk2"]="HelloWorldDiHk2Test"
  ["di-spring"]="HelloWorldDiSpringTest"
)
for profile in "${(@k)profilesAndTests[@]}"; do
  echo ------------------------------------------------------------------------
  test="${profilesAndTests[$profile]}"
  echo "$profile" / "$test"
  mvn -q -P"$profile" -Dtest="$test" clean test
done
