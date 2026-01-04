package de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.api.ChangesetManagementService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.ChangesetState;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.ChangesetDataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.FeatureMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.operation.union.UnaryUnionOp;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Geodata-View service provides a unified, read-only view on geospatial data
 * by combining the current OSM geometries with the content of relevant changesets.
 * It serves as a central query layer for quality checks and spatial validation.
 */
@Service
@RequiredArgsConstructor
public class GeodataViewServiceImpl implements GeodataViewService {

    private final OsmGeometriesService osmGeometriesService;
    private final ChangesetManagementService changesetManagementService;

    /**
     * Returns all features from the unified geodata view that match the given filter.
     */
    @Override
    public DataSetDto getDataSet(DataSetFilter dataSetFilter) {

        // Load current OSM geometries from the database and map to the domain model.
        DataSet currentDataSet = Optional.ofNullable(this.osmGeometriesService.getDataSet(dataSetFilter))
                .map(DataSetMapper::toDomain)
                .orElse(new DataSet());

        // Optionally load features from specified changesets and merge them into the current dataset.
        if (dataSetFilter.ignoreChangesetData() == null || !dataSetFilter.ignoreChangesetData()) {
            for (Long changesetId : this.changesetManagementService.getChangesetIds(Set.of(ChangesetState.OPEN, ChangesetState.CHECKED))) {

                ChangesetDataSet cs = Optional.ofNullable(this.changesetManagementService.getDataSet(changesetId, dataSetFilter))
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
     * Performs a spatial query on the unified geodata view.
     * <p>
     * Returns all features that satisfy at least one of the specified spatial operators
     * (e.g. CONTAINS, WITHIN, INTERSECTS) with respect to the given reference feature.
     * Optionally, a bounding box is automatically derived from the reference geometry
     * and injected into the dataset filter to limit the search space.
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
        DataSetFilter preparedDataSetFilter = this.addBboxToDataSetFilter(dataSetFilter, referenceEnvelope);

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
                    String candidateRole = (dataSetFilter.memberFilter() == null) ? null : dataSetFilter.memberFilter().role();
                    DataSet candidateMembers = Optional.ofNullable(this.getRelationMembers(relation.getOsmId(), candidateRole))
                            .map(DataSetMapper::toDomain).orElse(null);

                    if  (candidateMembers != null) {
                        List<Feature> candidateMemberFeatures = new ArrayList<>();
                        for (TaggedObject candidateMember : candidateMembers.getAll()) {
                            List<String> allowedObjectTypes = (dataSetFilter.memberFilter() == null) ? null : dataSetFilter.memberFilter().objectTypes();

                            if (allowedObjectTypes == null || allowedObjectTypes.contains(candidateMember.getObjectType())) {
                                if (candidateMember instanceof Feature candidateMemberFeature) {
                                    candidateMemberFeatures.add(candidateMemberFeature);
                                }
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

                    candidateDataSet.getNodes().stream()
                            .filter(cr -> cr.getOsmId().equals(resultFeature.getOsmId()) &&
                                    cr.getObjectType().equals(resultFeature.getObjectType()))
                            .findFirst()
                            .ifPresent(cr -> resultDataSet.getNodes().add(cr));

                    candidateDataSet.getWays().stream()
                            .filter(cr -> cr.getOsmId().equals(resultFeature.getOsmId()) &&
                                    cr.getObjectType().equals(resultFeature.getObjectType()))
                            .findFirst()
                            .ifPresent(cr -> resultDataSet.getWays().add(cr));

                    candidateDataSet.getAreas().stream()
                            .filter(cr -> cr.getOsmId().equals(resultFeature.getOsmId()) &&
                                    cr.getObjectType().equals(resultFeature.getObjectType()))
                            .findFirst()
                            .ifPresent(cr -> resultDataSet.getAreas().add(cr));

                    candidateDataSet.getRelations().stream()
                            .filter(cr -> cr.getOsmId().equals(resultFeature.getOsmId()) &&
                                                    cr.getObjectType().equals(resultFeature.getObjectType()))
                            .findFirst()
                            .ifPresent(cr -> resultDataSet.getRelations().add(cr));
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
            if (!selfCheck &&
                    (Objects.equals(candidate.getOsmId(), referenceFeature.getOsmId()) &&
                     candidate.getObjectType().equals(referenceFeature.getObjectType())) ||
                    (Optional.ofNullable(referenceFeature.getRelations()).orElseGet(List::of).stream()
                        .anyMatch(r -> Objects.equals(r.getOsmId(), candidate.getOsmId())
                            && Objects.equals(r.getObjectType(), candidate.getObjectType())))) {
                continue;
            }

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
                    case TOUCHES_ENDPOINT_ONLY -> match = referenceGeometry.getGeometry().relate(candidate.getGeometry(), "FF*FT****");
                    case COVERED_BY ->  match = referenceGeometry.coveredBy(candidate.getGeometry());
                    case COVERED_BY_BOUNDARY -> match = referenceGeometry.coveredBy(candidate.getGeometry().getBoundary());
                    case COVERED_BY_MULTILINE_AS_POLYGON -> match = toPolygon(referenceGeometry.getGeometry()).coveredBy(toPolygon(candidate.getGeometry()));
                    case EQUALS_TOPO, EQUALS -> match = referenceGeometry.getGeometry().equalsTopo(candidate.getGeometry());
                    case EQUALS_TOPO_BY_MULTILINE_AS_POLYGON, EQUALS_BY_MULTILINE_AS_POLYGON -> match = toPolygon(referenceGeometry.getGeometry()).equalsTopo(toPolygon(candidate.getGeometry()));
                    case INTERSECTS -> match = referenceGeometry.intersects(candidate.getGeometry());
                    case OVERLAPS -> match = referenceGeometry.overlaps(candidate.getGeometry());
                    case OVERLAPS_BY_MULTILINE_AS_POLYGON -> match = toPolygon(referenceGeometry.getGeometry()).overlaps(toPolygon(candidate.getGeometry()));
                    case CROSSES -> match = referenceGeometry.crosses(candidate.getGeometry());
                    case COVERS -> match = referenceGeometry.covers(candidate.getGeometry());
                    case COVERS_BY_MULTILINE_AS_POLYGON -> match = toPolygon(referenceGeometry.getGeometry()).covers(toPolygon(candidate.getGeometry()));
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
        }
    }

    /**
     * Prepares the DataSetFilter to load only features that intersect the reference feature’s bounding box.
     * Add relation ids of the relation filter.
     */
    private DataSetFilter addBboxToDataSetFilter(DataSetFilter dataSetFilter, Envelope referenceEnvelope) {
        boolean hasBboxFilter = false;
        if (dataSetFilter != null && dataSetFilter.criteria() != null) {
            switch (dataSetFilter.criteria()) {
                case Leaf leaf -> hasBboxFilter = "bbox".equalsIgnoreCase(leaf.type());
                case All(List<Criteria> items) -> {
                    if (items != null) {
                        hasBboxFilter = items.stream().anyMatch(it -> it instanceof Leaf leaf && "bbox".equalsIgnoreCase(leaf.type()));
                    }
                }
                case Any(List<Criteria> items) -> {
                    if (items != null) {
                        hasBboxFilter = items.stream().anyMatch(it -> it instanceof Leaf leaf && "bbox".equalsIgnoreCase(leaf.type()));
                    }
                }
                default -> {}
            }
        }

        // Bounding Box already on top level
        if (hasBboxFilter) return dataSetFilter;

        // Set Bounding Box and return new dataset filter
        Leaf boundingBoxLeaf = new Leaf("bbox", Map.of("min_x", referenceEnvelope.getMinX(), "min_y", referenceEnvelope.getMinY(),
                "max_x", referenceEnvelope.getMaxX(), "max_y", referenceEnvelope.getMaxY()));

        return new DataSetFilter(
                (dataSetFilter != null) ? dataSetFilter.ignoreChangesetData() : null,
                (dataSetFilter != null) ? dataSetFilter.coordinateReferenceSystem() : null,
                (dataSetFilter != null) ? dataSetFilter.aggregator() : null,
                (dataSetFilter != null) ? dataSetFilter.osmIds() : null,
                (dataSetFilter == null || dataSetFilter.criteria() == null) ? boundingBoxLeaf : new All(List.of(dataSetFilter.criteria(), boundingBoxLeaf)),
                (dataSetFilter != null) ? dataSetFilter.memberFilter() : null);
    }


    /**
     * Aggregates the geometries of the given features using the specified spatial
     * aggregator and returns a synthetic feature representing the result.
     */
    private Feature aggregateFeatures(List<Feature> features, SpatialAggregator aggregator) {
        if (aggregator == SpatialAggregator.UNION) {
            Geometry aggregateGeometry = UnaryUnionOp.union(features.stream().map(Feature::getGeometry).toList());
            return new Feature(aggregateGeometry, null, Collections.emptyList());
        }

        return null;
    }

    /**
     * Computes the outer boundary geometry for the given geometry.
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
     * Returns a dataset containing all members of the specified relation.
     * Uses the default coordinate reference system.
     */
    @Override
    public DataSetDto getRelationMembers(Long relationId, String role) {
        return this.getRelationMembers(relationId, role, null);
    }

    /**
     * Returns a dataset containing all members of the specified relation.
     */
    @Override
    public DataSetDto getRelationMembers(Long relationId, String role, String coordinateReferenceSystem) {
        DataSetDto relationMembers = null;
        for (Long changesetId : this.changesetManagementService.getChangesetIds(Set.of(ChangesetState.OPEN, ChangesetState.CHECKED))) {
            relationMembers = this.changesetManagementService.getRelationMembers(changesetId, relationId, role, coordinateReferenceSystem);
        }

        if (relationMembers == null || (relationMembers.nodes().isEmpty() && relationMembers.ways().isEmpty() &&
                relationMembers.areas().isEmpty() && relationMembers.relations().isEmpty())) {
            relationMembers = this.osmGeometriesService.getRelationMembers(relationId, role, coordinateReferenceSystem);
        }

        return relationMembers;
    }

    /**
     * Returns the geometry nodes of the given way.
     */
    @Override
    public List<Feature> getWayNodesAsFeature(TaggedObject taggedObject) {
        List<Feature> wayNodeFeatures = new ArrayList<>();

        if (taggedObject instanceof Feature way) {
            if (way.getGeometryNodes() != null && !way.getGeometryNodes().isEmpty()) {
                Set<Long> osmIds = way.getGeometryNodes().stream().map(GeometryNode::getOsmId).collect(Collectors.toSet());

                DataSet wayNodeTaggedFeatures = Optional.ofNullable(
                                this.getDataSet(
                                        new DataSetFilter(null, null, null,
                                                new OsmIds(osmIds, null, null, null), null, null)))
                        .map(DataSetMapper::toDomain)
                        .orElse(null);

                for (GeometryNode geometryNode : way.getGeometryNodes()) {
                    Feature wayNodeFeature = null;
                    if (wayNodeTaggedFeatures != null && wayNodeTaggedFeatures.getNodes() != null) {
                        wayNodeFeature = wayNodeTaggedFeatures.getNodes()
                                .stream().filter(n -> n.getOsmId().equals(geometryNode.getOsmId())).findFirst().orElse(null);
                    }

                    if (wayNodeFeature == null) {
                        wayNodeFeature = new Feature(
                                geometryNode.getGeometry(),
                                geometryNode.getGeometryTransformed(),
                                List.of(geometryNode)
                        );

                        wayNodeFeature.setOsmId(geometryNode.getOsmId());
                    }

                    wayNodeFeatures.add(wayNodeFeature);
                }
            }
        }

        return wayNodeFeatures;
    }

    /* ---------- Helpers for set operations on feature lists ---------- */
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
                ? Collections.emptySet()
                : list.stream().map(TaggedObject::getOsmId).collect(Collectors.toSet());
    }

    public static Geometry toPolygon(Geometry geom) {
        if (geom == null || geom.isEmpty()) {
            return emptyPolygon(geom);
        }

        // Already area
        if (geom instanceof Polygon || geom instanceof MultiPolygon) {
            return geom;
        }

        // Optional: if it's a collection, you can union it first
        // (helps if you sometimes get GeometryCollection of linework)
        if (geom instanceof GeometryCollection && !(geom instanceof MultiLineString)) {
            geom = geom.union();
            if (geom instanceof Polygon || geom instanceof MultiPolygon) {
                return geom;
            }
        }

        // Linework -> polygonize
        if (geom instanceof LineString || geom instanceof MultiLineString) {
            GeometryFactory gf = geom.getFactory();

            // Noding: split at intersections, fixes many "many segments" cases
            Geometry noded = geom.union();

            Polygonizer polygonizer = new Polygonizer();
            polygonizer.add(noded);

            @SuppressWarnings("unchecked")
            Collection<Polygon> polygons = (Collection<Polygon>) polygonizer.getPolygons();

            if (polygons == null || polygons.isEmpty()) {
                return gf.createPolygon(); // empty => coveredBy will be false
            }

            Geometry area = gf.createMultiPolygon(polygons.toArray(new Polygon[0])).union();
            return area;
        }

        // Fallback: cannot convert meaningfully
        return emptyPolygon(geom);
    }

    private static Polygon emptyPolygon(Geometry geom) {
        GeometryFactory gf = (geom != null) ? geom.getFactory() : new GeometryFactory();
        return gf.createPolygon();
    }
}
