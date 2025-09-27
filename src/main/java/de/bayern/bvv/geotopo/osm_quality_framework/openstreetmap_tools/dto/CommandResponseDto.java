package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_tools.dto;

import org.springframework.core.io.Resource;

import java.nio.file.Path;

/**
 * Data transfer object for a command.
 */
public record CommandResponseDto(
        String commandOutput,
        String downloadFileName,
        Resource downloadFileResource,
        Path downloadDirectory
) {}
