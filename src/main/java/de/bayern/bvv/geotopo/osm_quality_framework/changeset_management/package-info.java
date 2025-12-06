@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "openstreetmap_tools :: *",
                "openstreetmap_geometries :: *",
                "quality_core :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management;