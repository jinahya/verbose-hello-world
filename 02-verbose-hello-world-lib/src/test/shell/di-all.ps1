#Requires -Version 5.1
Set-StrictMode -Version Latest

$profiles = @(
    'di-avaje',
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
