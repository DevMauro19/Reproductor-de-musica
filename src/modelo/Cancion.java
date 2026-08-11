package modelo;

//Clase que representa la información de una canción
public class Cancion {
    private String nombre;
    private String artista;
    private String album;
    private int duracionEnSegundos;
    private String genero;
    private int anioLanzamiento;
    private int calificacion;

    public Cancion(String nombre, String artista, String album, int duracionEnSegundos, String genero, int anioLanzamiento) {
        this.nombre = nombre;
        this.artista = artista;
        album = album;
        this.duracionEnSegundos = duracionEnSegundos;
        this.genero = genero;
        this.anioLanzamiento = anioLanzamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuracionEnSegundos() {
        return duracionEnSegundos;
    }

    public void setDuracionEnSegundos(int duracionEnSegundos) {
        this.duracionEnSegundos = duracionEnSegundos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(int anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
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
}
