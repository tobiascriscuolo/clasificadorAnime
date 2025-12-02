@echo off
chcp 65001 >nul
title Clasificador de Anime

:: Ir al directorio del proyecto
cd /d "%~dp0"

:: Verificar si está compilado
if not exist "out\vista\VentanaPrincipal.class" (
    echo.
    echo La aplicacion no esta compilada. Compilando...
    echo.
    if not exist "out" mkdir out
    javac -encoding UTF-8 -d out src\excepcion\*.java src\modelo\*.java src\repositorio\*.java src\servicio\*.java src\utilidad\*.java src\vista\*.java
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
start "" javaw vista.VentanaPrincipal
