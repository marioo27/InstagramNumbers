# Instagram Numbers

Aplicacion de consola en Java que analiza los ficheros JSON exportados de Instagram para mostrar seguidores mutuos, gente que te sigue y no sigues, y gente que sigues y no te sigue.

## Requisitos

- Java 17 o superior
- Maven

## Estructura de ficheros esperada

En la misma carpeta donde se encuentra el `pom.xml` deben existir estos ficheros:

- `following.json`: contiene el objeto `relationships_following` con los usuarios a los que sigues.
- `followers_1.json`: contiene un array con los usuarios que te siguen.

Estos ficheros se obtienen de la exportacion de datos de Instagram.

## Compilacion y ejecucion

```bash
mvn clean package
