@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "changeset_data::*",
                "openstreetmap_geometries::*",
                "quality_core::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.merged_geodata_view;