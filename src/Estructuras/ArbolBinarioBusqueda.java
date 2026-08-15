package Estructuras;

import modelo.Cancion;

//Implementa el arbol binario de busqueda
//Su responsabilidad es mantener los elementos ordenados según su criterio de comparación.
public class ArbolBinarioBusqueda<T extends Comparable<T>> {

    private NodoArbol<T> raiz;
    private int tamano;
    private NodoArbol<T> actual;
    public ArbolBinarioBusqueda() {
        raiz = null;
        tamano = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getTamano() {
        return tamano;
    }

    public void insertar(T valor) {
        if (raiz == null) {
            raiz = new NodoArbol<>(valor);
            tamano++;
        } else {
            insertarRecursivo(raiz, valor);
        }
    }

    private NodoArbol<T> insertarRecursivo(NodoArbol<T> actual, T valor) {
        if (actual == null) {
            return new NodoArbol<>(valor);
        }

        // Usamos compareTo() en lugar de < o >
        // compareTo() devuelve < 0 si 'valor' es menor que 'actual.valor'
        if (valor.compareTo(actual.getDato()) < 0) {
            actual.setIzquierda(insertarRecursivo(actual.getIzquierda(), valor));
        }
        // compareTo() devuelve > 0 si 'valor' es mayor que 'actual.valor'
        else if (valor.compareTo(actual.getDato()) > 0) {
            actual.setDerecha(insertarRecursivo(actual.getDerecha(), valor));
        }

        return actual;
    }

    public boolean buscar(T valor) {
        return buscarRecursivo(raiz, valor);
    }

    /**
     * Lógica recursiva de búsqueda:
     * - Si el nodo actual es null, el valor NO está en el árbol.
     * - Si el valor es igual al nodo actual, lo encontramos.
     * - Si el valor es menor, buscamos en el subárbol izquierdo.
     * - Si el valor es mayor, buscamos en el subárbol derecho.
     */
    private boolean buscarRecursivo(NodoArbol<T> actual, T valor) {
        // Caso base: llegamos a un nodo nulo → no existe
        if (actual == null) {
            return false;
        }

        int comparacion = valor.compareTo(actual.getDato());

        if (comparacion == 0) {
            // ¡Encontrado!
            return true;
        } else if (comparacion < 0) {
            // El valor buscado es menor → ir a la izquierda
            return buscarRecursivo(actual.getIzquierda(), valor);
        } else {
            // El valor buscado es mayor → ir a la derecha
            return buscarRecursivo(actual.getDerecha(), valor);
        }
    }

    public void recorridoInorden() {
        inOrden(raiz);
    }

    private void inOrden(NodoArbol<T> nodo) {
        if (nodo != null) {
            inOrden(nodo.getIzquierda());
            System.out.print(nodo.getDato() + " ");
            inOrden(nodo.getDerecha());
        }
    }

    public NodoArbol<T> eliminar(T valor) {
        if (buscar(valor)) {
            raiz = eliminarRecursivo(raiz, valor);
            tamano--;
        }
        return raiz;
    }
    private NodoArbol<T> eliminarRecursivo(NodoArbol<T> actual, T valor) {

        if (actual == null) {
            return null;
        }

        int comparacion = valor.compareTo(actual.getDato());

        if (comparacion < 0) {

            actual.setIzquierda(
                    eliminarRecursivo(actual.getIzquierda(), valor)
            );

        } else if (comparacion > 0) {

            actual.setDerecha(
                    eliminarRecursivo(actual.getDerecha(), valor)
            );

        } else {

            // Caso 1: no tiene hijos
            if (actual.getIzquierda() == null &&
                    actual.getDerecha() == null) {

                return null;
            }

            // Caso 2: solamente tiene hijo derecho
            if (actual.getIzquierda() == null) {
                return actual.getDerecha();
            }

            // Caso 2: solamente tiene hijo izquierdo
            if (actual.getDerecha() == null) {
                return actual.getIzquierda();
            }

            // Caso 3: tiene dos hijos
            T sucesor = encontrarMinimo(actual.getDerecha());

            actual.setDato(sucesor);

            actual.setDerecha(
                    eliminarRecursivo(actual.getDerecha(), sucesor)
            );
        }

        return actual;
    }

    public T obtenerMinimo(){
        if(raiz==null){
            return null;
        }
        return encontrarMinimo(raiz);
    }

    private T encontrarMinimo(NodoArbol<T> nodo) {

        while (nodo.getIzquierda() != null) {
            nodo = nodo.getIzquierda();
        }

        return nodo.getDato();
    }

    public T obtenerSiguiente(T valor) {

        return obtenerSiguienteRecursivo(raiz, valor, null);
    }

    private T obtenerSiguienteRecursivo(
            NodoArbol<T> actual,
            T valor,
            T sucesor) {

        if (actual == null) {
            return sucesor;
        }

        int comparacion = valor.compareTo(actual.getDato());

        if (comparacion < 0) {

            sucesor = actual.getDato();

            return obtenerSiguienteRecursivo(
                    actual.getIzquierda(),
                    valor,
                    sucesor
            );

        } else if (comparacion > 0) {

            return obtenerSiguienteRecursivo(
                    actual.getDerecha(),
                    valor,
                    sucesor
            );

        } else {

            if (actual.getDerecha() != null) {
                return encontrarMinimo(actual.getDerecha());
            }

            return sucesor;
        }
    }

    public T obtenerAnterior(T valor) {

        return obtenerAnteriorRecursivo(raiz, valor, null);
    }

    private T obtenerAnteriorRecursivo(
            NodoArbol<T> actual,
            T valor,
            T anterior) {

        if (actual == null) {
            return anterior;
        }

        int comparacion = valor.compareTo(actual.getDato());

        if (comparacion > 0) {

            anterior = actual.getDato();

            return obtenerAnteriorRecursivo(
                    actual.getDerecha(),
                    valor,
                    anterior
            );

        } else if (comparacion < 0) {

            return obtenerAnteriorRecursivo(
                    actual.getIzquierda(),
                    valor,
                    anterior
            );

        } else {

            if (actual.getIzquierda() != null) {

                NodoArbol<T> nodo = actual.getIzquierda();

                while (nodo.getDerecha() != null) {
                    nodo = nodo.getDerecha();
                }

                return nodo.getDato();
            }

            return anterior;
        }
    }
}