#Requires -Version 5.1
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$pom = Join-Path -Path $PSScriptRoot -ChildPath '03-verbose-hello-world-appc/pom.xml'
mvn -f $pom -q compile exec:exec `
    '-Dexec.executable=java' `
    '-Dexec.args=--enable-preview -cp %classpath com.github.jinahya.hello.appc_.HelloWorldMain'
