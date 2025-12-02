@echo off
chcp 65001 >nul
echo ======================================
echo  Sistema de Clasificacion de Anime
echo ======================================
echo.

if not exist out\vista\VentanaPrincipal.class (
    echo No se encontraron archivos compilados.
    echo Ejecutando compilacion...
    call compile.bat
) else (
    java -cp out vista.VentanaPrincipal
)

pause
