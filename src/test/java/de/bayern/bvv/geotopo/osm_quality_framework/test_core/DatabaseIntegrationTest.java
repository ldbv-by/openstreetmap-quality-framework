package de.bayern.bvv.geotopo.osm_quality_framework.test_core;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto.CreateSchemaDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.api.Osm2PgSqlService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public abstract class DatabaseIntegrationTest {
    @Autowired
    protected Osm2PgSqlService osm2PgSqlService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected DataSource dataSource;

    private static final PostgreSQLContainer<?> testDatabaseContainer =
            TestDatabase.TEST_DATABASE_CONTAINER;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testDatabaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", testDatabaseContainer::getUsername);
        registry.add("spring.datasource.password", testDatabaseContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.datasource.hikari.connection-init-sql",
                () -> "CREATE SCHEMA IF NOT EXISTS openstreetmap_schema");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("OSM_QUALITY_FRAMEWORK_DATABASE", testDatabaseContainer::getDatabaseName);
        registry.add("OSM_QUALITY_FRAMEWORK_DATABASE_HOST", testDatabaseContainer::getHost);
        registry.add("OSM_QUALITY_FRAMEWORK_DATABASE_PORT", () -> String.valueOf(testDatabaseContainer.getMappedPort(5432)));
        registry.add("OSM_QUALITY_FRAMEWORK_DATABASE_USERNAME", testDatabaseContainer::getUsername);
        registry.add("OSM_QUALITY_FRAMEWORK_DATABASE_PASSWORD", testDatabaseContainer::getPassword);
    }

    @BeforeAll
    void init() {
        this.jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis;");
        this.createOpenStreetMapSchema();
    }

    @BeforeEach
    void resetSchema() throws Exception {
        this.createSchemaOpenStreetMapGeometries();
        this.createSchemaChangesetData();
    }

    private void createSchemaOpenStreetMapGeometries() throws Exception {
        this.jdbcTemplate.execute("DROP SCHEMA IF EXISTS openstreetmap_geometries CASCADE");
        this.jdbcTemplate.execute("CREATE SCHEMA openstreetmap_geometries");

        Path pbf = new ClassPathResource("pbf/test.osm.pbf").getFile().toPath();
        Path lua = new ClassPathResource("lua/openstreetmap_geometries.lua").getFile().toPath();

        CreateSchemaDto createSchemaDto = new CreateSchemaDto(
                pbf,
                lua,
                testDatabaseContainer.getDatabaseName(),
                "openstreetmap_geometries",
                testDatabaseContainer.getHost(),
                String.valueOf(testDatabaseContainer.getMappedPort(5432)),
                testDatabaseContainer.getUsername(),
                testDatabaseContainer.getPassword()
        );

        this.osm2PgSqlService.createSchema(createSchemaDto);

        String sql = """
                CREATE INDEX IF NOT EXISTS planet_osm_rels_rel_members_idx
                        ON openstreetmap_geometries.planet_osm_rels
                        USING gin (openstreetmap_geometries.planet_osm_member_ids(members, 'R'::character(1)))
                        WITH (fastupdate = off);
                """;
        this.jdbcTemplate.execute(sql);
    }

    private void createSchemaChangesetData() {
        String srcSchemaName = "openstreetmap_geometries";
        String dstSchemaName = "changeset_data";

        try {
            this.jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + dstSchemaName + " CASCADE");
            this.jdbcTemplate.execute("CREATE SCHEMA " + dstSchemaName);

            // copy openstreetmap_geometries tables
            List<String> tables = this.jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables " +
                            "WHERE table_schema = ? AND table_type = 'BASE TABLE'",
                    String.class, srcSchemaName);

            for (String table : tables) {
                String sql = "CREATE TABLE " + dstSchemaName + "." + table +
                        " (LIKE " + srcSchemaName + "." + table + " INCLUDING ALL INCLUDING INDEXES)";

                this.jdbcTemplate.execute(sql);
            }

            // copy openstreetmap_geometries functions
            List<String> functionDefs = this.jdbcTemplate.query(
                    "SELECT pg_get_functiondef(p.oid) AS def " +
                            "FROM pg_proc p " +
                            "JOIN pg_namespace n ON n.oid = p.pronamespace " +
                            "WHERE n.nspname = ?",
                    ps -> ps.setString(1, srcSchemaName),
                    (rs, i) -> rs.getString("def"));

            for (String def : functionDefs) {
                this.jdbcTemplate.execute(def.replace(srcSchemaName, dstSchemaName));
            }

            // create changesets and changeset_objects table and add changeset_id to object tables
            String ddl = """
            CREATE TABLE IF NOT EXISTS changeset_data.changesets (
              id                bigint       PRIMARY KEY,
              state             text         NOT NULL,
              created_at        timestamptz  NOT NULL,
              closed_at         timestamptz,
              CONSTRAINT chk_changeset_state
                CHECK (state IN ('OPEN','CHECKED','FINISHED', 'CANCELLED'))
            );

            CREATE TABLE IF NOT EXISTS changeset_data.changeset_objects (
              id                bigserial    PRIMARY KEY,
              osm_id            bigint       NOT NULL,
              geometry_type     text         NOT NULL,
              changeset_id      bigint       NOT NULL,
              operation_type    text         NOT NULL,
              CONSTRAINT chk_changeset_objects_geom_type
                CHECK (geometry_type IN ('NODE','WAY','AREA', 'MULTIPOLYGON','RELATION')),
              CONSTRAINT chk_changeset_objects_op_type
                CHECK (operation_type IN ('CREATE','MODIFY','DELETE'))
            );

            CREATE INDEX IF NOT EXISTS idx_changeset_objects_changeset_id
              ON changeset_data.changeset_objects (changeset_id);

            CREATE INDEX IF NOT EXISTS idx_changeset_objects_osm
              ON changeset_data.changeset_objects (geometry_type, osm_id);

            CREATE UNIQUE INDEX IF NOT EXISTS uq_changeset_objects
              ON changeset_data.changeset_objects (changeset_id, geometry_type, osm_id, operation_type);

            ALTER TABLE IF EXISTS changeset_data.nodes             ADD COLUMN IF NOT EXISTS changeset_id bigint;
            ALTER TABLE IF EXISTS changeset_data.ways              ADD COLUMN IF NOT EXISTS changeset_id bigint;
            ALTER TABLE IF EXISTS changeset_data.areas             ADD COLUMN IF NOT EXISTS changeset_id bigint;
            ALTER TABLE IF EXISTS changeset_data.relations         ADD COLUMN IF NOT EXISTS changeset_id bigint;
            ALTER TABLE IF EXISTS changeset_data.relation_members  ADD COLUMN IF NOT EXISTS changeset_id bigint;
            
            CREATE OR REPLACE VIEW openstreetmap_geometries.way_nodes AS
            SELECT way.osm_id AS way_osm_id,
                   node.id AS node_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM openstreetmap_geometries.ways way,
                   openstreetmap_geometries.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             openstreetmap_geometries.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE planet_way.id = way.osm_id;
            
            CREATE OR REPLACE VIEW changeset_data.way_nodes AS
            SELECT way.osm_id AS way_osm_id,
                   node.id AS node_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM changeset_data.ways way,
                   changeset_data.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             changeset_data.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE planet_way.id = way.osm_id;
            
            CREATE OR REPLACE VIEW openstreetmap_geometries.area_nodes AS
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   NULL::bigint AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM openstreetmap_geometries.areas area,
                   openstreetmap_geometries.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             openstreetmap_geometries.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'W'::bpchar AND planet_way.id = area.osm_id
            UNION ALL
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   planet_way.id AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM openstreetmap_geometries.areas area,
                   openstreetmap_geometries.planet_osm_rels planet_rel,
                   openstreetmap_geometries.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             openstreetmap_geometries.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'R'::bpchar AND planet_rel.id = area.osm_id
              AND (planet_way.id = ANY (openstreetmap_geometries.planet_osm_member_ids(planet_rel.members, 'W'::bpchar)));
            
            
            CREATE OR REPLACE VIEW changeset_data.area_nodes AS
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   NULL::bigint AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM changeset_data.areas area,
                   changeset_data.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             changeset_data.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'W'::bpchar AND planet_way.id = area.osm_id
            UNION ALL
            SELECT area.osm_id AS area_osm_id,
                   node.id AS node_osm_id,
                   planet_way.id AS member_osm_id,
                   node.lat::double precision / '10000000'::bigint::double precision AS lat,
                   node.lon::double precision / '10000000'::bigint::double precision AS lon,
                   node.seq
              FROM changeset_data.areas area,
                   changeset_data.planet_osm_rels planet_rel,
                   changeset_data.planet_osm_ways planet_way,
            LATERAL ( SELECT nd.node_id,
                             nd.seq,
                             planet_node.id,
                             planet_node.lat,
                             planet_node.lon,
                             planet_node.tags
                        FROM unnest(planet_way.nodes) WITH ORDINALITY nd(node_id, seq),
                             changeset_data.planet_osm_nodes planet_node
                       WHERE planet_node.id = nd.node_id) node
            WHERE area.osm_geometry_type = 'R'::bpchar AND planet_rel.id = area.osm_id
              AND (planet_way.id = ANY (changeset_data.planet_osm_member_ids(planet_rel.members, 'W'::bpchar)));
            """;

            for (String stmt : ddl.split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) {
                    this.jdbcTemplate.execute(s);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("Schema " + dstSchemaName + " creation failed:\n" + e.getMessage());
        }
    }

    private void createOpenStreetMapSchema() {
        this.jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS openstreetmap_schema");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSeparator(";");
        populator.setContinueOnError(false);
        populator.addScripts(
                new ClassPathResource("sql/openstreetmap_schema/aaa-basis-schema.sql"),
                new ClassPathResource("sql/openstreetmap_schema/atkis-basis-dlm-schema.sql"),
                new ClassPathResource("sql/openstreetmap_schema/openstreetmap-quality-framework-schema.sql")
        );

        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] ruleScripts = resolver.getResources(
                    "classpath*:sql/openstreetmap_schema/rules/**/*.sql" // oder **/*.sql
            );

            Arrays.sort(ruleScripts, Comparator.comparing(Resource::getFilename));
            populator.addScripts(ruleScripts);
        } catch (Exception e) {
            throw new IllegalStateException("No rules found:\n" + e.getMessage());
        }

        populator.execute(this.dataSource);
    }
}
