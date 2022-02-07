$profilesAndTests = [ordered]@{
    "cdi-se-openwebbeans" = "HelloWorldCdiSeOpenWebBeansTest"
    "cdi-se-weld" = "HelloWorldCdiSeWeldTest"
    "di-dagger" = "HelloWorldDiDaggerTest"
    "di-guide" = "HelloWorldDiGuiceTest"
    "di-hk2" = "HelloWorldDiHk2Test"
    "di-spring" = "HelloWorldDiSpringTest"
#     "weld-junit5" = "HelloWorldWeldJunit5Test"
}
Write-Host "at: $PSScriptRoot"
foreach ($profile in $profilesAndTests.keys)
{
    $test = $profilesAndTests[$profile]
    $command = "mvn -f $PSScriptRoot\pom.xml -P$profile -Dtest=$test test"
    Write-Host $command
    Invoke-Expression $command
}