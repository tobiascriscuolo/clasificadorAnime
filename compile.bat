@echo off
chcp 65001 >nul
echo ======================================
echo  Compilando Sistema de Anime
echo ======================================
echo Compilando archivos Java...

if not exist out mkdir out
if not exist out\data mkdir out\data

javac -encoding UTF-8 -d out ^
    src\excepcion\*.java ^
    src\modelo\*.java ^
    src\repositorio\*.java ^
    src\servicio\*.java ^
    src\utilidad\*.java ^
    src\vista\*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: La compilacion fallo
    pause
    exit /b 1
)

echo.
echo Compilacion exitosa!
echo.
set /p ejecutar=Desea ejecutar la aplicacion? (S/N): 
if /i "%ejecutar%"=="S" (
    java -cp out vista.VentanaPrincipal
)

pause
