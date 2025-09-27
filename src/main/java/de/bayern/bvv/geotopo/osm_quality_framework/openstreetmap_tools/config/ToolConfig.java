package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Settings for external OSM utilities.
 */
@ConfigurationProperties(prefix = "openstreetmap-tools")
@Data
@Component
public class ToolConfig {
    private String osm2PgSql;
    private String osmosis;
}
