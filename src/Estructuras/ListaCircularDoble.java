package Estructuras;

import modelo.Cancion;
//La responsabilidad de esta clase es administrar una colección de elementos conectados circularmente en ambas direcciones
public class ListaCircularDoble<T> {
    private Nodo<T> cabeza;
    private int tamano;

    public ListaCircularDoble() {
        this.cabeza = null;
        tamano=0;
    }
    public boolean estaVacia(){
        return cabeza==null; //compara y devuelve el valor de TRUE si la cabeza es nula
    }

    public int getTamano(){
        return this.tamano;
    }

    public void insertarInicio(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);

        if (cabeza == null) {
            // Lista vacía: esto significa que el nodo apunta a si mismo en ambas direcciones
            cabeza = nuevo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
        } else {
            Nodo<T> cola = cabeza.getAnterior(); // el nodo anterior a la cabeza

            // Conectamos el nuevo nodo entre la cola y la cabeza antigua
            nuevo.setSiguiente(cabeza);      // nuevo -> cabeza_antigua
            nuevo.setAnterior(cola);       // cola <- nuevo

            cabeza.setAnterior(nuevo);     // nuevo <- cabeza
            cola.setSiguiente(nuevo);    // nuevo -> cabeza_antigua

            // Actualizamos la cabeza
            cabeza=nuevo;
        }
        tamano++;
    }
    public void insertarFinal(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);

        if (cabeza == null) {
            cabeza = nuevo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cabeza);
        } else {
            Nodo<T> cola = cabeza.getAnterior(); // Accesible en O(1)

            // Conectamos el nuevo nodo entre la cola y la cabeza
            cola.setSiguiente(nuevo);      // cola -> nuevo
            nuevo.setAnterior(cola);       // cola <- nuevo
            nuevo.setSiguiente(cabeza);    // nuevo -> cabeza
            cabeza.setAnterior(nuevo);     // nuevo <- cabeza
            // La cabeza NO cambia (a diferencia de insertarInicio)
        }
        tamano++;
    }

    public void insertarEnPosicion(int posicion, T valor) {
        if (posicion < 0 || posicion > tamano) {
            System.out.println("⚠ Posición " + posicion + " inválida. "
                    + "Rango válido: [0, " + tamano + "]");
            return;
        }

        if (posicion == 0) {
            insertarInicio(valor);
            return;
        }

        if (posicion == tamano) {
            insertarFinal(valor);
            return;
        }

        // Recorremos hasta el nodo ANTERIOR a la posición deseada
        Nodo<T> nuevo = new Nodo<>(valor);
        Nodo<T> anterior = cabeza;

        for (int i = 0; i < posicion - 1; i++) {
            anterior = anterior.getSiguiente();
        }

        // Guardamos el nodo que está después del anterior
        Nodo<T> sigDeAnt = anterior.getSiguiente();

        // Enlazamos (4 punteros)
        anterior.setSiguiente(nuevo);     // anterior -> nuevo
        nuevo.setAnterior(anterior);      // anterior <- nuevo
        nuevo.setSiguiente(sigDeAnt);     // nuevo -> sigDeAnt
        sigDeAnt.setAnterior(nuevo);      // nuevo <- sigDeAnt
        tamano++;
    }

    //Recorrido hacia delante
    public void recorrerAdelante() {
        if (estaVacia()) {
            return;
        }

        Nodo<T> actual = cabeza;

        do {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        } while (actual != cabeza);
    }

    //Recorrer hacia a atras
    public void recorrerAtras() {
        if (estaVacia()) {
            return;
        }

        Nodo<T> actual = cabeza.getAnterior();

        do {
            System.out.println(actual.getDato());
            actual = actual.getAnterior();
        } while (actual != cabeza.getAnterior());
    }

}
