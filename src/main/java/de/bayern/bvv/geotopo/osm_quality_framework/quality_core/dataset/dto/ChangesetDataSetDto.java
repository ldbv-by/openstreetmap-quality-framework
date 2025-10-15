package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto;

/**
 * Data transfer object with all tagged objects of a changeset.
 */
public record ChangesetDataSetDto(
        DataSetDto create,
        DataSetDto modify,
        DataSetDto delete
) {}
