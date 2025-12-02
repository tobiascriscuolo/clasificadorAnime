# Diagrama de Clases - Descripción Textual

## 1. Paquete `modelo` - Clases de Dominio

### Interfaces
- **Calificable** (interface)
  - `+obtenerCalificacion(): int`
  - `+establecerCalificacion(int): void`
  - `+tieneCalificacion(): boolean`

### Enumeraciones
- **Estado**: POR_VER, VIENDO, FINALIZADO, ABANDONADO
- **Genero**: SHONEN, SHOJO, SEINEN, JOSEI, MECHA, ISEKAI, SLICE_OF_LIFE, etc.
- **TipoAnime**: SERIE, PELICULA

### Clases

#### AnimeBase (abstracta) implements Calificable, Serializable
**Atributos:**
- `-titulo: String`
- `-anioLanzamiento: int`
- `-estudio: String`
- `-estado: Estado`
- `-calificacionUsuario: int`
- `-generos: Set<Genero>`

**Métodos:**
- `+obtenerTipo(): TipoAnime` (abstracto)
- `+obtenerDuracion(): int` (abstracto)
- `+obtenerDescripcionDuracion(): String` (abstracto)
- `+perteneceAGenero(Genero): boolean`
- `+lanzadoEntre(int, int): boolean`
- `+tituloContiene(String): boolean`
- `+cumpleCalificacionMinima(int): boolean`
- Getters y setters

#### AnimeSerie extends AnimeBase
**Atributos adicionales:**
- `-cantidadCapitulos: int`
- `-enEmision: boolean`

**Relaciones:**
- Hereda de AnimeBase (generalización)

#### AnimePelicula extends AnimeBase
**Atributos adicionales:**
- `-duracionMinutos: int`
- `-director: String`

**Relaciones:**
- Hereda de AnimeBase (generalización)

#### ListaPersonalizada implements Serializable
**Atributos:**
- `-nombre: String`
- `-descripcion: String`
- `-animes: List<AnimeBase>`

**Métodos:**
- `+agregarAnime(AnimeBase): boolean`
- `+removerAnime(AnimeBase): boolean`
- `+contieneAnime(AnimeBase): boolean`
- `+obtenerCantidadAnimes(): int`

**Relaciones:**
- Agregación con AnimeBase (0..* -- 0..*)
  - Una lista contiene múltiples anime
  - Un anime puede estar en múltiples listas

---

## 2. Paquete `repositorio` - Persistencia

### Interfaces

#### RepositorioAnime
- `+guardar(AnimeBase): void`
- `+guardarTodos(List<AnimeBase>): void`
- `+buscarPorTitulo(String): AnimeBase`
- `+obtenerTodos(): List<AnimeBase>`
- `+eliminarPorTitulo(String): boolean`
- `+existePorTitulo(String): boolean`
- `+contar(): int`

#### RepositorioListaPersonalizada
- `+guardar(ListaPersonalizada): void`
- `+buscarPorNombre(String): ListaPersonalizada`
- `+obtenerTodas(): List<ListaPersonalizada>`
- `+eliminarPorNombre(String): boolean`

### Implementaciones

#### RepositorioAnimeArchivo implements RepositorioAnime
- `-rutaArchivo: String`
- `-cache: List<AnimeBase>`
- Persiste en archivo binario usando serialización Java

#### RepositorioListaPersonalizadaArchivo implements RepositorioListaPersonalizada
- `-rutaArchivo: String`
- `-cache: List<ListaPersonalizada>`

---

## 3. Paquete `servicio` - Lógica de Negocio

#### ServicioAnime
**Atributos:**
- `-repositorioAnime: RepositorioAnime` (inyectado)

**Métodos:**
- `+registrarSerie(...)`: AnimeSerie
- `+registrarPelicula(...)`: AnimePelicula
- `+actualizarAnime(...)`: void
- `+eliminarAnime(String): boolean`
- `+buscarPorTitulo(String): List<AnimeBase>`
- `+filtrarPorGenero(Genero): List<AnimeBase>`
- `+busquedaAvanzada(FiltroAnime): List<AnimeBase>`
- `+ordenar(List, CriterioOrdenamiento): List<AnimeBase>`

**Relaciones:**
- Depende de RepositorioAnime (interfaz) → DIP

#### ServicioListaPersonalizada
**Atributos:**
- `-repositorioLista: RepositorioListaPersonalizada`
- `-repositorioAnime: RepositorioAnime`

**Métodos:**
- `+crearLista(String, String): ListaPersonalizada`
- `+agregarAnimeALista(String, String): boolean`
- `+removerAnimeDeLista(String, String): boolean`

#### ServicioRecomendacion
**Atributos:**
- `-repositorioAnime: RepositorioAnime`

**Métodos:**
- `+obtenerRecomendaciones(CriterioRecomendacion, int): List<AnimeBase>`
- `+obtenerTopGlobal(int): List<AnimeBase>`
- `+obtenerTopPorGenero(Genero, int): List<AnimeBase>`

#### ServicioEstadisticas
**Métodos:**
- `+obtenerPromedioCalificacionGlobal(): double`
- `+obtenerPromedioCalificacionPorGenero(Genero): double`
- `+obtenerCantidadPorEstado(): Map<Estado, Long>`
- `+obtenerTop3GenerosMasFrecuentes(): List<Entry<Genero, Long>>`

---

## 4. Paquete `utilidad` - Estrategias

### Interfaces

#### CriterioOrdenamiento extends Comparator<AnimeBase>
- `+comparar(AnimeBase, AnimeBase): int`
- `+obtenerDescripcion(): String`

