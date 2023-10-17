package com.tfg.review.services;

import com.tfg.review.dtos.CustomDataSourceDTO;
import com.tfg.review.models.CustomDataSource;
import java.util.List;
import java.util.Optional;

public interface CustomDataSourceService {

    List<CustomDataSource> findAll();
    Optional<CustomDataSource> findOne(Long id);
    CustomDataSourceDTO createAndSave(CustomDataSourceDTO dataSource, Long protocolId);
    CustomDataSource update(CustomDataSource dataSource);
    void deleteFromProtocol(Long dataSourceId, Long protocolId);
}
