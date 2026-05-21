#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '03-verbose-hello-world-app3/pom.xml'
mvn -f $pom -q compile exec:exec `
    '-Dexec.executable=java' `
    '-Dexec.args=--enable-preview -cp %classpath com.github.jinahya.hello.app3_.HelloWorldMain'
