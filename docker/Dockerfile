# ---- PostgreSQL Tool Stage ----
FROM postgis/postgis:17-3.5 AS pgtools


# ---- Build Stage ----
FROM maven:3.9.12-eclipse-temurin-25 AS build

WORKDIR /src
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package


# ---- Runtime Stage ----
FROM eclipse-temurin:25-jre

RUN apt-get update && apt-get install -y --no-install-recommends \
    postgresql-client osm2pgsql osmosis \
 && rm -rf /var/lib/apt/lists/*

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
WORKDIR /app

COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_dump /usr/local/bin/pg_dump
COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_restore /usr/local/bin/pg_restore
COPY src/main/resources/lua/openstreetmap_geometries.lua /app/import/openstreetmap_geometries.lua
COPY docker/entrypoint/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
COPY --from=build /src/target/*.jar ./openstreetmap-quality-framework.jar

ENV OSM2PGSQL_EXECUTABLE=/usr/bin/osm2pgsql
ENV OSMOSIS_EXECUTABLE=/usr/bin/osmosis
ENV OSM_LUA_FILE=file:/app/import/openstreetmap_geometries.lua

RUN chmod +x /usr/local/bin/docker-entrypoint.sh \
    && sed -i 's/\r$//' /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]