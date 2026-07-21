package streamflow.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestiona la conexion unica a la base de datos SQLite y crea las tablas
 * necesarias la primera vez que se usa.
 */
public class ConexionBD {

    private static final String URL = "jdbc:sqlite:db/baseDeDatosStreamflow.db";

    private ConexionBD() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Abre una nueva conexion a la base de datos con las claves foraneas
     * activadas.
     *
     * @return conexion lista para usar
     * @throws SQLException si no se puede conectar
     */
    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);
        try (Statement st = conexion.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conexion;
    }

    /**
     * Crea las tablas 'contenido', 'usuario' y 'usuario_favorito' si aun no
     * existen.
     */
    public static void inicializar() {
        String sqlContenido = "CREATE TABLE IF NOT EXISTS contenido ("
                + "id TEXT PRIMARY KEY, "
                + "tipo TEXT NOT NULL, "
                + "titulo TEXT NOT NULL, "
                + "genero TEXT NOT NULL, "
                + "calidad TEXT NOT NULL, "
                + "duracion_min INTEGER NOT NULL, "
                + "costo_base REAL NOT NULL, "
                + "extra1 TEXT, "
                + "extra2 TEXT)";

        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuario ("
                + "id TEXT PRIMARY KEY, "
                + "nombre TEXT NOT NULL, "
                + "email TEXT NOT NULL, "
                + "suscripcion_id TEXT, "
                + "suscripcion_fecha TEXT, "
                + "suscripcion_costo REAL)";

        // Tabla puente: relaciona cada usuario con los contenidos que marco
        // como favoritos. La clave primaria compuesta evita duplicados.
        String sqlFavorito = "CREATE TABLE IF NOT EXISTS usuario_favorito ("
                + "usuario_id TEXT NOT NULL, "
                + "contenido_id TEXT NOT NULL, "
                + "PRIMARY KEY (usuario_id, contenido_id), "
                + "FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (contenido_id) REFERENCES contenido(id) ON DELETE CASCADE)";

        try (Connection conexion = obtenerConexion();
             Statement st = conexion.createStatement()) {
            st.execute(sqlContenido);
            st.execute(sqlUsuario);
            st.execute(sqlFavorito);
        } catch (SQLException e) {
            System.out.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}
