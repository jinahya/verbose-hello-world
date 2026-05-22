#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '04-verbose-hello-world-app4/pom.xml'
mvn -f $pom -q compile exec:exec `
    '-Dexec.executable=java' `
    '-Dexec.args=--enable-preview -cp %classpath com.github.jinahya.hello.app4_.HelloWorldMain'
