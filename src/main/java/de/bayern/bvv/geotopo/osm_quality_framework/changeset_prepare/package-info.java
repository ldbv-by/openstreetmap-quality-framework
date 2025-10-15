@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core::*",
                "openstreetmap_tools::*",
                "changeset_data::*",
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare;