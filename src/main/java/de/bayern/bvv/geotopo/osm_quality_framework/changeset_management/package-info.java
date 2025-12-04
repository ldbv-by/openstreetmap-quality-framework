@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core::*",
                "openstreetmap_geometries::api"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management;