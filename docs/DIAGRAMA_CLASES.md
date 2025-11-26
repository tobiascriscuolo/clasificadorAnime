# Diagrama de Clases - Descripción Textual

## 1. Paquete `model` - Clases de Dominio

### Interfaces
- **Calificable** (interface)
  - `+getCalificacion(): int`
  - `+setCalificacion(int): void`
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
- `+getTipo(): TipoAnime` (abstracto)
- `+getDuracion(): int` (abstracto)
- `+getDescripcionDuracion(): String` (abstracto)
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
- `+getCantidadAnimes(): int`

**Relaciones:**
- Agregación con AnimeBase (0..* -- 0..*)
  - Una lista contiene múltiples anime
  - Un anime puede estar en múltiples listas

---

## 2. Paquete `repository` - Persistencia

### Interfaces

#### AnimeRepository
- `+save(AnimeBase): void`
- `+saveAll(List<AnimeBase>): void`
- `+findByTitulo(String): Optional<AnimeBase>`
- `+findAll(): List<AnimeBase>`
- `+deleteByTitulo(String): boolean`
- `+existsByTitulo(String): boolean`
- `+count(): int`

#### ListaPersonalizadaRepository
- `+save(ListaPersonalizada): void`
- `+findByNombre(String): Optional<ListaPersonalizada>`
- `+findAll(): List<ListaPersonalizada>`
- `+deleteByNombre(String): boolean`

### Implementaciones

#### FileAnimeRepository implements AnimeRepository
- `-filePath: String`
- `-cache: List<AnimeBase>`
- Persiste en archivo binario usando serialización Java

#### FileListaPersonalizadaRepository implements ListaPersonalizadaRepository
- `-filePath: String`
- `-cache: List<ListaPersonalizada>`

---

## 3. Paquete `service` - Lógica de Negocio

#### AnimeService
**Atributos:**
- `-animeRepository: AnimeRepository` (inyectado)

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
- Depende de AnimeRepository (interfaz) → DIP

#### ListaPersonalizadaService
**Atributos:**
- `-listaRepository: ListaPersonalizadaRepository`
- `-animeRepository: AnimeRepository`

**Métodos:**
- `+crearLista(String, String): ListaPersonalizada`
- `+agregarAnimeALista(String, String): boolean`
- `+removerAnimeDeLista(String, String): boolean`

#### RecomendacionService
**Atributos:**
- `-animeRepository: AnimeRepository`

**Métodos:**
- `+obtenerRecomendaciones(CriterioRecomendacion, int): List<AnimeBase>`
- `+getTopGlobal(int): List<AnimeBase>`
- `+getTopPorGenero(Genero, int): List<AnimeBase>`

#### EstadisticasService
**Métodos:**
- `+getPromedioCalificacionGlobal(): double`
- `+getPromedioCalificacionPorGenero(Genero): double`
- `+getCantidadPorEstado(): Map<Estado, Long>`
- `+getTop3GenerosMasFrecuentes(): List<Entry<Genero, Long>>`

---

## 4. Paquete `util` - Estrategias

### Interfaces

#### CriterioOrdenamiento extends Comparator<AnimeBase>
- `+compare(AnimeBase, AnimeBase): int`
- `+getDescripcion(): String`

#### CriterioRecomendacion
- `+recomendar(List<AnimeBase>, int): List<AnimeBase>`
- `+getNombre(): String`
- `+getDescripcion(): String`

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
- `+build(): Predicate<AnimeBase>`

---

## 5. Paquete `exception` - Excepciones

```
AnimeException (abstracta)
├── AnimeYaExistenteException
├── AnimeNoEncontradoException
├── ListaNoEncontradaException
├── ValidacionException
└── PersistenciaException
```

---

## 6. Paquete `ui` - Interfaz Gráfica

#### MainFrame extends JFrame
- Ventana principal con JTabbedPane
- Coordina los paneles

#### AnimePanel extends JPanel
- Tabla de anime con filtros
- Diálogos de creación/edición

#### ListasPanel extends JPanel
- Lista de listas personalizadas
- Tabla de anime por lista

#### RecomendacionesPanel extends JPanel
- Configuración de criterios
- Resultados de recomendaciones

#### EstadisticasPanel extends JPanel
- Tarjetas con métricas
- Gráficos de distribución

#### Diálogos
- **AnimeSerieDialog**: Crear/editar serie
- **AnimePeliculaDialog**: Crear/editar película

---

## Relaciones Principales

```
┌─────────────┐     usa      ┌───────────────┐     implementa     ┌──────────────────────┐
│  MainFrame  │─────────────>│ AnimeService  │<──────────────────│ FileAnimeRepository  │
│  (UI)       │              │ (Controller)  │                    │ (Pure Fabrication)   │
└─────────────┘              └───────────────┘                    └──────────────────────┘
                                    │                                       │
                                    │ depende de                            │ implementa
                                    ▼                                       ▼
                             ┌───────────────┐                    ┌──────────────────┐
                             │AnimeRepository│                    │ <<interface>>    │
                             │ (Interface)   │                    │ AnimeRepository  │
                             └───────────────┘                    └──────────────────┘

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
| Service - Repository | 1:1 (Inyección de dependencia) |
| MainFrame - Panel | 1:N (Composición) |

