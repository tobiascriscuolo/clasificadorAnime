@echo off
title Clasificador de Anime

:: Ir al directorio del proyecto
cd /d "%~dp0"

:: Verificar si está compilado
if not exist "out\ui\MainFrame.class" (
    echo.
    echo La aplicacion no esta compilada. Compilando...
    echo.
    if not exist "out" mkdir out
    javac -source 8 -target 8 -d out -sourcepath src src\ui\MainFrame.java
    if errorlevel 1 (
        echo.
        echo ERROR: No se pudo compilar. Verifica que Java este instalado.
        pause
        exit /b 1
    )
    echo Compilacion exitosa!
    echo.
)

:: Ejecutar la aplicacion
cd out
start "" javaw ui.MainFrame


