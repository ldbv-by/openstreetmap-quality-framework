package de.bayern.bvv.geotopo.osm_quality_framework.test_core;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestDatabase {
    private static final DockerImageName POSTGIS_IMAGE =
            DockerImageName.parse("postgis/postgis:17-3.5")
                    .asCompatibleSubstituteFor("postgres");

    @SuppressWarnings("resource")
    public static final PostgreSQLContainer<?> TEST_DATABASE_CONTAINER = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("osm_quality_framework_tst")
            .withUsername("osm_quality_framework_user")
            .withPassword("osm_quality_framework_pass");


    static {
        TEST_DATABASE_CONTAINER.start();
    }

    private TestDatabase() {}
}
