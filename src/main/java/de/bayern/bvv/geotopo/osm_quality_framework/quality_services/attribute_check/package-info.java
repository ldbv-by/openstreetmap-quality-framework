@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_contract :: dto",
                "quality_contract :: spi"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check;