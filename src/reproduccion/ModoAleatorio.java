package reproduccion;

import Estructuras.ListaCircularDoble;
import Estructuras.Nodo;
import modelo.Cancion;

import java.util.Random;

//Responsable de controlar el comportamiento del modo aleatorio
// con la ayuda de la clase de ListaDobleCircular
public class ModoAleatorio extends ModoReproduccion {

    private ListaCircularDoble<Cancion> lista;
    private Nodo<Cancion> nodoActual; //el cursor que marca en que parte de la lista vamos
    private Random random;

    public ModoAleatorio() {
        lista = new ListaCircularDoble<>();
        random = new Random();
        nodoActual = null;
        actual = null;
    }

    @Override
    public void agregarCancion(Cancion cancion) {
        //Insertamos en una posicion al azar para que la lista quede desordenada desde que se arma
        int posicion = random.nextInt(lista.getTamano() + 1);
        lista.insertarEnPosicion(posicion, cancion);

        //Si era la primera cancion el cursor arranca en la cabeza
        if (nodoActual == null) {
            nodoActual = lista.getCabeza();
            actual = nodoActual.getDato();
        }
    }

    @Override
    public Cancion siguiente() {
        if (nodoActual == null) {
            return null; //la lista esta vacia, no hay nada que reproducir
        }

        //Como la lista es circular nunca llegamos a null, al final volvemos a la cabeza sola
        nodoActual = nodoActual.getSiguiente();
        actual = nodoActual.getDato();
        return actual;
    }

    @Override
    public Cancion anterior() {
        if (nodoActual == null) {
            return null;
        }

        //Mismo caso pero al reves, desde la cabeza saltamos al ultimo nodo
        nodoActual = nodoActual.getAnterior();
        actual = nodoActual.getDato();
        return actual;
    }
}