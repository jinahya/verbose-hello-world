#Requires -Version 5.1
Set-StrictMode -Version Latest

$apps = @(
    'app1',
    'app2',
    'app3',
    'app4'
)
Write-Host "at: $PSScriptRoot"
foreach ($app in $apps) {
    Write-Host '------------------------------------------------------------------------'
    Write-Host $app
    & (Join-Path -Path $PSScriptRoot -ChildPath "$app.ps1")
}
