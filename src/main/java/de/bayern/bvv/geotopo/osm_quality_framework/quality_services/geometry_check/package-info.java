@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services::*",
                "quality_core::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.geometry_check;