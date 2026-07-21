package streamflow.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Suscripcion;
import streamflow.modelo.Usuario;

/**
 * Implementacion de {@link IUsuarioDao} sobre SQLite. La suscripcion se guarda
 * en columnas planas dentro de la misma fila del usuario. Los favoritos viven en
 * la tabla puente 'usuario_favorito' y se reconstruyen al leer el usuario, para
 * lo cual se apoya en un {@link IContenidoDao} recibido por constructor.
 */
public class UsuarioDaoSQLite implements IUsuarioDao {

    private final IContenidoDao contenidoDao;

    public UsuarioDaoSQLite(IContenidoDao contenidoDao) {
        this.contenidoDao = contenidoDao;
        ConexionBD.inicializar();
    }

    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario "
                + "(id, nombre, email, suscripcion_id, suscripcion_fecha, suscripcion_costo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            enlazarUsuario(ps, u);
            if (ps.executeUpdate() != 1) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
        // El usuario puede llegar con favoritos ya marcados en memoria.
        for (Contenido favorito : u.getFavoritos()) {
            agregarFavorito(u.getId(), favorito);
        }
        return true;
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        // Los favoritos se cargan con el ResultSet ya cerrado, para no anidar
        // consultas sobre una lectura en curso.
        for (Usuario u : lista) {
            cargarFavoritos(u);
        }
        return lista;
    }

    @Override
    public Usuario buscarPorId(String id) {
        Usuario encontrado = null;
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    encontrado = mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        if (encontrado != null) {
            cargarFavoritos(encontrado);
        }
        return encontrado;
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET "
                + "nombre = ?, email = ?, suscripcion_id = ?, "
                + "suscripcion_fecha = ?, suscripcion_costo = ? "
                + "WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            Suscripcion s = u.getSuscripcion();
            ps.setString(3, s != null ? s.getId() : null);
            ps.setString(4, s != null ? s.getFechaInicio().toString() : null);
            if (s != null) {
                ps.setDouble(5, s.getCostoMensual());
            } else {
                ps.setNull(5, java.sql.Types.REAL);
            }
            ps.setString(6, u.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean agregarFavorito(String idUsuario, Contenido c) {
        if (idUsuario == null || c == null) {
            return false;
        }
        // INSERT OR IGNORE: si ya era favorito, no es un error, simplemente no
        // se duplica la fila.
        String sql = "INSERT OR IGNORE INTO usuario_favorito (usuario_id, contenido_id) "
                + "VALUES (?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idUsuario);
            ps.setString(2, c.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al agregar favorito: " + e.getMessage());
            return false;
        }
    }

    // ---------- Helpers ----------

    /**
     * Lee los favoritos del usuario en la tabla puente y los reconstruye como
     * objetos {@link Contenido} usando el DAO de contenidos.
     *
     * @param u usuario al que se le cargan los favoritos
     */
    private void cargarFavoritos(Usuario u) {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT contenido_id FROM usuario_favorito WHERE usuario_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("contenido_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar favoritos: " + e.getMessage());
            return;
        }
        for (String idContenido : ids) {
            Contenido c = contenidoDao.buscarPorId(idContenido);
            if (c != null) {
                u.agregarFavorito(c);
            }
        }
    }

    private void enlazarUsuario(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getId());
        ps.setString(2, u.getNombre());
        ps.setString(3, u.getEmail());
        Suscripcion s = u.getSuscripcion();
        ps.setString(4, s != null ? s.getId() : null);
        ps.setString(5, s != null ? s.getFechaInicio().toString() : null);
        if (s != null) {
            ps.setDouble(6, s.getCostoMensual());
        } else {
            ps.setNull(6, java.sql.Types.REAL);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String nombre = rs.getString("nombre");
        String email = rs.getString("email");

        Suscripcion suscripcion = null;
        String suscId = rs.getString("suscripcion_id");
        if (suscId != null) {
            String fechaTexto = rs.getString("suscripcion_fecha");
            LocalDate fecha = fechaTexto != null ? LocalDate.parse(fechaTexto) : LocalDate.now();
            double costo = rs.getDouble("suscripcion_costo");
            suscripcion = new Suscripcion(suscId, fecha, costo);
        }
        return new Usuario(id, nombre, email, suscripcion);
    }
}
