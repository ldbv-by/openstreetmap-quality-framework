package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.model;

import lombok.Data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A specific command request.
 */
@Data
public class CommandRequest {
    private List<String> args;
    private List<Path> uploadPaths = new ArrayList<>();
    private Map<String, String> environments = new HashMap<>();
}
