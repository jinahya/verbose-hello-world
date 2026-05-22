#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '..\..\..\pom.xml'
mvn -f $pom -q -Pcdi-se-weld-junit5 '-Dtest=HelloWorldCdiSe_Weld_Junit5_Test' test
