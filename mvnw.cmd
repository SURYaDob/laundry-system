@echo off
:: Self-bootstrapping lightweight Maven Wrapper for Windows CMD
set "DIR=%~dp0"
set "MAVEN_VERSION=3.9.6"
set "MAVEN_DIST=apache-maven-%MAVEN_VERSION%"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_DIST%"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo ======================================================================
    echo Maven Wrapper: Maven not found at %MAVEN_HOME%
    echo Downloading Maven %MAVEN_VERSION%...
    echo Please wait while we bootstrap the build environment...
    echo ======================================================================
    
    if not exist "%USERPROFILE%\.m2\wrapper\dists" (
        mkdir "%USERPROFILE%\.m2\wrapper\dists"
    )
    
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/%MAVEN_DIST%-bin.zip' -OutFile '%TEMP%\maven.zip'"
    
    if errorlevel 1 (
        echo Error: Failed to download Maven %MAVEN_VERSION%
        exit /b 1
    )
    
    echo Extracting Maven to %USERPROFILE%\.m2\wrapper\dists...
    powershell -Command "Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force; Remove-Item '%TEMP%\maven.zip'"
    
    if errorlevel 1 (
        echo Error: Failed to extract Maven %MAVEN_VERSION%
        exit /b 1
    )
    
    echo Maven bootstrapped successfully!
)

:: Run Maven command
"%MAVEN_HOME%\bin\mvn.cmd" %*
