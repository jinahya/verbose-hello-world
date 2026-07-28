#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '01-verbose-hello-world-appa/pom.xml'
mvn -f $pom -q compile exec:exec `
    '-Dexec.executable=java' `
    '-Dexec.args=--enable-preview -cp %classpath com.github.jinahya.hello.appa_.HelloWorldMain'
