package modelo;

import Exceptions.ECancion;

import java.util.ArrayList;
import java.util.ListIterator;

public class Biblioteca {


    private ArrayList<Cancion> canciones;

    public Biblioteca() {
        canciones = new ArrayList<>();
    }

    public void agregarCancion(Cancion cancion) {
        canciones.add(cancion);
    }

    public void eliminarCancion(Cancion cancion) {
        canciones.remove(cancion);
    }

    public Cancion buscarCancion(String nombre) throws ECancion{


        ListIterator <Cancion> iterator=canciones.listIterator();
        Cancion x=new Cancion();
        while(iterator.hasNext()&&!x.getNombre().equalsIgnoreCase(nombre)){
            x=iterator.next();
        }
        if(x!=null){
            return x;
        }else{
            throw new ECancion(nombre);
        }
    }

    public ArrayList<Cancion> getCanciones() {
        return canciones;
    }

    public boolean estaVacia() {
        return canciones.isEmpty();
    }

    public int getTamano() {
        return canciones.size();
    }
}
