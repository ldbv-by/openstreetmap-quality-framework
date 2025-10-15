@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services::*",
                "quality_core::*",
                "rule_engine::*",
                "openstreetmap_schema::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check;