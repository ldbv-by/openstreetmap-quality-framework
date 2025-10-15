@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core::*",
                "quality_services::*",
                "changeset_prepare::*",
                "changeset_data::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub;