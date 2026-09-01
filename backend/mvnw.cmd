@REM Maven Wrapper script for Windows
@REM Auto-downloads and runs Maven
@echo off
setlocal

set WRAPPER_JAR="%~dp0\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%~dp0\.mvn\wrapper\maven-wrapper.properties"

set MAVEN_PROJECTBASEDIR=%~dp0

java -jar %WRAPPER_JAR% %*
