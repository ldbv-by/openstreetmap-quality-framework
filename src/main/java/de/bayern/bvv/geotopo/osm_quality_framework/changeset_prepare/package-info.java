@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: changeset-dto",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-model",
                "quality_core :: changeset-util",
                "openstreetmap_tools :: spi",
                "openstreetmap_tools :: dto",
                "changeset_data :: spi",
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare;