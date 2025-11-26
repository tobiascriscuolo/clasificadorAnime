#!/bin/bash
echo "======================================"
echo " Compilando Sistema de Anime"
echo "======================================"

# Crear directorio de salida
mkdir -p out

# Compilar todos los archivos Java
echo "Compilando archivos Java..."
javac -d out -sourcepath src src/ui/MainFrame.java

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: La compilación falló."
    exit 1
fi

echo ""
echo "Compilación exitosa!"
echo ""
echo "Para ejecutar: cd out && java ui.MainFrame"

