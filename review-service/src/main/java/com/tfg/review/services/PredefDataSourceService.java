package com.tfg.review.services;

import com.tfg.review.dtos.PredefDataSourceDTO;
import com.tfg.review.models.PredefDataSource;
import java.util.List;
import java.util.Optional;

public interface PredefDataSourceService {

    List<PredefDataSource> findAll();

    Optional<PredefDataSource> findOne(Long id);

    PredefDataSourceDTO createAndSave(PredefDataSourceDTO dataSource, Long protocolId);

    PredefDataSource update(PredefDataSource dataSource);

    void deleteFromProtocol(Long dataSourceId, Long protocolId);
}
