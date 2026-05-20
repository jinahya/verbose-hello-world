#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '..\..\..\pom.xml'
mvn -f $pom -q -Pdi-hk2 '-Dtest=HelloWorldDiHk2Test' test
