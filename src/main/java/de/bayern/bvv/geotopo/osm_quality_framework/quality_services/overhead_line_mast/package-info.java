@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: *",
                "geodata_view :: *",
                "quality_core :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.overhead_line_mast;