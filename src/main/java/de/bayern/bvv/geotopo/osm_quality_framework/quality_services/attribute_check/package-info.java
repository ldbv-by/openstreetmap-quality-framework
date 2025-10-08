@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: dto",
                "quality_services :: spi",
                "quality_services :: mapper",
                "quality_services :: model",
                "quality_core :: changeset-model",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-dto",
                "quality_core :: feature-dto",
                "quality_core :: feature-mapper",
                "quality_core :: feature-model",
                "openstreetmap_schema :: spi"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check;