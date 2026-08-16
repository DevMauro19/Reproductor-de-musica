package modelo;

import Exceptions.EAnioInvalido;
import Exceptions.ECalificacion;
import Exceptions.ENumeroNegativo;
import Exceptions.EVacia;

import java.util.Locale;

//Clase que representa la información de una canción
public class Cancion implements Comparable<Cancion> {
    private String nombre;
    private String artista;
    private String album;
    private int duracionEnSegundos;
    private String genero;
    private int anioLanzamiento;
    private int calificacion;

    public Cancion(){}

    public Cancion(String nombre, String artista, String album, int duracionEnSegundos, String genero, int anioLanzamiento) throws EVacia, ENumeroNegativo {

        if(nombre.isEmpty()||nombre.trim().isEmpty())throw new EVacia("El nombre de la cancion no puede estar vacio");
        if(artista.isEmpty()||artista.trim().isEmpty()) throw new EVacia("El nombre del artista no puede ser vacio");
        if(album.isEmpty()||album.trim().isEmpty()) throw new EVacia("La cancion no puede no pertenecer a algun album");
        if(duracionEnSegundos<0) throw new ENumeroNegativo("La duración no puede ser negativa el valor ingrsado fue: "+duracionEnSegundos);
        if(genero.isEmpty()||genero.trim().isEmpty()) throw new EVacia("El genro no puede ser vacio");
        if(anioLanzamiento<0) throw new ENumeroNegativo(" El anio no puede ser negativo, el anio ingresado fue: "+anioLanzamiento);

        this.nombre = nombre;
        this.artista = artista;
        this.album = album;
        this.duracionEnSegundos = duracionEnSegundos;
        this.genero = genero;
        this.anioLanzamiento = anioLanzamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre)throws EVacia {
        if(nombre==null || nombre.isEmpty()){
            throw new EVacia("El nombre no puede estar vacio");
        }
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) throws EVacia {
        if(artista==null || artista.isEmpty()){
            throw new EVacia("El artista no puede estar en vacio");
        }
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) throws EVacia {
        if(album==null || album.isEmpty()){
            throw new EVacia("El campo del album no puede estar vacio");
        }
        this.album = album;
    }

    public int getDuracionEnSegundos() {
        return duracionEnSegundos;
    }

    public void setDuracionEnSegundos(int duracionEnSegundos)throws ENumeroNegativo{
        if(duracionEnSegundos<0){
            throw new ENumeroNegativo("La duracion no puede ser negativa");
        }
        this.duracionEnSegundos = duracionEnSegundos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) throws EVacia {
        if (genero == null || genero.isEmpty()) {
            throw new EVacia("El campo de genero no puede estar vacio");
        }

        this.genero = genero;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(int anioLanzamiento) throws ENumeroNegativo {
        if(anioLanzamiento<0){
            throw new ENumeroNegativo("El anio de lanzamiento no puede ser negativo");
        }
        this.anioLanzamiento = anioLanzamiento;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) throws ECalificacion {
        if(calificacion<0 || calificacion>100){
            throw new ECalificacion(calificacion);
        }
        this.calificacion = calificacion;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "nombre='" + nombre + '\'' +
                ", artista='" + artista + '\'' +
                ", album='" + album + '\'' +
                ", duracionEnSegundos=" + duracionEnSegundos +
                ", genero='" + genero + '\'' +
                ", anioLanzamiento=" + anioLanzamiento +
                ", calificacion=" + calificacion +
                '}';
    }
    //Dos canciones se consideran iguales si tienen el mismo nombre
    //Lo necesitamos para que buscar() y eliminar() comparen contenido y no direcciones de memoria
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Cancion otra = (Cancion) obj;
        return this.nombre.equalsIgnoreCase(otra.nombre);
    }

    //Si sobreescribimos equals hay que sobreescribir hashCode, es el contrato de Java
    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }
    @Override
    public int compareTo(Cancion o) {

        //tenemos que tener en cuenta que el nombre de la cancion se puede repetir
        //por lo que también compararemos por el artista

        int comparacionNombre=this.nombre.compareToIgnoreCase(o.nombre);

        if(comparacionNombre!=0){
            return comparacionNombre;
        }

        return this.artista.compareToIgnoreCase(o.artista);
    }
}
