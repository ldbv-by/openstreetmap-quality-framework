@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_domain :: changeset-model",
                "quality_domain :: changeset-mapper",
                "quality_contract :: dto",
                "quality_contract :: spi"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub;