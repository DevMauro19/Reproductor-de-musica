package Estructuras;

//La responsabilidad de esta clase es representar un árbol
public class NodoArbol<A> {

    private A dato;
    private NodoArbol<A> hijoIzq;
    private NodoArbol<A> hijoDer;

    public NodoArbol(A dato) {
        this.dato = dato;
        this.hijoIzq = null;
        this.hijoDer = null;
    }

    public A getDato() {
        return dato;
    }

    public void setDato(A dato) {
        this.dato = dato;
    }

    public NodoArbol<A> getIzquierda() {
        return hijoIzq;
    }

    public void setIzquierda(NodoArbol<A> izquierda) {
        this.hijoIzq = izquierda;
    }

    public NodoArbol<A> getDerecha() {
        return hijoDer;
    }

    public void setDerecha(NodoArbol<A> derecha) {
        this.hijoDer = derecha;
    }
}