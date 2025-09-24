package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.components.Osm2PgSqlRunner;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.repository.ChangesetPrepareRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.mapper.ChangesetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangesetPrepareServiceImpl implements ChangesetPrepareService {

    private final ChangesetPrepareRepository changesetPrepareRepository;
    private final Osm2PgSqlRunner osm2PgSqlRunner;

    @Override
    public void prepareChangeset(Long changesetId, ChangesetDto changesetDto) {
        Changeset changeset = ChangesetMapper.toDomain(changesetId, changesetDto);

        try {
            this.changesetPrepareRepository.createSchemaByChangesetId(changesetId);
            this.changesetPrepareRepository.insertDependingOsmObjects(changeset);

            System.out.println(ChangesetXml.toXml(changeset));
            this.osm2PgSqlRunner.appendChangeset(changeset);
        } finally {
            this.changesetPrepareRepository.dropSchemaByChangeset(changesetId);
        }

        // Todo: Kopiere alle abhängigen Osm-Objekte in das neue Schema

        // Todo: Appende den Changeset mithilfe von Osm2PgSql zum Schema changset_prepare_xxx

        // Todo: Rufe die Schnittstelle auf vom Modul changeset_data, welche moveFromPrepare aufruft.
        //       (Dort wird alles rüberkopiert und anhand der Tabellen node, way, area, relation ein changset_objects Eintrag erstellt)
        //       (Zudem werden die Math.Pow(10, 17) minus gerechnet => negative Ids wieder in der changeset_data)

        // Todo: Lösche das Schema changeset_prepare_xxx wieder (muss immer gemacht werden, auch bei Absturz)

    }



}
