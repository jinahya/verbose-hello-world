$profilesAndTests = [ordered]@{
    "cdi-se-openwebbeans" = "HelloWorldCdiSeOpenWebBeansTest"
    "cdi-se-openwebbeans-junit5" = "HelloWorldCdiSeOpenWebBeansJunit5Test"
    "cdi-se-weld" = "HelloWorldCdiSeWeldTest"
    "cdi-se-weld-junit5" = "HelloWorldCdiSeWeldJunit5Test"
    "di-dagger" = "HelloWorldDiDaggerTest"
    "di-guice" = "HelloWorldDiGuiceTest"
    "di-hk2" = "HelloWorldDiHk2Test"
    "di-spring" = "HelloWorldDiSpringTest"
}
Write-Host "at: $PSScriptRoot"
foreach ($profile in $profilesAndTests.keys)
{
    $test = $profilesAndTests[$profile]
    Write-Host "$profile / $test"
    $command = "mvn -f $PSScriptRoot/pom.xml -q -P$profile -Dtest=$test test"
    #    Write-Host $command
    Invoke-Expression $command
}
