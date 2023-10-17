package com.tfg.slr.searchservice.services.implementations;

import com.tfg.slr.searchservice.dtos.SearchDTO;
import com.tfg.slr.searchservice.exceptions.SearchNotFoundException;
import com.tfg.slr.searchservice.models.Search;
import com.tfg.slr.searchservice.repositories.SearchRepository;
import com.tfg.slr.searchservice.services.SearchService;
import com.tfg.slr.searchservice.utils.MessageConstants;
import com.tfg.slr.searchservice.utils.ReferenceGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private SearchRepository searchRepository;

    public Search findOne(Long id){
        return searchRepository.findSearchById(id).orElseThrow(()-> new SearchNotFoundException("Search doesn't exist"));
    }

    public List<Search> findAll(){
        return searchRepository.findAll();
    }

    public Optional<Search> findByReference(String ref){
        return searchRepository.findSearchBySearchReference(ref);
    }

    public Search createAndSave(Long dataSourceId, SearchDTO dto){
        if(dataSourceId==null) throw  new IllegalArgumentException("Can't create a search from null data source ID");
        if(dto == null) throw new IllegalArgumentException("Search dto is null");

        Search search = SearchDTO.buildEntity(dto);
        String ref = ReferenceGenerator.generateRef();
        while(findByReference(ref).isPresent()){
            ref = ReferenceGenerator.generateRef();
        }
        search.setSearchReference(ref);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -10);
        search.setSearchDate(calendar.getTime());

        search.setDataSourceId(dataSourceId);

        return searchRepository.save(search);
    }

    public Search update (Search search){
        if(search == null) throw new SearchNotFoundException(MessageConstants.SEARCH_NOT_FOUND);
        return searchRepository.save(search);
    }


}
