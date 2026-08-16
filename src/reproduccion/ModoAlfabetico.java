package reproduccion;
import Estructuras.ArbolBinarioBusqueda;
import modelo.Cancion;

//Su responsabilidad es reproducir las canciones en orden alfabetico

public class ModoAlfabetico extends ModoReproduccion {

    //crear el arbol de reproduccion
    private ArbolBinarioBusqueda<Cancion> arbol;

    //Constructor
    public ModoAlfabetico() {
        arbol = new ArbolBinarioBusqueda<>();
        actual = null;
    }

    //Agregar Canciones
    @Override
    public void agregarCancion(Cancion cancion) {
        arbol.insertar(cancion);

        // Si es la primera canción, comenzamos
        // por la primera alfabéticamente.
        if (actual == null) {
            actual = arbol.obtenerMinimo();
        }
    }

    //Pasar a la siguiente
    @Override
    public Cancion siguiente() {

        if (actual == null) {
            return null;
        }

        Cancion siguiente = arbol.obtenerSiguiente(actual);

        if (siguiente != null) {
            actual = siguiente;
        }

        return actual;
    }

    //Pasar a la anterior
    @Override
    public Cancion anterior() {

        if (actual == null) {
            return null;
        }

        Cancion anterior = arbol.obtenerAnterior(actual);

        if (anterior != null) {
            actual = anterior;
        }

        return actual;
    }

    //obtener la cancion actual
    @Override
    public Cancion getActual() {
        return actual;
    }

    //Eliminar cancion
    @Override
    public Cancion eliminarCancion(Cancion c) {
        // La canción no existe en el árbol
        if (!arbol.buscar(c)) {
            return null;
        }

        // Si estamos eliminando la canción actual,
        // obtenemos la siguiente ANTES de eliminarla.
        Cancion siguiente = null;

        if (actual != null && actual.compareTo(c) == 0) {
            siguiente = arbol.obtenerSiguiente(actual);
        }

        // Eliminamos la canción
        arbol.eliminar(c);

        // Si eliminamos la canción actual
        if (actual != null && actual.compareTo(c) == 0) {
            actual = siguiente;
        }

        return c;
    }
}