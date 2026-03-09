@echo off

cd ../

if exist "latest-updated.jar" (
    echo Found latest-updated.jar, updating...

    if exist "latest.jar" (
        del /f /q "latest.jar"
    )

    ren "latest-updated.jar" "latest.jar"
)

start javaw -jar latest.jar