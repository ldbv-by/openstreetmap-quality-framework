@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: changeset-model",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-dto",
                "quality_core :: changeset-util",
                "quality_services :: dto",
                "quality_services :: spi",
                "changeset_prepare :: spi"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub;