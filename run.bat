@echo off
echo ======================================
echo  Sistema de Clasificacion de Anime
echo ======================================

if not exist "out\ui\MainFrame.class" (
    echo.
    echo La aplicacion no esta compilada.
    echo Ejecute compile.bat primero.
    pause
    exit /b 1
)

cd out
java ui.MainFrame

