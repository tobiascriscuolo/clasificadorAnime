@echo off
echo ======================================
echo  Compilando Sistema de Anime
echo ======================================

:: Crear directorio de salida
if not exist "out" mkdir out

:: Compilar todos los archivos Java
echo Compilando archivos Java...
javac -d out -sourcepath src src\ui\MainFrame.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: La compilacion fallo.
    pause
    exit /b 1
)

echo.
echo Compilacion exitosa!
echo.

:: Preguntar si ejecutar
set /p RUN="Desea ejecutar la aplicacion? (S/N): "
if /i "%RUN%"=="S" (
    echo.
    echo Iniciando aplicacion...
    cd out
    java ui.MainFrame
)

pause

