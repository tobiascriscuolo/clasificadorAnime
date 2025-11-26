# Justificación de Diseño - Defensa Oral

Este documento proporciona los argumentos para defender cada decisión de diseño en la defensa oral del TP.

---

## 1. Arquitectura por Capas

### Pregunta: ¿Por qué usar arquitectura por capas?

**Respuesta:**
Se eligió arquitectura por capas porque:

1. **Separación de responsabilidades**: Cada capa tiene un propósito específico:
   - `model`: Representa el dominio del problema
   - `service`: Contiene la lógica de negocio
   - `repository`: Maneja la persistencia
   - `ui`: Interfaz de usuario

2. **Bajo acoplamiento**: Cambiar la implementación de persistencia (por ejemplo, de archivos a base de datos) no afecta a la UI ni a los servicios.

3. **Facilita testing**: Podemos probar servicios con repositorios mock.

4. **SOLID - SRP**: Cada capa tiene una única responsabilidad.

---

## 2. Uso de Interfaces para Repositorios

### Pregunta: ¿Por qué AnimeRepository es una interfaz?

**Respuesta:**
Aplicando **SOLID - DIP (Dependency Inversion Principle)**:

```java
// El servicio depende de la abstracción
public class AnimeService {
    private final AnimeRepository animeRepository; // Interfaz
    
    public AnimeService(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }
}
```

Beneficios:
- Podemos cambiar de `FileAnimeRepository` a `DatabaseAnimeRepository` sin modificar el servicio.
- Facilita testing con mocks.
- Es un **GRASP - Protected Variations**: aisla los cambios de implementación.

---

## 3. Herencia: AnimeBase, AnimeSerie, AnimePelicula

### Pregunta: ¿Por qué usar herencia aquí?

**Respuesta:**
Se aplica **SOLID - LSP (Liskov Substitution Principle)**:

```java
public abstract class AnimeBase { ... }
public class AnimeSerie extends AnimeBase { ... }
public class AnimePelicula extends AnimeBase { ... }
```

- **AnimeSerie** y **AnimePelicula** pueden sustituir a **AnimeBase** sin romper el código.
- Los servicios trabajan con `AnimeBase`, lo que permite polimorfismo.
- Evita duplicación de código (atributos y métodos comunes en la clase base).

Ejemplo de uso:
```java
List<AnimeBase> catalogo; // Puede contener series y películas
for (AnimeBase anime : catalogo) {
    System.out.println(anime.getTitulo()); // Funciona igual para ambos
}
```

---

## 4. Interfaz Calificable

### Pregunta: ¿Por qué crear una interfaz Calificable?

**Respuesta:**
Aplicando **SOLID - ISP (Interface Segregation Principle)**:

```java
public interface Calificable {
    int getCalificacion();
    void setCalificacion(int calificacion);
    boolean tieneCalificacion();
}
```

- Interfaz pequeña y cohesiva.
- Si en el futuro queremos que otras entidades sean calificables (ej: estudios, directores), solo implementan esta interfaz.
- No forzamos a implementar métodos que no necesitan.

---

## 5. Patrón Strategy para Recomendaciones

### Pregunta: ¿Cómo se aplica el patrón Strategy?

**Respuesta:**
```java
public interface CriterioRecomendacion {
    List<AnimeBase> recomendar(List<AnimeBase> animes, int cantidad);
}

public class RecomendacionTopGlobal implements CriterioRecomendacion { ... }
public class RecomendacionTopPorGenero implements CriterioRecomendacion { ... }
```

Aplicamos:
- **SOLID - OCP**: Para agregar un nuevo criterio (ej: `RecomendacionPorEstudio`), creamos una nueva clase sin modificar las existentes.
- **GRASP - Polymorphism**: El servicio trata todos los criterios de forma uniforme.
- **GRASP - Protected Variations**: Los cambios en un criterio no afectan a otros.

Uso:
```java
// El servicio recibe cualquier estrategia
CriterioRecomendacion criterio = new RecomendacionTopPorGenero(Genero.SHONEN);
List<AnimeBase> recomendados = service.obtenerRecomendaciones(criterio, 5);
```

---

## 6. Builder Pattern para Filtros

### Pregunta: ¿Por qué usar un Builder para FiltroAnime?

**Respuesta:**
```java
FiltroAnime filtro = new FiltroAnime()
    .porGenero(Genero.SHONEN)
    .porCalificacionMinima(4)
    .porEstado(Estado.FINALIZADO)
    .build();
```

Beneficios:
- **Legibilidad**: El código expresa claramente qué filtros se aplican.
- **Flexibilidad**: Se pueden combinar criterios de forma arbitraria.
- **Evita constructores con muchos parámetros**.
- Internamente usa `Predicate<AnimeBase>` para composición funcional.

---

## 7. AnimeService como GRASP Controller

### Pregunta: ¿Por qué AnimeService coordina los casos de uso?

**Respuesta:**
Aplicando **GRASP - Controller**:

```java
public class AnimeService {
    public AnimeSerie registrarSerie(...) {
        validarTitulo(titulo);         // Validación
        validarAnio(anio);
        if (repository.existsByTitulo(titulo)) {
            throw new AnimeYaExistenteException(titulo);
        }
        AnimeSerie serie = new AnimeSerie(...); // Creación
        repository.save(serie);        // Persistencia
        return serie;
    }
}
```

