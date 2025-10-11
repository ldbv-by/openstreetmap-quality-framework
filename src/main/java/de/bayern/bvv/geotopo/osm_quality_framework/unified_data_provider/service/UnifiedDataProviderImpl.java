package de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.spi.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.spi.UnifiedDataProvider;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.spi.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation of {@link UnifiedDataProvider}.
 */
@Service
@RequiredArgsConstructor
public class UnifiedDataProviderImpl implements UnifiedDataProvider {

    private final OsmGeometriesService osmGeometriesService;
    private final ChangesetDataService changesetDataService;

    /**
     * Returns features from the data source that match the given filter.
     */
    @Override
    public DataSetDto getDataSet(DataSetFilter dataSetFilter) {

        // Load current OSM geometries from the database and map to the domain model.
        DataSet currentDataSet = Optional.ofNullable(this.osmGeometriesService.getDataSet(
                        dataSetFilter.featureFilter(),
                        dataSetFilter.coordinateReferenceSystem()))
                .map(DataSetMapper::toDomain)
                .orElse(null);

        // Optionally load features from specified changesets and merge them into the current dataset.
        /*
        if (dataSetFilter.includedChangesetIds() != null) {
            for (Long changesetId : dataSetFilter.includedChangesetIds()) {

                ChangesetDataSet changesetDataSet = Optional.ofNullable(this.changesetDataService.getDataSet(changesetId, dataSetFilter.featureFilter()))
                        .map(ChangesetDataSetMapper::toDomain).orElse(null);

                // ...

            }
        }*/

        return DataSetMapper.toDto(currentDataSet);
    }

    /**
     * Returns all features from the dataset that satisfy the given spatial relation
     * (e.g., contains, within, intersects) with the provided feature.
     */
    @Override
    public DataSetDto getDataSetBySpatialRelation(FeatureDto featureDto, Set<SpatialOperator> operators, DataSetFilter dataSetFilter) {
        DataSet resultDataSet = new DataSet();

        // Prepare the reference feature and its prepared geometry for fast spatial predicates.
        Feature referenceFeature = FeatureMapper.toDomain(featureDto);
        PreparedGeometry referenceGeometry = PreparedGeometryFactory.prepare(referenceFeature.getGeometry());

        // Prepare candidate features.
        // If no search bounding box is provided, derive it from the reference feature’s geometry.
        Envelope referenceEnvelope = featureDto.geometry().getEnvelopeInternal();

        if (referenceEnvelope != null) {
            BoundingBox bbox = new BoundingBox(
                    referenceEnvelope.getMinX(),
                    referenceEnvelope.getMinY(),
                    referenceEnvelope.getMaxX(),
                    referenceEnvelope.getMaxY()
            );

            FeatureFilter featureFilter = (dataSetFilter != null) ? dataSetFilter.featureFilter() : null;
            FeatureFilter modifiedFeatureFilter = (featureFilter == null || featureFilter.boundingBox() == null)
                    ? new FeatureFilter(
                    (featureFilter != null) ? featureFilter.osmIds() : null,
                    (featureFilter != null) ? featureFilter.tags() : null,
                    bbox
            ) : featureFilter;

            dataSetFilter = new DataSetFilter(
                    (dataSetFilter != null) ? dataSetFilter.includedChangesetIds() : null,
                    modifiedFeatureFilter,
                    (dataSetFilter != null) ? dataSetFilter.coordinateReferenceSystem() : null
            );
        }

        // Fetch the candidate features using the (possibly) augmented filter.
        DataSet candidateDataSet = Optional.ofNullable(this.getDataSet(dataSetFilter))
                .map(DataSetMapper::toDomain).orElse(null);

        // Evaluate spatial relations and collect matching candidates.
        if (candidateDataSet != null) {
            if (candidateDataSet.getNodes() != null) getSpatialCandidates(candidateDataSet.getNodes(), resultDataSet.getNodes(), referenceFeature, referenceGeometry, operators);
            if (candidateDataSet.getWays() != null)  getSpatialCandidates(candidateDataSet.getWays(), resultDataSet.getWays(), referenceFeature, referenceGeometry, operators);
            if (candidateDataSet.getAreas() != null) getSpatialCandidates(candidateDataSet.getAreas(), resultDataSet.getAreas(), referenceFeature, referenceGeometry, operators);
        }

        return DataSetMapper.toDto(resultDataSet);
    }

    /**
     * Populates {@code result} with features from {@code features} that match any of the
     * specified spatial operators relative to {@code referenceFeature}.
     */
    private void getSpatialCandidates(List<Feature> features, List<Feature> result, Feature referenceFeature, PreparedGeometry referenceGeometry, Set<SpatialOperator> operators) {
        STRtree candidateDataSetTree = new STRtree();

        for (Feature candidate : features) {
            // Skip the same object (same OSM id and type) as the reference feature.
            if (Objects.equals(candidate.getOsmId(), referenceFeature.getOsmId()) &&
                candidate.getObjectType().equals(referenceFeature.getObjectType())) continue;

            candidateDataSetTree.insert(candidate.getGeometry().getEnvelopeInternal(), candidate);
        }

        @SuppressWarnings("unchecked")
        List<Feature> candidates = candidateDataSetTree.query(referenceGeometry.getGeometry().getEnvelopeInternal());

        // Apply the requested spatial predicates to each candidate.
        for (Feature candidate : candidates) {

            for (SpatialOperator operator : operators) {
                boolean match = false;

                switch (operator) {
                    case CONTAINS -> match = referenceGeometry.contains(candidate.getGeometry());
                    case WITHIN -> match = referenceGeometry.within(candidate.getGeometry());
                }

                if (match) {
                    result.add(candidate);
                }
            }
        }
    }
}
