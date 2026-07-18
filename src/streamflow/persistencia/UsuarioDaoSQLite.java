package streamflow.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import streamflow.modelo.Suscripcion;
import streamflow.modelo.Usuario;

/**
 * Implementacion de {@link IUsuarioDao} sobre SQLite. La suscripcion se guarda
 * en columnas planas dentro de la misma fila del usuario. Los favoritos no se
 * persisten (se mantienen en memoria durante la ejecucion).
 */
public class UsuarioDaoSQLite implements IUsuarioDao {

    public UsuarioDaoSQLite() {
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
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
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
        return lista;
    }

    @Override
    public Usuario buscarPorId(String id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
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

    // ---------- Helpers ----------

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
