#Requires -Version 5.1
Set-StrictMode -Version Latest

$apps = @(
    'appa',
    'appb',
    'appc',
    'appd'
)
Write-Host "at: $PSScriptRoot"
foreach ($app in $apps) {
    Write-Host '------------------------------------------------------------------------'
    Write-Host $app
    & (Join-Path -Path $PSScriptRoot -ChildPath "$app.ps1")
}
