package streamflow.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Documental;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Podcast;
import streamflow.modelo.Serie;

/**
 * Implementacion de {@link IContenidoDao} que persiste los contenidos en
 * la base de datos SQLite. El campo 'tipo' indica la subclase concreta y
 * 'extra1'/'extra2' guardan los atributos propios de cada tipo.
 */
public class ContenidoDaoSQLite implements IContenidoDao {

    public ContenidoDaoSQLite() {
        ConexionBD.inicializar();
    }

    @Override
    public boolean insertar(Contenido c) {
        String sql = "INSERT INTO contenido "
                + "(id, tipo, titulo, genero, calidad, duracion_min, costo_base, extra1, extra2) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, tipoDe(c));
            ps.setString(3, c.getTitulo());
            ps.setString(4, c.getGenero().name());
            ps.setString(5, c.getCalidad().name());
            ps.setInt(6, c.getDuracionMin());
            ps.setDouble(7, c.getCostoBase());
            ps.setString(8, extra1De(c));
            ps.setString(9, extra2De(c));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar contenido: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Contenido> listar() {
        List<Contenido> lista = new ArrayList<>();
        String sql = "SELECT * FROM contenido";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar contenidos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Contenido buscarPorId(String id) {
        String sql = "SELECT * FROM contenido WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar contenido: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean actualizar(Contenido c) {
        String sql = "UPDATE contenido SET "
                + "tipo = ?, titulo = ?, genero = ?, calidad = ?, "
                + "duracion_min = ?, costo_base = ?, extra1 = ?, extra2 = ? "
                + "WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipoDe(c));
            ps.setString(2, c.getTitulo());
            ps.setString(3, c.getGenero().name());
            ps.setString(4, c.getCalidad().name());
            ps.setInt(5, c.getDuracionMin());
            ps.setDouble(6, c.getCostoBase());
            ps.setString(7, extra1De(c));
            ps.setString(8, extra2De(c));
            ps.setString(9, c.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar contenido: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM contenido WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar contenido: " + e.getMessage());
            return false;
        }
    }

    // ---------- Helpers de mapeo objeto <-> fila ----------

    private Contenido mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String tipo = rs.getString("tipo");
        String titulo = rs.getString("titulo");
        Genero genero = Genero.valueOf(rs.getString("genero"));
        Calidad calidad = Calidad.valueOf(rs.getString("calidad"));
        int duracion = rs.getInt("duracion_min");
        double costoBase = rs.getDouble("costo_base");
        String extra1 = rs.getString("extra1");
        String extra2 = rs.getString("extra2");

        switch (tipo) {
            case "PELICULA":
                return new Pelicula(id, titulo, genero, calidad, duracion, costoBase, extra1);
            case "SERIE":
                int temporadas = extra1 != null ? Integer.parseInt(extra1) : 0;
                int episodios = extra2 != null ? Integer.parseInt(extra2) : 0;
                return new Serie(id, titulo, genero, calidad, duracion, costoBase, temporadas, episodios);
            case "DOCUMENTAL":
                return new Documental(id, titulo, genero, calidad, duracion, costoBase, extra1);
            case "PODCAST":
                return new Podcast(id, titulo, genero, calidad, duracion, costoBase, extra1);
            default:
                throw new SQLException("Tipo de contenido desconocido: " + tipo);
        }
    }

    private String tipoDe(Contenido c) {
        if (c instanceof Pelicula) {
            return "PELICULA";
        } else if (c instanceof Serie) {
            return "SERIE";
        } else if (c instanceof Documental) {
            return "DOCUMENTAL";
        } else if (c instanceof Podcast) {
            return "PODCAST";
        }
        return "DESCONOCIDO";
    }

    private String extra1De(Contenido c) {
        if (c instanceof Pelicula) {
            return ((Pelicula) c).getDirector();
        } else if (c instanceof Serie) {
            return String.valueOf(((Serie) c).getTemporadas());
        } else if (c instanceof Documental) {
            return ((Documental) c).getTema();
        } else if (c instanceof Podcast) {
            return ((Podcast) c).getPresentador();
        }
        return null;
    }

    private String extra2De(Contenido c) {
        if (c instanceof Serie) {
            return String.valueOf(((Serie) c).getEpisodios());
        }
        return null;
    }
}
