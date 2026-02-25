# ---- PostgreSQL Tool Stage ----
FROM postgis/postgis:17-3.5 AS pgtools


# ---- OSM Tools Stage (Build osm2pgsql + osmosis) ----
FROM eclipse-temurin:25-jdk AS osmtools

ARG OSM2PGSQL_REPO_URL
ARG OSM2PGSQL_REPO_BRANCH
ARG OSMOSIS_REPO_TAR

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

RUN apt-get update && apt-get install -y --no-install-recommends \
    make cmake g++ libboost-dev \
    libexpat1-dev zlib1g-dev libpotrace-dev \
    libopencv-dev libbz2-dev libpq-dev libproj-dev \
    lua5.4 liblua5.4-0 liblua5.4-dev \
    pandoc nlohmann-json3-dev pyosmium \
    git \
    curl ca-certificates tar gzip \
 && rm -rf /var/lib/apt/lists/*

# ---- Build osm2pgsql (Lua 5.4, no LuaJIT) ----
WORKDIR /src
RUN git clone --branch "$OSM2PGSQL_REPO_BRANCH" "$OSM2PGSQL_REPO_URL"

WORKDIR /src/osm2pgsql/build
RUN cmake -DWITH_LUA=lua5.4 -DCMAKE_BUILD_TYPE=RelWithDebInfo ..
RUN make -j"$(nproc)" && make install

# Sanity Checks
RUN osm2pgsql --version && \
    lua5.4 -e "assert(math.type(100000000000000000)=='integer')" && \
    lua5.4 -e "local a=(1<<53); assert(a+1~=a, 'Lua numbers not precise')" || true

ENV OSM2PGSQL_EXECUTABLE=/usr/local/bin/osm2pgsql

# ---- Add osmosis ----
RUN curl -OL "$OSMOSIS_REPO_TAR" \
 && tar -C /usr/local -xvf osmosis-0.49.2.tar

ENV OSMOSIS_EXECUTABLE=/usr/local/osmosis-0.49.2/bin/osmosis


# ---- Build Stage ----
FROM maven:3.9.12-eclipse-temurin-25 AS build

WORKDIR /src
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package


# ---- Runtime Stage ----
FROM eclipse-temurin:25-jre

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
WORKDIR /app

COPY --from=osmtools /usr/local/bin/osm2pgsql /usr/local/bin/osm2pgsql
COPY --from=osmtools /usr/local/osmosis-0.49.2 /usr/local/osmosis-0.49.2
COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_dump /usr/local/bin/pg_dump
COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_restore /usr/local/bin/pg_restore

ENV OSM2PGSQL_EXECUTABLE=/usr/local/bin/osm2pgsql
ENV OSMOSIS_EXECUTABLE=/usr/local/osmosis-0.49.2/bin/osmosis

COPY --from=build /src/target/*.jar ./openstreetmap-quality-framework.jar

ENV OSM_LUA_FILE=file:/app/import/openstreetmap_geometries.lua
COPY src/main/resources/lua/openstreetmap_geometries.lua /app/import/openstreetmap_geometries.lua

COPY docker/entrypoint/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh \
    && sed -i 's/\r$//' /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]