@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: *",
                "openstreetmap_geometries :: *",
                "openstreetmap_schema :: *",
                "quality_core :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.object_number_assignment;