@echo off
IF "%1"=="" GOTO :noargs
./gradlew.bat run --args "%*"

:noargs
    ./gradlew.bat run
