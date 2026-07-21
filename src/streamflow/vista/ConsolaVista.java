package streamflow.vista;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import streamflow.controlador.IContenidoControlador;
import streamflow.controlador.IUsuarioControlador;
import streamflow.modelo.Calidad;
import streamflow.modelo.Contenido;
import streamflow.modelo.Documental;
import streamflow.modelo.Genero;
import streamflow.modelo.Pelicula;
import streamflow.modelo.Podcast;
import streamflow.modelo.Serie;
import streamflow.modelo.Suscripcion;
import streamflow.modelo.Usuario;

/**
 * Implementacion de {@link IVista} basada en la consola de texto.
 */
public class ConsolaVista implements IVista {

    private final IContenidoControlador contenidoControlador;
    private final IUsuarioControlador usuarioControlador;

    /**
     * Cablea esta vista concreta con sus colaboradores (los controladores).
     * La dependencia hacia las interfaces de controlador vive aqui, en la
     * implementacion, no en el contrato {@link IVista}.
     *
     * @param contenidoControlador controlador para operaciones de contenido
     * @param usuarioControlador    controlador para operaciones de usuario
     */
    public ConsolaVista(IContenidoControlador contenidoControlador,
                        IUsuarioControlador usuarioControlador) {
        this.contenidoControlador = contenidoControlador;
        this.usuarioControlador = usuarioControlador;
    }

    @Override
    public void mostrarMenu() {
        System.out.println();
        System.out.println("========= STREAMFLOW =========");
        System.out.println("1. Listar contenidos");
        System.out.println("2. Facturar usuario");
        System.out.println("3. Recomendar por genero");
        System.out.println("4. Registrar contenido");
        System.out.println("5. Registrar usuario");
        System.out.println("6. Agregar favorito a un usuario");
        System.out.println("0. Salir");
        System.out.println("==============================");
        System.out.print("Seleccione una opcion: ");
    }

