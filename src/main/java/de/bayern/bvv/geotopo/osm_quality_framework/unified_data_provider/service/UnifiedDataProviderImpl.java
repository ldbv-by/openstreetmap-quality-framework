package de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.api.ChangesetDataService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api.UnifiedDataProvider;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.springframework.stereotype.Service;

import java.util.*;

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
                .orElse(new DataSet());

        // Optionally load features from specified changesets and merge them into the current dataset.
        if (dataSetFilter.ignoreChangesetData() == null || !dataSetFilter.ignoreChangesetData()) {
            for (Long changesetId : this.changesetDataService.getChangesetIds(Set.of(ChangesetState.OPEN, ChangesetState.CHECKED))) {

                ChangesetDataSet cs = Optional.ofNullable(this.changesetDataService.getDataSet(
                                changesetId,
                                dataSetFilter.featureFilter(),
                                dataSetFilter.coordinateReferenceSystem()))
                        .map(ChangesetDataSetMapper::toDomain)
                        .orElse(null);

                if (cs == null) continue;

                // --- CREATE: add ---
                addAll(currentDataSet.getNodes(), cs.getCreate() != null ? cs.getCreate().getNodes() : null);
                addAll(currentDataSet.getWays(),  cs.getCreate() != null ? cs.getCreate().getWays()  : null);
                addAll(currentDataSet.getAreas(), cs.getCreate() != null ? cs.getCreate().getAreas() : null);
                addAll(currentDataSet.getRelations(), cs.getCreate() != null ? cs.getCreate().getRelations() : null);

                // --- MODIFY: upsert (replace if exists, else add) ---
                upsertAll(currentDataSet.getNodes(), cs.getModify() != null ? cs.getModify().getNodes() : null);
                upsertAll(currentDataSet.getWays(),  cs.getModify() != null ? cs.getModify().getWays()  : null);
                upsertAll(currentDataSet.getAreas(), cs.getModify() != null ? cs.getModify().getAreas() : null);
                upsertAll(currentDataSet.getRelations(), cs.getModify() != null ? cs.getModify().getRelations() : null);

                // --- DELETE: remove by osmId ---
                removeAll(currentDataSet.getNodes(), idsOf(cs.getDelete() != null ? cs.getDelete().getNodes() : null));
                removeAll(currentDataSet.getWays(),  idsOf(cs.getDelete() != null ? cs.getDelete().getWays()  : null));
                removeAll(currentDataSet.getAreas(), idsOf(cs.getDelete() != null ? cs.getDelete().getAreas() : null));
                removeAll(currentDataSet.getRelations(), idsOf(cs.getDelete() != null ? cs.getDelete().getRelations() : null));
            }
        }

        return DataSetMapper.toDto(currentDataSet);
    }

    /**
     * Returns all features from the dataset that satisfy the given spatial relation
     * (e.g., contains, within, intersects) with the provided feature.
     */
    @Override
    public DataSetDto getDataSetBySpatialRelation(FeatureDto referenceFeatureDto,
                                                  Set<SpatialOperator> operators,
                                                  DataSetFilter dataSetFilter,
                                                  boolean selfCheck) {
        DataSet resultDataSet = new DataSet();

        // Check inputs
        if (referenceFeatureDto == null) throw new IllegalArgumentException("referenceFeatureDto cannot be null");
        if (operators == null) throw new IllegalArgumentException("operators cannot be null");

        // Prepare the reference feature and its prepared geometry for fast spatial predicates.
        Feature referenceFeature = FeatureMapper.toDomain(referenceFeatureDto);
        PreparedGeometry referenceGeometry = PreparedGeometryFactory.prepare(referenceFeature.getGeometry());

        Envelope referenceEnvelope = referenceFeature.getGeometry().getEnvelopeInternal();
        referenceEnvelope.expandBy(1e-5);

        // Prepare candidate features.
        // If no search bounding box is provided, derive it from the reference feature’s geometry.
        DataSetFilter preparedDataSetFilter = this.prepareDataSetFilter(dataSetFilter, referenceEnvelope);

        // Fetch the candidate features using the (possibly) augmented filter.
        DataSet candidateDataSet = Optional.ofNullable(this.getDataSet(preparedDataSetFilter))
                .map(DataSetMapper::toDomain).orElse(null);

        // Evaluate spatial relations and collect matching candidates.
        if (candidateDataSet != null) {
            List<Feature> candidateFeatures = new ArrayList<>();
            if (candidateDataSet.getNodes() != null && !candidateDataSet.getNodes().isEmpty()) candidateFeatures.addAll(candidateDataSet.getNodes());
            if (candidateDataSet.getWays() != null && !candidateDataSet.getWays().isEmpty()) candidateFeatures.addAll(candidateDataSet.getWays());
            if (candidateDataSet.getAreas() != null && !candidateDataSet.getAreas().isEmpty()) candidateFeatures.addAll(candidateDataSet.getAreas());

            if (candidateDataSet.getRelations() != null && !candidateDataSet.getRelations().isEmpty()) {
                List<Feature> candidateRelations = new ArrayList<>();

                for (Relation relation : candidateDataSet.getRelations()) {
                    String candidateRole = (dataSetFilter.featureFilter() == null) ? null : dataSetFilter.featureFilter().role();
                    DataSet candidateMembers = Optional.ofNullable(this.getRelationMembers(relation.getOsmId(), candidateRole))
                            .map(DataSetMapper::toDomain).orElse(null);

                    if  (candidateMembers != null) {
                        List<Feature> candidateMemberFeatures = new ArrayList<>();
                        for (TaggedObject candidateMember : candidateMembers.getAll()) {
                            if (candidateMember instanceof Feature candidateMemberFeature) {
                                candidateMemberFeatures.add(candidateMemberFeature);
                            }
                        }

                            if (!candidateMemberFeatures.isEmpty()) {
                            Feature relationMemberFeature = this.aggregateFeatures(candidateMemberFeatures, SpatialAggregator.UNION);
                            if (relationMemberFeature == null) continue;
                            relationMemberFeature.setOsmId(relation.getOsmId());
                            relationMemberFeature.setObjectType(relation.getObjectType());

                            candidateRelations.add(relationMemberFeature);
                        }
                    }
                }

                if (!candidateRelations.isEmpty()) candidateFeatures.addAll(candidateRelations);
            }

            if (!candidateFeatures.isEmpty()) {
                List<Feature> resultFeatures = new ArrayList<>();
                this.getSpatialCandidates(candidateFeatures, resultFeatures, referenceFeature,
                        referenceGeometry, referenceEnvelope, operators, dataSetFilter, selfCheck);

                for (Feature resultFeature : resultFeatures) {
                    switch (resultFeature.getGeometry()) {
                        case Point _ -> resultDataSet.getNodes().add(resultFeature);
                        case LineString _ -> resultDataSet.getWays().add(resultFeature);
                        case Polygon _ -> resultDataSet.getAreas().add(resultFeature);
                        case null, default -> candidateDataSet.getRelations().stream()
                                .filter(cr -> cr.getOsmId().equals(resultFeature.getOsmId()))
                                .findFirst()
                                .ifPresent(cr -> resultDataSet.getRelations().add(cr));
                    }
                }
            }
        }

        return DataSetMapper.toDto(resultDataSet);
    }

    /**
     * Populates {@code result} with features from {@code features} that match any of the
     * specified spatial operators relative to {@code referenceFeature}.
     */
    private void getSpatialCandidates(List<Feature> features, List<Feature> result, Feature referenceFeature,
                                      PreparedGeometry referenceGeometry, Envelope referenceEnvelope,
                                      Set<SpatialOperator> operators, DataSetFilter dataSetFilter, boolean selfCheck) {

        STRtree candidateDataSetTree = new STRtree();

        for (Feature candidate : features) {
            // Skip the same object (same OSM id and type) as the reference feature.
            if (Objects.equals(candidate.getOsmId(), referenceFeature.getOsmId()) &&
                candidate.getObjectType().equals(referenceFeature.getObjectType()) &&
                !selfCheck) continue;

            candidateDataSetTree.insert(candidate.getGeometry().getEnvelopeInternal(), candidate);
        }

        @SuppressWarnings("unchecked")
        List<Feature> candidates = candidateDataSetTree.query(referenceEnvelope);
        if (candidates.isEmpty()) return;

        List<Feature> nonAggregateCandidates = new ArrayList<>(candidates);
        if (dataSetFilter.aggregator() != null) {
            Feature aggregateFeature = this.aggregateFeatures(candidates, dataSetFilter.aggregator());
            candidates.clear();
            candidates.add(aggregateFeature);
        }

        // Apply the requested spatial predicates to each candidate.
        for (Feature candidate : candidates) {

            for (SpatialOperator operator : operators) {
                boolean match = false;

                switch (operator) {
                    case CONTAINS -> match = referenceGeometry.contains(candidate.getGeometry());
                    case WITHIN -> match = referenceGeometry.within(candidate.getGeometry());
                    case TOUCHES -> match = referenceGeometry.touches(candidate.getGeometry());
                    case COVERED_BY ->  match = referenceGeometry.coveredBy(candidate.getGeometry());
                    case COVERED_BY_BOUNDARY -> match = referenceGeometry.coveredBy(candidate.getGeometry().getBoundary());
                    case EQUALS_TOPO -> match = referenceGeometry.getGeometry().equalsTopo(candidate.getGeometry());
                    case INTERSECTS -> match = referenceGeometry.intersects(candidate.getGeometry());
                    case OVERLAPS -> match = referenceGeometry.overlaps(candidate.getGeometry());
                    case CROSSES -> match = referenceGeometry.crosses(candidate.getGeometry());
                }

                if (match) {
                    if (dataSetFilter.aggregator() != null) {
                        for (Feature nonAggregateCandidate : nonAggregateCandidates) {
                            if (referenceGeometry.getGeometry().intersects(nonAggregateCandidate.getGeometry())) {
                                result.add(nonAggregateCandidate);
                            }
                        }
                    } else {
                        result.add(candidate);
                    }
                }
            }
        }

        // Check SURROUNDED_BY
        if (operators.contains(SpatialOperator.SURROUNDED_BY)) {
            Geometry referenceOuterBoundary = this.getOuterBoundary(referenceFeature.getGeometry());

            Feature unionCandidate;
            if (dataSetFilter.aggregator() == null) {
                unionCandidate = this.aggregateFeatures(candidates, SpatialAggregator.UNION);
            } else {
                unionCandidate = candidates.stream().findFirst().orElse(null);
            }

            if (unionCandidate == null) return;

            if (referenceOuterBoundary.coveredBy(unionCandidate.getGeometry())) {
                for (Feature candidate : candidates) {
                    if (candidate.getGeometry().intersects(referenceOuterBoundary)) {
                        result.add(candidate);
                    }
                }
            }
        }
    }

    /**
     * Prepares the DataSetFilter to load only features that intersect the reference feature’s bounding box.
     * Add relation ids of the relation filter.
     */
    private DataSetFilter prepareDataSetFilter(DataSetFilter dataSetFilter, Envelope referenceEnvelope) {
        boolean featureFilterIsSet = dataSetFilter != null && dataSetFilter.featureFilter() != null;

        return new DataSetFilter(
                (dataSetFilter != null) ? dataSetFilter.ignoreChangesetData() : null,
                (dataSetFilter != null) ? dataSetFilter.coordinateReferenceSystem() : null,
                (dataSetFilter != null) ? dataSetFilter.aggregator() : null,
                new FeatureFilter(
                        (featureFilterIsSet) ? ((dataSetFilter.featureFilter().osmIds() != null) ? dataSetFilter.featureFilter().osmIds() : null) : null,
                        (featureFilterIsSet) ? dataSetFilter.featureFilter().tags() : null,
                        (featureFilterIsSet && (dataSetFilter.featureFilter().boundingBox() != null)) ?
                                dataSetFilter.featureFilter().boundingBox() :
                                        new BoundingBox(
                                            referenceEnvelope.getMinX(), referenceEnvelope.getMinY(),
                                            referenceEnvelope.getMaxX(), referenceEnvelope.getMaxY()),
                        (featureFilterIsSet) ? dataSetFilter.featureFilter().role() : null
                )
        );
    }


    /**
     * Aggregate Features.
     */
    private Feature aggregateFeatures(List<Feature> features, SpatialAggregator aggregator) {
        if (aggregator == SpatialAggregator.UNION) {
            Geometry aggregateGeometry = UnaryUnionOp.union(features.stream().map(Feature::getGeometry).toList());
            return new Feature(aggregateGeometry, null, Collections.emptyList());
        }

        return null;
    }

    /**
     * Get outer boundary.
     */
    private Geometry getOuterBoundary(Geometry geometry) {
        GeometryFactory geometryFactory = new GeometryFactory();
        List<LineString> rings = new ArrayList<>();

        if (geometry instanceof Polygon polygon) {
            rings.add(polygon.getExteriorRing());
        } else if (geometry instanceof MultiPolygon multiPolygon) {
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                rings.add(polygon.getExteriorRing());
            }
        } else if (geometry instanceof LineString || geometry instanceof MultiLineString) {
            return geometry;
        } else {
            return geometry.getBoundary();
        }

        return geometryFactory.createMultiLineString(rings.toArray(LineString[]::new));
    }

    /**
     * Returns a data set of all relation members.
     */
    @Override
    public DataSetDto getRelationMembers(Long relationId, String role) {
        return this.getRelationMembers(relationId, role, null);
    }

    @Override
    public DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem) {
        DataSetDto relationMembers = this.changesetDataService.getRelationMembers(1L, relationId, role, coordinateReferenceSystem);

        if (relationMembers.nodes().isEmpty() && relationMembers.ways().isEmpty() &&
                relationMembers.areas().isEmpty() && relationMembers.relations().isEmpty()) {
            relationMembers = this.osmGeometriesService.getRelationMembers(relationId, role, coordinateReferenceSystem);
        }

        return relationMembers;
    }

    /* ---------- Helpers ---------- */
    private static <T extends TaggedObject> void addAll(List<T> target, List<T> src) {
        if (src != null && !src.isEmpty()) target.addAll(src);
    }

    private static <T extends TaggedObject> void upsertAll(List<T> target, List<T> src) {
        if (src == null || src.isEmpty()) return;
        for (T obj : src) {
            int idx = indexOfId(target, obj.getOsmId());
            if (idx >= 0) target.set(idx, obj); else target.add(obj);
        }
    }

    private static <T extends TaggedObject> void removeAll(List<T> target, Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        target.removeIf(o -> ids.contains(o.getOsmId()));
    }

    private static int indexOfId(List<? extends TaggedObject> list, Long id) {
        for (int i = 0; i < list.size(); i++) {
            if (id != null && id.equals(list.get(i).getOsmId())) return i;
        }
        return -1;
    }

    private static Set<Long> idsOf(List<? extends TaggedObject> list) {
        return (list == null || list.isEmpty())
                ? java.util.Collections.emptySet()
                : list.stream().map(TaggedObject::getOsmId).collect(java.util.stream.Collectors.toSet());
    }
}
