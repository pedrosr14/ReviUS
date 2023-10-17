package com.tfg.review.services.implementations;

import com.tfg.review.dtos.SearchDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.DataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.DataSourceRepository;
import com.tfg.review.services.DataSourceService;
import com.tfg.review.utils.MessageConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    private final ProtocolServiceImpl protocolService;
    private final RestTemplate restTemplate;

    //--CRUD--//

    public List<DataSource> findAll(){

        return dataSourceRepository.findAll();
    }

    public Optional<DataSource> findOne(Long id){
        if(!dataSourceRepository.existsById(id)) throw new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND);

        return dataSourceRepository.findDataSourceById(id);
    }

    public void delete (Long dataSourceId) {
        if(dataSourceId==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        DataSource dataSource = dataSourceRepository.findDataSourceById(dataSourceId).orElseThrow(() -> new DataSourceNotFoundException(MessageConstants.DATA_SOURCE_NOT_FOUND));

        List<Protocol> protocols = List.copyOf(dataSource.getProtocols());
        for (Protocol protocol : protocols) {
            protocol.getDataSources().remove(dataSource);
            protocolService.update(protocol);
        }
        dataSourceRepository.delete(dataSource);
    }

    //--Other methods--//

    public SearchDTO createSearch(SearchDTO dto, Long dataSourceId){
        if(dataSourceId==null) throw new NullEntityException(MessageConstants.NULL_ID_ENTITY);
        return restTemplate.postForObject("http://search-service/api/search/"+dataSourceId+"/create-search", dto, SearchDTO.class);
    }
}
