# EIA ON AIR

Reproductor de música de escritorio desarrollado en **Java** con **Swing**, construido como proyecto del curso de Lenguajes y Compiladores de la Universidad EIA.

El objetivo del proyecto no es construir un reproductor comercial, sino demostrar la implementación y el comportamiento real de distintas estructuras de datos aplicadas a un mismo dominio: una biblioteca de canciones.

## Descripción

Cada canción que se agrega vive simultáneamente en **cuatro lugares**: la biblioteca general y las tres estructuras que soportan cada modo de reproducción, todas apuntando al mismo objeto `Cancion`. La aplicación separa completamente la lógica (estructuras de datos y reglas de reproducción) de la presentación (interfaz gráfica), siguiendo el paquete `main` únicamente para la vista.

## Modos de reproducción

El reproductor ofrece tres modos, cada uno respaldado por una estructura de datos distinta:

| Modo | Estructura | Comportamiento |
|------|-----------|-----------------|
| **Por orden de llegada** | Cola simple (`Cola`) | FIFO estricto. Solo permite avanzar; una vez reproducida, la canción sale de la cola. No permite retroceder. |
| **Aleatorio** | Lista circular doble (`ListaCircularDoble`) | Cada canción se inserta en una posición aleatoria de la lista al agregarse. La navegación es circular en ambas direcciones: nunca hay un final de reproducción. |
| **Alfabético** | Árbol binario de búsqueda (`ArbolBinarioBusqueda`) | Las canciones se ordenan por nombre y artista. La navegación respeta un recorrido inorden; al llegar a un extremo (primera o última canción), la reproducción se detiene ahí en lugar de dar la vuelta. |

## Funcionalidades

- Agregar, editar y eliminar canciones.
- Buscar canciones por nombre o artista en tiempo real.
- Mostrar la biblioteca completa en una tabla.
- Calificar una canción entre 0 y 100 mediante un diálogo con slider.
- Visualizar la canción actual (título, artista, álbum y portada genérica).
- Barra de progreso y temporizador simulados (play/pausa/siguiente/anterior), sin reproducción de audio real.
- Selector de modo de reproducción con descripción de la estructura activa.
- Interfaz en modo oscuro, inspirada en Apple Music.

## Estructura del proyecto

```
src/
├── Estructuras/
│   ├── Nodo.java                   Nodo genérico enlazado (usado por Cola y ListaCircularDoble)
│   ├── NodoArbol.java               Nodo del árbol binario de búsqueda
│   ├── Cola.java                    Cola simple (FIFO)
│   ├── ListaCircularDoble.java      Lista circular doblemente enlazada
│   └── ArbolBinarioBusqueda.java    Árbol binario de búsqueda genérico
├── Exceptions/
│   ├── EVacia.java                  Campos vacíos / estructura vacía
│   ├── ENumeroNegativo.java         Valores numéricos negativos
│   ├── ECalificacion.java           Calificación fuera del rango 0-100
│   ├── EAnioInvalido.java           Año de lanzamiento inválido
│   ├── ECancion.java                Canción no encontrada
│   └── EPosicion.java               Posición de inserción inválida
├── modelo/
│   ├── Cancion.java                 Entidad canción (Comparable por nombre + artista)
│   └── Biblioteca.java              Colección general de canciones (ArrayList)
├── reproduccion/
│   ├── ModoReproduccion.java        Clase abstracta base de los tres modos
│   ├── ModoLlegada.java             Modo por orden de llegada (Cola)
│   ├── ModoAleatorio.java           Modo aleatorio (ListaCircularDoble)
│   └── ModoAlfabetico.java          Modo alfabético (ArbolBinarioBusqueda)
└── main/
    └── VentanaPrincipal.java        Interfaz gráfica (Swing)
```

## Requisitos

- JDK 17 o superior.
- IntelliJ IDEA (el proyecto incluye configuración `.idea` y archivo `.iml`).

## Ejecución

1. Clona el repositorio o descarga el proyecto.
2. Ábrelo en IntelliJ IDEA como proyecto existente.
3. Ejecuta la clase `main.VentanaPrincipal`.

No requiere dependencias externas ni gestor de builds (Maven/Gradle): el proyecto compila con el JDK estándar.

## Diseño técnico

- **Encapsulamiento**: todos los atributos de `Cancion` y las estructuras de datos son privados, expuestos mediante getters/setters con validación.
- **Herencia**: `ModoLlegada`, `ModoAleatorio` y `ModoAlfabetico` heredan de la clase abstracta `ModoReproduccion`.
- **Polimorfismo**: `VentanaPrincipal` opera sobre la referencia `ModoReproduccion modoActivo` sin conocer la estructura interna de cada modo.
- **Genéricos**: `ArbolBinarioBusqueda<T extends Comparable<T>>`, `ListaCircularDoble<T>`, `Cola<Q>` y `Nodo<E>` son reutilizables para cualquier tipo de dato.
- **Excepciones personalizadas**: cada validación de negocio (campos vacíos, números negativos, calificación fuera de rango, posición inválida, canción no encontrada) lanza una excepción propia en `Exceptions/`.
- **Separación lógica/presentación**: ninguna clase de `Estructuras`, `modelo` o `reproduccion` depende de Swing; toda la interfaz vive aislada en `main.VentanaPrincipal`.

## Complejidad de las operaciones principales

| Estructura | Inserción | Eliminación | Búsqueda | Recorrido |
|------------|-----------|-------------|----------|-----------|
| Cola simple | O(1) | O(n)* | — | O(n) |
| Lista circular doble | O(n)** | O(1) con referencia al nodo | O(n) | O(n) |
| Árbol binario de búsqueda | O(log n) promedio / O(n) peor caso | O(log n) promedio / O(n) peor caso | O(log n) promedio / O(n) peor caso | O(n) |

\* Eliminar una canción que no está en el frente de la cola requiere reconstruirla completa, ya que una cola simple solo opera sobre sus extremos.
\** La inserción en el modo aleatorio es O(n) porque primero se recorre hasta la posición aleatoria elegida; la cola y la lista en sí insertan en O(1) en sus extremos.

## Autores

Proyecto desarrollado para el curso de Lenguajes y Compiladores — Universidad EIA.