#### CriterioRecomendacion
- `+recomendar(List<AnimeBase>, int): List<AnimeBase>`
- `+obtenerNombre(): String`
- `+obtenerDescripcion(): String`

### Implementaciones de Ordenamiento
- **OrdenamientoPorTitulo**: Alfabético
- **OrdenamientoPorCalificacion**: Por calificación (desc/asc)
- **OrdenamientoPorAnio**: Por año (desc/asc)

### Implementaciones de Recomendación
- **RecomendacionTopGlobal**: Top N de todo el catálogo
- **RecomendacionTopPorGenero**: Top N de un género específico
- **RecomendacionPorEstado**: Top N filtrado por estado

### Utilidades
#### FiltroAnime (Builder Pattern)
- `+porTitulo(String): FiltroAnime`
- `+porRangoAnios(Integer, Integer): FiltroAnime`
- `+porGenero(Genero): FiltroAnime`
- `+porEstado(Estado): FiltroAnime`
- `+porCalificacionMinima(Integer): FiltroAnime`
- `+construir(): Predicate<AnimeBase>`

---

## 5. Paquete `vista` - Interfaz Gráfica

#### VentanaPrincipal extends JFrame
- Ventana principal con JTabbedPane
- Coordina los paneles

#### PanelAnime extends JPanel
- Tabla de anime con filtros
- Diálogos de creación/edición

#### PanelListas extends JPanel
- Lista de listas personalizadas
- Tabla de anime por lista

#### PanelRecomendaciones extends JPanel
- Configuración de criterios
- Resultados de recomendaciones

#### PanelEstadisticas extends JPanel
- Tarjetas con métricas
- Gráficos de distribución

#### Diálogos
- **DialogoAnimeSerie**: Crear/editar serie
- **DialogoAnimePelicula**: Crear/editar película

---

## Relaciones Principales

```
┌────────────────────┐     usa      ┌─────────────────┐     implementa     ┌─────────────────────────┐
│  VentanaPrincipal  │─────────────>│  ServicioAnime  │<──────────────────│ RepositorioAnimeArchivo │
│  (Vista)           │              │  (Controlador)  │                    │ (Pure Fabrication)      │
└────────────────────┘              └─────────────────┘                    └─────────────────────────┘
                                           │                                         │
                                           │ depende de                              │ implementa
                                           ▼                                         ▼
                                    ┌─────────────────┐                    ┌──────────────────────┐
                                    │RepositorioAnime │                    │ <<interface>>        │
                                    │ (Interface)     │                    │ RepositorioAnime     │
                                    └─────────────────┘                    └──────────────────────┘

┌─────────────┐                                    ┌─────────────┐
│ AnimeSerie  │────────extends────────────────────>│ AnimeBase   │<───implements───┐
└─────────────┘                                    │ (abstract)  │                 │
                                                   └─────────────┘          ┌──────┴──────┐
┌──────────────┐                                         │                  │ Calificable │
│AnimePelicula │────────extends──────────────────────────┘                  │ (interface) │
└──────────────┘                                                            └─────────────┘

┌───────────────────┐      0..*        0..*      ┌─────────────┐
│ListaPersonalizada │◇─────────────────────────>│ AnimeBase   │
│                   │      contiene              └─────────────┘
└───────────────────┘
```

---

## Multiplicidades

| Relación | Multiplicidad |
|----------|---------------|
| ListaPersonalizada - AnimeBase | N:M (Un anime puede estar en múltiples listas) |
| AnimeBase - Genero | 1:N (Un anime tiene múltiples géneros) |
| Servicio - Repositorio | 1:1 (Inyección de dependencia) |
| VentanaPrincipal - Panel | 1:N (Composición) |

---

## Archivos UML PlantUML

Los diagramas están disponibles en formato PlantUML en la carpeta `docs/uml/`:

### Diagramas de Secuencia
| Archivo | Descripción |
|---------|-------------|
| `01_registrar_anime.puml` | Registrar nuevo anime (botón **[+]** verde) |
| `02_aplicar_filtro.puml` | Aplicar filtros y búsqueda |
| `05_recomendaciones.puml` | Obtener recomendaciones |
| `09_gestion_listas.puml` | Gestión de listas personalizadas |
| `11_editar_eliminar_calificar.puml` | Editar **[✏]**, Eliminar **[✕]** y Calificar **[★]** |

### Diagramas de Clases
| Archivo | Descripción |
|---------|-------------|
| `03_clases_modelo.puml` | Paquete modelo (AnimeBase, AnimeSerie, etc.) |
| `04_arquitectura.puml` | Arquitectura MVC por capas |
| `07_servicios.puml` | Servicios y repositorios |
| `08_utilidades_estrategias.puml` | Estrategias (ordenamiento, recomendación, filtros) |
| `10_vista_ui.puml` | Interfaz gráfica (paneles, diálogos, iconos) |
| `12_iconos_detalle.puml` | **Iconos personalizados** (IconoMas, IconoLapiz, IconoCruz, IconoEstrella) |

### Iconos de la Barra de Herramientas
| Icono | Color | Acción | Clase |
|-------|-------|--------|-------|
| **[+]** | 🟢 Verde | Nueva Serie / Nueva Película | `IconoMas` |
| **[✏]** | 🟠 Naranja | Editar anime | `IconoLapiz` |
| **[✕]** | 🔴 Rojo | Eliminar anime | `IconoCruz` |
| **[★]** | 🟡 Amarillo | Calificar anime | `IconoEstrella` |

