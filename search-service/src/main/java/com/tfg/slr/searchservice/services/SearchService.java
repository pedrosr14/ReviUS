package com.tfg.slr.searchservice.services;

import com.tfg.slr.searchservice.dtos.SearchDTO;
import com.tfg.slr.searchservice.exceptions.SearchNotFoundException;
import com.tfg.slr.searchservice.models.Search;
import com.tfg.slr.searchservice.utils.ReferenceGenerator;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public interface SearchService {

    Search findOne(Long id);

    List<Search> findAll();

    Optional<Search> findByReference(String ref);

    Search createAndSave(Long dataSourceId, SearchDTO dto);

    Search update (Search search);
}
