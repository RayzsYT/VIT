:: Variables
set "VIT_DIR=%appdata%\..\local\VIT"
set "VIT_JAR_FILE=%appdata%\..\local\VIT\latest.jar"

set "JAVA_URL=https://releases.installbuilder.com/installbuilder/java/liberica-jdk21.0.3-windows-x64.zip"
set "GITHUB_URL=https://api.github.com/repos/rayzsyt/vit/releases/latest"

set "JAVA_DIR=%VIT_DIR%\java"
set "JAVA_SANITY_CHECK=%JAVA_DIR%\jdk21.0.3-windows-x64\java-windows\lib\jvm.cfg"
set "TMP_JAVA_ZIP_FILE=%TEMP%\liberica-jdk21.0.3-windows-x64.zip"


@echo off
cls


:checks

:: Preparing java (Not actual installer though. Just a smaller copy of it inside the VIT folder.)
if not exist "%JAVA_SANITY_CHECK%" (

    echo Alright, here we go! Let's prepare Java first.
    title Preparing Java...

    if not exist "%JAVA_DIR%" mkdir "%JAVA_DIR%"

    echo Downloading...
    curl -L -o "%TMP_JAVA_ZIP_FILE%" "%JAVA_URL%"

    if errorlevel 1 (
        echo Download failed. Please report the issue at: https://github.com/RayzsYT/VIT/issues/new
        exit /b 1
    )

    echo Unzipping...
    powershell -NoProfile -Command "Expand-Archive -LiteralPath '%TMP_JAVA_ZIP_FILE%' -DestinationPath '%JAVA_DIR%' -Force"

    if errorlevel 1 (
        echo Extraction failed.
        exit /b 1
    )

    echo Sweeping all the sweat after all the hard work...
    del /q "%TMP_JAVA_ZIP_FILE%"

    echo Done!
)

:: Downloading and starting VIT now.
if not exist "%VIT_JAR_FILE%" (

    echo Downloading latest version of VIT...
    title Preparing VIT installation setup...

    powershell -NoProfile -Command "$release = Invoke-RestMethod -Uri '%GITHUB_URL%'; $jar = $release.assets | Where-Object { $_.name -like '*.jar' } | Select-Object -First 1; if (-not $jar) { exit 1 }; Invoke-WebRequest -Uri $jar.browser_download_url -OutFile '%VIT_JAR_FILE%'"

    if errorlevel 1 (
        echo Failed to download VIT. Please report it back here: https://github.com/RayzsYT/VIT/issues/new
        exit /b 1
    )

    echo Done!
)


:: Starting VIT
cd /d %VIT_DIR%
if exist "latest-updated.jar" (
    echo Found latest-updated.jar, updating...

    if exist "latest.jar" (
        del /f /q "latest.jar"
    )

    ren "latest-updated.jar" "latest.jar"
)

start "" "java/jdk21.0.3-windows-x64/java-windows/bin/javaw.exe" -jar latest.jar