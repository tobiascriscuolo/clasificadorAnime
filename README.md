# Sistema de Clasificación de Animé

Aplicación de escritorio en Java/Swing para gestionar y clasificar un catálogo personal de animé.

## 📋 Descripción

Este sistema permite a los usuarios mantener un catálogo personal de anime, organizándolos en listas personalizadas, calificándolos, y obteniendo recomendaciones basadas en diferentes criterios.

## 🏗️ Arquitectura

El proyecto sigue una arquitectura **MVC por capas**:

```
src/
├── model/          # Clases de dominio (Anime, ListaPersonalizada, Enums)
├── service/        # Lógica de negocio (AnimeService, RecomendacionService, etc.)
├── repository/     # Persistencia (interfaces e implementaciones)
├── ui/             # Interfaz gráfica Swing (MainFrame, paneles, diálogos)
├── exception/      # Excepciones personalizadas
└── util/           # Utilidades (estrategias de ordenamiento/recomendación)
```

## 🎯 Características

### RF1 - Gestión de Animé
- ✅ Agregar series y películas de anime
- ✅ Validación de datos (título único, año válido, calificación 1-5)
- ✅ Modificar y eliminar anime
- ✅ Listar todo el catálogo

### RF2 - Listas Personalizadas
- ✅ Crear listas con nombre y descripción
- ✅ Agregar/quitar anime de listas
- ✅ Un anime puede estar en múltiples listas

### RF3 - Búsqueda y Filtrado
- ✅ Búsqueda por título (parcial, case-insensitive)
- ✅ Filtrado por género, estado, calificación mínima
- ✅ Combinación de múltiples criterios

### RF4 - Ordenamiento y Recomendaciones
- ✅ Ordenar por título, calificación, año
- ✅ Top N global
- ✅ Top N por género
- ✅ Top N por estado
- ✅ Patrón Strategy para criterios extensibles

### RF5 - Estadísticas
- ✅ Promedio de calificaciones global y por género
- ✅ Cantidad de anime por estado
- ✅ Top 3 géneros más frecuentes

### RF6 - Persistencia
- ✅ Guardado automático en archivos binarios
- ✅ Manejo robusto de errores de I/O
- ✅ Mensajes amigables al usuario

## 🔧 Requisitos

- **JDK 17** o superior
- No requiere dependencias externas (solo Java SE + Swing)

## 🚀 Compilación y Ejecución

### Opción 1: Desde línea de comandos

```bash
# Compilar
cd src
javac -d ../out ui/MainFrame.java

# Ejecutar
cd ../out
java ui.MainFrame
```

### Opción 2: Usando el script de compilación

```bash
# Windows
compile.bat

# Linux/Mac
./compile.sh
```

### Opción 3: Desde un IDE
1. Importar la carpeta `src` como proyecto Java
2. Configurar JDK 17+
3. Ejecutar la clase `ui.MainFrame`

## 📁 Estructura de Datos

Los datos se persisten en la carpeta `data/`:
- `animes.dat` - Catálogo de anime (serialización Java)
- `listas.dat` - Listas personalizadas

## 🎨 Patrones y Principios Aplicados

### GRASP
| Patrón | Aplicación |
|--------|------------|
| Controller | AnimeService, ListaPersonalizadaService coordinan casos de uso |
| Information Expert | Anime responde sobre sus géneros, calificación |
| Creator | Services crean instancias de Anime |
| Low Coupling | UI → Services → Repositories (interfaces) |
| High Cohesion | Cada clase tiene una responsabilidad clara |
| Polymorphism | Estrategias de ordenamiento y recomendación |
| Pure Fabrication | Repositories como clases técnicas |
| Indirection | Services intermedian entre UI y datos |
| Protected Variations | Interfaces para aislar cambios |

### SOLID
| Principio | Aplicación |
|-----------|------------|
| SRP | Cada clase tiene una única razón de cambio |
| OCP | Nuevas estrategias sin modificar código existente |
| LSP | AnimeSerie/AnimePelicula sustituyen a AnimeBase |
| ISP | Interfaces pequeñas (Calificable, CriterioRecomendacion) |
| DIP | Services dependen de interfaces de Repository |

## 📊 Modelo de Dominio

### Clases Principales
- `AnimeBase` (abstracta): Clase base con atributos comunes
- `AnimeSerie`: Series con cantidad de capítulos
- `AnimePelicula`: Películas con duración en minutos
- `ListaPersonalizada`: Colección nombrada de anime

### Enumeraciones
- `Estado`: POR_VER, VIENDO, FINALIZADO, ABANDONADO
- `Genero`: SHONEN, SHOJO, SEINEN, MECHA, ISEKAI, etc.
- `TipoAnime`: SERIE, PELICULA

## 📝 Excepciones Personalizadas

- `AnimeException` (base abstracta)
- `AnimeYaExistenteException`: Título duplicado
- `AnimeNoEncontradoException`: Anime no existe
- `ListaNoEncontradaException`: Lista no existe
- `ValidacionException`: Datos inválidos
- `PersistenciaException`: Errores de I/O

## 👤 Autor

Trabajo Práctico Final - Programación Orientada a Objetos

## 📄 Licencia

Proyecto académico - Uso educativo

