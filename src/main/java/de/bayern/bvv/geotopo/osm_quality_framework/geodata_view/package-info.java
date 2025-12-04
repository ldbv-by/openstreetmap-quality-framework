@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "changeset_management::api",
                "openstreetmap_geometries::api",
                "quality_core::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.geodata_view;