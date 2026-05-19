#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath 'pom.xml'
mvn -f $pom -q -Pcdi-se-weld '-Dtest=HelloWorldCdiSeWeldTest' test
