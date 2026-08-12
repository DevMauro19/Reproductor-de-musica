package reproduccion;

import modelo.Cancion;

//Escoger el modo de reproducción
public abstract class ModoReproduccion {

    public abstract void agregarCancion(Cancion cancion);

    public abstract Cancion siguiente();

    public abstract Cancion anterior();

    public abstract Cancion getActual();

}
