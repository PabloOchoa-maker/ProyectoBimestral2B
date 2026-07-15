package streamflow.modelo;

public class Serie extends Contenido {

    private int temporadas;
    private int episodios;

    public Serie(String id, String titulo, Genero genero, Calidad calidad,
                 int duracionMin, double costoBase, int temporadas, int episodios) {
        super(id, titulo, genero, calidad, duracionMin, costoBase);
        this.temporadas = temporadas;
        this.episodios = episodios;
    }

    @Override
    public void reproducir() {
        System.out.println("Reproduciendo la serie: " + titulo + " (" + calidad + ")");
    }

    @Override
    public String obtenerDetalles() {
        return "Serie: " + titulo
                + " | Temporadas: " + temporadas
                + " | Episodios: " + episodios
                + " | Genero: " + genero
                + " | Duracion por episodio: " + duracionMin + " min"
                + " | Calidad: " + calidad
                + " | Costo: " + calcularCosto();
    }

    public int getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(int temporadas) {
        this.temporadas = temporadas;
    }

    public int getEpisodios() {
        return episodios;
    }

    public void setEpisodios(int episodios) {
        this.episodios = episodios;
    }
}
