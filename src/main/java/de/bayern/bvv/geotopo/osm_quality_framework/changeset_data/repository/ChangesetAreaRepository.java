package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangesetAreaRepository extends JpaRepository<AreaEntity,Long>, ChangesetAreaRepositoryCustom {}
