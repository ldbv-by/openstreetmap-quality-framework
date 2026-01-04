@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: *",
                "openstreetmap_schema :: *",
                "rule_engine :: *",
                "quality_core :: *",
                "geodata_view :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.geometry_check;