    @Override
    public void mostrarContenidos(List<Contenido> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay contenidos para mostrar.");
            return;
        }
        System.out.println("--- Contenidos disponibles (" + lista.size() + ") ---");
        for (Contenido c : lista) {
            System.out.println(" - " + c.obtenerDetalles());
        }
    }

    @Override
    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        String opcion;
        do {
            mostrarMenu();
            opcion = sc.nextLine().trim();
            switch (opcion) {
                case "1":
                    mostrarContenidos(contenidoControlador.listar());
                    break;
                case "2":
                    System.out.print("ID del usuario a facturar: ");
                    String idFactura = sc.nextLine().trim();
                    double total = usuarioControlador.facturar(idFactura);
                    mostrarMensaje("Costo mensual total: $" + total);
                    break;
                case "3":
                    System.out.print("ID del usuario para recomendar: ");
                    String idRec = sc.nextLine().trim();
                    List<Contenido> recomendados = usuarioControlador.recomendar(idRec);
                    mostrarContenidos(recomendados);
                    break;
                case "4":
                    registrarContenido(sc);
                    break;
                case "5":
                    registrarUsuario(sc);
                    break;
                case "6":
                    agregarFavorito(sc);
                    break;
                case "0":
                    mostrarMensaje("Hasta pronto.");
                    break;
                default:
                    mostrarMensaje("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
        sc.close();
    }

    /**
     * Pide por consola los datos de un contenido nuevo, construye el objeto
     * concreto segun el tipo elegido y lo registra a traves del controlador.
     *
     * @param sc scanner del bucle de interaccion
     */
    private void registrarContenido(Scanner sc) {
        System.out.println("--- Registrar contenido ---");
        System.out.println("Tipo: 1=Pelicula, 2=Serie, 3=Documental, 4=Podcast");
        String tipo = leerLinea(sc, "Seleccione el tipo: ");

        String id = leerLinea(sc, "ID: ");
        String titulo = leerLinea(sc, "Titulo: ");
        Genero genero = leerEnum(sc, "Genero", Genero.class);
        Calidad calidad = leerEnum(sc, "Calidad", Calidad.class);
        int duracion = leerEntero(sc, "Duracion (min): ");
        double costoBase = leerDecimal(sc, "Costo base: ");

        Contenido contenido;
        switch (tipo) {
            case "1":
                String director = leerLinea(sc, "Director: ");
                contenido = new Pelicula(id, titulo, genero, calidad, duracion, costoBase, director);
                break;
            case "2":
                int temporadas = leerEntero(sc, "Temporadas: ");
                int episodios = leerEntero(sc, "Episodios: ");
                contenido = new Serie(id, titulo, genero, calidad, duracion, costoBase, temporadas, episodios);
                break;
            case "3":
                String tema = leerLinea(sc, "Tema: ");
                contenido = new Documental(id, titulo, genero, calidad, duracion, costoBase, tema);
                break;
            case "4":
                String presentador = leerLinea(sc, "Presentador: ");
                contenido = new Podcast(id, titulo, genero, calidad, duracion, costoBase, presentador);
                break;
            default:
                mostrarMensaje("Tipo no valido. Se cancela el registro.");
                return;
        }

        boolean ok = contenidoControlador.registrar(contenido);
        mostrarMensaje(ok
                ? "Contenido registrado correctamente."
                : "No se pudo registrar (ID duplicado o datos invalidos).");
    }

    /**
     * Pide por consola los datos de un usuario nuevo (con su suscripcion) y lo
     * registra a traves del controlador.
     *
     * @param sc scanner del bucle de interaccion
     */
    private void registrarUsuario(Scanner sc) {
        System.out.println("--- Registrar usuario ---");
        String id = leerLinea(sc, "ID: ");
        String nombre = leerLinea(sc, "Nombre: ");
        String email = leerLinea(sc, "Email: ");

        System.out.println("- Datos de la suscripcion -");
        String idSus = leerLinea(sc, "ID de suscripcion: ");
        LocalDate fechaInicio = leerFecha(sc, "Fecha de inicio (AAAA-MM-DD): ");
        double costoMensual = leerDecimal(sc, "Costo mensual: ");
        Suscripcion suscripcion = new Suscripcion(idSus, fechaInicio, costoMensual);

        Usuario usuario = new Usuario(id, nombre, email, suscripcion);
        boolean ok = usuarioControlador.registrar(usuario);
        mostrarMensaje(ok
                ? "Usuario registrado correctamente."
                : "No se pudo registrar (ID duplicado o datos invalidos).");
    }

    /**
     * Pide el usuario y le marca como favorito un contenido del catalogo,
     * elegido por su numero en la lista.
     *
     * @param sc scanner del bucle de interaccion
     */
    private void agregarFavorito(Scanner sc) {
        System.out.println("--- Agregar favorito ---");
        String idUsuario = leerLinea(sc, "ID del usuario: ");

        List<Contenido> catalogo = contenidoControlador.listar();
        if (catalogo.isEmpty()) {
            mostrarMensaje("No hay contenidos registrados todavia.");
            return;
        }
        System.out.println("Contenidos disponibles:");
        for (int i = 0; i < catalogo.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + catalogo.get(i).obtenerDetalles());
        }

        int numero = leerEntero(sc, "Numero del contenido: ");
        if (numero < 1 || numero > catalogo.size()) {
            mostrarMensaje("Numero fuera de rango.");
            return;
        }

        boolean ok = usuarioControlador.agregarFavorito(idUsuario, catalogo.get(numero - 1));
        mostrarMensaje(ok
                ? "Favorito agregado correctamente."
                : "No se pudo agregar (el usuario no existe).");
    }

    /**
     * Muestra un prompt y devuelve la linea leida ya recortada.
     */
    private String leerLinea(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Lee un entero, repitiendo la pregunta hasta que la entrada sea valida.
     */
    private int leerEntero(Scanner sc, String prompt) {
        while (true) {
            try {
                return Integer.parseInt(leerLinea(sc, prompt));
            } catch (NumberFormatException e) {
                mostrarMensaje("Numero entero no valido, intente de nuevo.");
            }
        }
    }

    /**
     * Lee un decimal, repitiendo la pregunta hasta que la entrada sea valida.
     */
    private double leerDecimal(Scanner sc, String prompt) {
        while (true) {
            try {
                return Double.parseDouble(leerLinea(sc, prompt));
            } catch (NumberFormatException e) {
                mostrarMensaje("Numero no valido, intente de nuevo.");
            }
        }
    }

    /**
     * Lee una constante de un enum mostrando las opciones disponibles; repite
     * hasta que el texto coincida con una constante valida.
     */
    private <T extends Enum<T>> T leerEnum(Scanner sc, String etiqueta, Class<T> tipo) {
        StringBuilder opciones = new StringBuilder();
        for (T constante : tipo.getEnumConstants()) {
            if (opciones.length() > 0) {
                opciones.append(", ");
            }
            opciones.append(constante.name());
        }
        while (true) {
            String valor = leerLinea(sc, etiqueta + " (" + opciones + "): ").toUpperCase();
            try {
                return Enum.valueOf(tipo, valor);
            } catch (IllegalArgumentException e) {
                mostrarMensaje("Valor no valido, intente de nuevo.");
            }
        }
    }

    /**
     * Lee una fecha en formato ISO (AAAA-MM-DD), repitiendo hasta que sea valida.
     */
    private LocalDate leerFecha(Scanner sc, String prompt) {
        while (true) {
            try {
                return LocalDate.parse(leerLinea(sc, prompt));
            } catch (DateTimeParseException e) {
                mostrarMensaje("Fecha no valida (use AAAA-MM-DD), intente de nuevo.");
            }
        }
    }

    /**
     * Muestra un mensaje generico en la consola.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
