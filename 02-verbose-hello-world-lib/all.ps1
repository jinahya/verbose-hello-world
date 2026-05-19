#Requires -Version 5.1
Set-StrictMode -Version Latest

$profiles = @(
    'cdi-se-openwebbeans',
    'cdi-se-openwebbeans-junit5',
    'cdi-se-weld',
    'cdi-se-weld-junit5',
    'di-dagger',
    'di-guice',
    'di-hk2',
    'di-spring'
)
Write-Host "at: $PSScriptRoot"
foreach ($id in $profiles) {
    Write-Host '------------------------------------------------------------------------'
    Write-Host $id
    & (Join-Path -Path $PSScriptRoot -ChildPath "$id.ps1")
}
