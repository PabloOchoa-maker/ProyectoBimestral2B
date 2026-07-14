# StreamFlow

Sistema de gestión de una plataforma de contenidos por streaming. Administra un catálogo
(películas, series, documentales y podcasts), calcula el costo mensual de las suscripciones
según la calidad contratada y recomienda contenidos por género.

Proyecto del segundo bimestre — Programación Orientada a Objetos, UTPL.

## Stack

- **Java 21** (compilado con JDK 26)
- **Maven** — gestión de dependencias y build
- **SQLite** (driver JDBC) — persistencia
- **JUnit 5** — pruebas unitarias

## Arquitectura

Arquitectura por capas (MVC), con una regla estricta: **cada capa habla únicamente con la de abajo.**

```
Vista → Controlador → Servicio → DAO → Base de datos
```

```
streamflow/
├── modelo/           entidades del dominio (Contenido, Usuario, Suscripcion, enums)
│   └── contrato/     interfaces (Detallable, Reproducible)
├── persistencia/     patrón DAO: interfaces + implementaciones SQLite
├── servicio/         reglas de negocio (costos, recomendaciones, validaciones)
├── controlador/      traduce entre la vista y el negocio
└── vista/            interacción por consola
```

El acceso a datos usa el **patrón DAO**: los servicios dependen de las interfaces
(`IContenidoDao`, `IUsuarioDao`) y nunca de las implementaciones concretas. Esto permite
sustituir SQLite por una implementación en memoria durante las pruebas.

## Diagrama de clases

.pendiente

## Cómo ejecutarlo

> **Nota:** Maven no está en el PATH del sistema. Se usa el embebido de NetBeans.

```bash
# Compilar
"C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" clean package

# Ejecutar
java -jar target/streamflow-1.0-SNAPSHOT.jar
```

## Pruebas

```bash
"C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" test
```

## Documentación

- [`docs/informe.md`](docs/informe.md) — informe del proyecto (análisis SOLID, pruebas)


## Autor

Pablo Ochoa y Pedro Gomez — UTPL