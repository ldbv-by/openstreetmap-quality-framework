@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "changeset_management :: *",
                "openstreetmap_geometries :: *",
                "quality_services :: *",
                "quality_core::*"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub;