package com.tfg.review.services;

import com.tfg.review.dtos.SearchDTO;
import com.tfg.review.models.DataSource;

import java.util.List;
import java.util.Optional;

public interface DataSourceService {

    List<DataSource> findAll();

    Optional<DataSource> findOne(Long id);

    void delete (Long dataSourceId);

    //--Other methods--//

    /**
     *
     * @param dto DTO with the information ot the Search that the Researcher wants to create
     * @param dataSourceId ID of the DataSource to which the Search will be associated
     * @return A DTO with the data of the Search that was created
     */
    SearchDTO createSearch(SearchDTO dto, Long dataSourceId);
}
