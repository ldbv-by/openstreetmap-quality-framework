FROM postgis/postgis:17-3.5 AS pgtools

FROM azul/zulu-openjdk:24.0.2-24.32 AS openstreetmap-quality-framework

ARG OSM2PGSQL_REPO_URL
ARG OSM2PGSQL_REPO_BRANCH
ARG OSMOSIS_REPO_TAR

# Build dependencies
RUN apt-get update && apt-get install -y \
    make cmake g++ libboost-dev \
    libexpat1-dev zlib1g-dev libpotrace-dev \
    libopencv-dev libbz2-dev libpq-dev libproj-dev lua5.3 liblua5.3-dev \
    pandoc nlohmann-json3-dev pyosmium \
    libluajit-5.1-dev \
    git \
    maven \
    postgresql-client \
    curl ca-certificates tar gzip \
 && rm -rf /var/lib/apt/lists/*

# ---- Copy pgdump ----
COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_dump /usr/local/bin/pg_dump
COPY --from=pgtools /usr/lib/postgresql/17/bin/pg_restore /usr/local/bin/pg_restore

# ---- Build osm2pgsql ----
WORKDIR /src
RUN git clone --branch $OSM2PGSQL_REPO_BRANCH $OSM2PGSQL_REPO_URL

WORKDIR /src/osm2pgsql/build
RUN cmake -D WITH_LUAJIT=ON -D CMAKE_BUILD_TYPE=RelWithDebInfo ..
RUN make
RUN make install

ENV OSM2PGSQL_EXECUTABLE=/usr/local/bin/osm2pgsql

# ---- Add osmosis ----
RUN curl -OL $OSMOSIS_REPO_TAR \
 && tar -C /usr/local -xvf osmosis-0.49.2.tar

ENV OSMOSIS_EXECUTABLE=/usr/local/osmosis-0.49.2/bin/osmosis

# ---- Build openstreetmap-quality-framework ----
WORKDIR /src/openstreetmap-quality-framework
COPY pom.xml .
RUN mvn -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package && \
    install -d /app && \
    JAR="$(find target -maxdepth 1 -type f -name '*.jar' \
           ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)" && \
    cp "$JAR" /app/openstreetmap-quality-framework.jar

# ---- Clean ----
RUN rm -rf /src

# ---- Copy initial openstreetmap-geometries data ----
ENV OSM_LUA_FILE=file:/app/import/openstreetmap_geometries.lua
COPY src/main/resources/lua/openstreetmap_geometries.lua /app/import/openstreetmap_geometries.lua

# ---- Entrypoint Script ----
COPY docker/entrypoint/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["docker-entrypoint.sh"]
