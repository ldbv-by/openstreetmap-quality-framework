@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: dto",
                "quality_services :: spi",
                "quality_core :: changeset-model",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-dto"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check;