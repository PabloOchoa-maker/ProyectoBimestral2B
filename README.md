# StreamFlow

Sistema de gestión de una plataforma de contenidos por streaming. Administra un catálogo
(películas, series, documentales y podcasts), calcula el costo mensual de las suscripciones
según la calidad contratada y recomienda contenidos por género.

Proyecto del segundo bimestre — Programación Orientada a Objetos, UTPL.

## Stack

- **Java 25** (compilado con JDK 26)
- **Ant** — build del proyecto (proyecto *Java with Ant* de NetBeans)
- **SQLite** (driver JDBC) — persistencia
- **JUnit 5** — pruebas unitarias

Ant no resuelve dependencias por sí solo: los `.jar` viven a mano en `lib/` y se enlazan
desde `nbproject/project.properties`.

| Librería | Uso |
|---|---|
| `sqlite-jdbc-3.47.1.0.jar` | driver JDBC de SQLite |
| `slf4j-api-1.7.36.jar` | requerido por el driver de SQLite |
| `junit-platform-console-standalone-1.11.3.jar` | JUnit 5 (solo classpath de pruebas) |

## Arquitectura

Arquitectura por capas (MVC), con una regla estricta: **cada capa habla únicamente con la de abajo.**

```
Vista → Controlador → Servicio → DAO → Base de datos
```

```
src/streamflow/
├── modelo/           entidades del dominio (Contenido, Usuario, Suscripcion, enums)
│   └── contrato/     interfaces (Detallable, Reproducible)
├── persistencia/     patrón DAO: interfaces + implementaciones SQLite y en memoria
├── servicio/         reglas de negocio (costos, recomendaciones, validaciones)
├── controlador/      traduce entre la vista y el negocio
├── vista/            interacción por consola
└── Main.java         composición raíz: cablea DAO → servicio → controlador → vista
test/streamflow/      pruebas JUnit 5, espejando los paquetes de src/
```

Todas las capas dependen de **interfaces**, nunca de implementaciones concretas
(`IContenidoDao`, `IUsuarioService`, `IUsuarioControlador`, `IVista`). `Main` es la única
clase que conoce las clases concretas, así que sustituir SQLite por la implementación en
memoria es cambiar una sola línea.

La herencia de `Contenido` se mapea a una **tabla única** `contenido` con una columna `tipo`
(PELICULA/SERIE/DOCUMENTAL/PODCAST) más `extra1`/`extra2` para los atributos propios de cada
subclase. Los favoritos se guardan en la tabla puente `usuario_favorito`, con clave primaria
compuesta y `ON DELETE CASCADE`.

## Diagramas

Los diagramas están separados por herramienta dentro de `docs/uml/`:

- **`StratUML .mdj/`** — proyecto StarUML (fuente de la entrega):
  - `StartUMl.mdj` — diagrama de clases + diagrama de secuencia del flujo *facturar usuario*
  - `StartUmlFInal.png` — diagrama de clases renderizado
  - `Secuencia - Facturar usuario.png` / `.svg` — diagrama de secuencia renderizado
- **`PUML por conveniencia visual/`** — versión PlantUML del diagrama de clases (apoyo visual):
  - `streamflow_uml.puml` — fuente
  - `streamflow_uml.png` — renderizado

## Cómo ejecutarlo

> **Nota:** Ant no está en el PATH del sistema. Se usa el embebido de NetBeans.

```bash
# Compilar
"C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" compile

# Generar el jar
"C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" jar

# Ejecutar
java -jar dist/StrwamFlowProyectoBimestral2.jar
```

> La base de datos se abre por **ruta relativa** (`db/baseDeDatosStreamflow.db`): hay que
> ejecutar el programa desde la raíz del proyecto.

Desde NetBeans: abrir el proyecto y pulsar F6.

## Pruebas

```bash
"C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" test-junit5
```

La tarea `<junit>` que trae Ant es de **JUnit 4** y no sabe ejecutar pruebas de JUnit 5, por
eso `build.xml` define el target `test-junit5`, que lanza el *console launcher* de JUnit 5, y
sobrescribe `test` para que el botón de pruebas de NetBeans (Alt+F6) también lo use.

## Documentación

- [`docs/informe.md`](docs/informe.md) — informe del proyecto (análisis SOLID, pruebas)
- `docs/Informe_StreamFlow.docx` — informe completo para la entrega

## Autor

Pablo Ochoa y Pedro Gomez — UTPL