El servicio:
- **No es UI**: La UI solo captura datos y muestra resultados.
- **Coordina**: Llama a validaciones, crea objetos, usa repositorios.
- **Es el punto de entrada** para los casos de uso del sistema.
- Mantiene **alta cohesión** (solo lógica de anime).

---

## 8. Information Expert en AnimeBase

### Pregunta: ¿Qué métodos aplican Information Expert?

**Respuesta:**
```java
public abstract class AnimeBase {
    // El anime conoce sus géneros, entonces él responde esta pregunta
    public boolean perteneceAGenero(Genero genero) {
        return generos.contains(genero);
    }
    
    // El anime conoce su título
    public boolean tituloContiene(String texto) {
        return titulo.toLowerCase().contains(texto.toLowerCase());
    }
    
    // El anime conoce su calificación
    public boolean cumpleCalificacionMinima(int minima) {
        return tieneCalificacion() && calificacionUsuario >= minima;
    }
}
```

**GRASP - Information Expert**: La responsabilidad se asigna a la clase que tiene la información necesaria.

---

## 9. Pure Fabrication en Repositorios

### Pregunta: ¿Por qué FileAnimeRepository es una Pure Fabrication?

**Respuesta:**
```java
public class FileAnimeRepository implements AnimeRepository {
    private final String filePath;
    private List<AnimeBase> cache;
    // ...
}
```

- **No representa un concepto del dominio** (no existe "repositorio" en el mundo del anime).
- **Es una invención técnica** para resolver un problema de diseño: dónde poner la lógica de persistencia.
- Mejora la cohesión: el modelo no sabe cómo se persiste.
- Reduce acoplamiento: los servicios no conocen detalles de archivos.

---

## 10. Excepciones Personalizadas

### Pregunta: ¿Por qué crear excepciones propias?

**Respuesta:**
```java
public abstract class AnimeException extends Exception { ... }
public class AnimeYaExistenteException extends AnimeException { ... }
public class ValidacionException extends AnimeException { ... }
```

Beneficios:
- **Claridad semántica**: El nombre indica qué pasó.
- **Información adicional**: Pueden llevar datos (ej: `getTitulo()`).
- **Jerarquía**: Se pueden capturar por tipo específico o general.
- **Separación de errores de negocio vs técnicos**.

En la UI:
```java
try {
    animeService.registrarSerie(...);
} catch (AnimeYaExistenteException e) {
    mostrarMensaje("El anime ya existe: " + e.getTitulo());
} catch (ValidacionException e) {
    mostrarMensaje("Error de validación: " + e.getMessage());
}
```

---

## 11. UI Separada de Lógica

### Pregunta: ¿Cómo se separa la UI de la lógica?

**Respuesta:**
Los listeners en la UI son simples:

```java
btnGuardar.addActionListener(e -> {
    // 1. Capturar datos de la vista
    String titulo = txtTitulo.getText();
    int anio = (Integer) spnAnio.getValue();
    
    try {
        // 2. Llamar al servicio (NO hay lógica de negocio aquí)
        animeService.registrarSerie(titulo, anio, ...);
        
        // 3. Mostrar resultado
        JOptionPane.showMessageDialog(this, "Serie creada");
        refrescar();
    } catch (AnimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
});
```

La UI:
- **NO valida datos** (eso lo hace el servicio)
- **NO accede a repositorios** (eso lo hace el servicio)
- **NO conoce la estructura de persistencia**
- Solo coordina la interacción usuario ↔ servicios

---

## 12. Relación N:M entre ListaPersonalizada y Anime

### Pregunta: ¿Cómo se modela que un anime esté en varias listas?

**Respuesta:**
```java
public class ListaPersonalizada {
    private List<AnimeBase> animes; // Referencias a objetos Anime
    
    public boolean agregarAnime(AnimeBase anime) {
        if (!contieneAnime(anime)) {
            return animes.add(anime);
        }
        return false;
    }
}
```

- La lista **contiene referencias** a objetos `AnimeBase`.
- Un mismo objeto `AnimeBase` puede estar referenciado por múltiples listas.
- Es una **agregación**, no composición: si se elimina la lista, el anime sigue existiendo.
- Usar `List` (no `Set`) permite ordenamiento personalizado futuro.

---

## Resumen de Patrones Aplicados

| Patrón/Principio | Ubicación | Justificación |
|------------------|-----------|---------------|
| SRP | Todas las clases | Cada clase tiene una sola responsabilidad |
| OCP | Estrategias | Nuevos criterios sin modificar existentes |
| LSP | AnimeSerie/Pelicula | Sustituyen a AnimeBase correctamente |
| ISP | Calificable | Interfaz pequeña y específica |
| DIP | Services → Repositories | Dependen de interfaces |
| Controller | *Service | Coordinan casos de uso |
| Information Expert | AnimeBase | Responde sobre sí mismo |
| Creator | *Service | Crean instancias de modelo |
| Pure Fabrication | *Repository | Clases técnicas de persistencia |
| Polymorphism | Estrategias | Criterios intercambiables |
| Protected Variations | Interfaces | Aíslan cambios |
| Low Coupling | Capas | Mínimas dependencias |
| High Cohesion | Clases | Métodos relacionados juntos |

