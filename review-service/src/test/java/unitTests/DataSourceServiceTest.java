package unitTests;

import com.tfg.review.dtos.SearchDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.SelectionCriteriaNotFoundException;
import com.tfg.review.models.*;
import com.tfg.review.repositories.DataSourceRepository;
import com.tfg.review.services.DataSourceService;
import com.tfg.review.services.implementations.DataSourceServiceImpl;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataSourceServiceTest {

    @InjectMocks
    private DataSourceServiceImpl dataSourceService;

    @Mock
    private DataSourceRepository dataSourceRepository;
    @Mock
    private ProtocolServiceImpl protocolService;
    @Mock
    private RestTemplate restTemplate;

    @Test
    public void findAll_OK(){
        List<DataSource> dataSources =Arrays.asList(new PredefDataSource(), new CustomDataSource(), new Snowballing());
        when(dataSourceRepository.findAll()).thenReturn(dataSources);

        List<DataSource> result = dataSourceService.findAll();

        Assertions.assertEquals(dataSources, result);
    }

    @Test
    public void findOne_OK(){
        Long dataSourceId = 2L;
        when(dataSourceRepository.existsById(dataSourceId)).thenReturn(true);
        when(dataSourceRepository.findDataSourceById(anyLong())).thenReturn(Optional.of(new DataSource()));

        Optional<DataSource> result = dataSourceService.findOne(dataSourceId);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findOne_KO(){
        Long dataSourceId = 2L;
        when(dataSourceRepository.existsById(dataSourceId)).thenReturn(false);

        Assertions.assertThrows(DataSourceNotFoundException.class, ()-> dataSourceService.findOne(dataSourceId));
    }

    @Test
    public void delete_OK() {
        Long dataSourceId = 1L;
        DataSource dataSource = new DataSource();
        when(dataSourceRepository.findDataSourceById(dataSourceId)).thenReturn(Optional.of(dataSource));

        Protocol protocol1 = Protocol.builder().id(4L).build();
        Protocol protocol2 = Protocol.builder().id(3L).build();
        Set<Protocol> protocols = new HashSet<>();
        protocols.add(protocol1);
        protocols.add(protocol2);
        dataSource.setProtocols(protocols);

        Set<DataSource> dataSources = new HashSet<>();
        dataSources.add(dataSource);
        protocol1.setDataSources(dataSources);
        protocol2.setDataSources(dataSources);

        when(protocolService.update(any(Protocol.class))).thenReturn(null);

        dataSourceService.delete(dataSourceId);

        Assertions.assertFalse(protocol1.getDataSources().contains(dataSource));
        Assertions.assertFalse(protocol2.getDataSources().contains(dataSource));
    }

    @Test
    public void delete_NullId() {
        Assertions.assertThrows(NullEntityException.class, () -> dataSourceService.delete(null));
    }

    @Test
    public void delete_NotFound() {
        Long dataSourceId = 1L;
        when(dataSourceRepository.findDataSourceById(dataSourceId)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataSourceNotFoundException.class, () -> dataSourceService.delete(dataSourceId));
    }

    @Test
    public void createSearch_OK(){
        SearchDTO dto = SearchDTO.builder().build();
        Long dataSourceId = 3L;

        when(restTemplate.postForObject("http://search-service/api/search/3/create-search", dto, SearchDTO.class)
        ).thenReturn(new ResponseEntity<SearchDTO>(dto, null, 200).getBody());

        SearchDTO result = dataSourceService.createSearch(dto, dataSourceId);
        Assertions.assertNotNull(result);
        Assertions.assertDoesNotThrow(()->dataSourceService.createSearch(dto, dataSourceId));
    }

    @Test
    public void createSearch_KO(){
        Assertions.assertThrows(NullEntityException.class, ()-> dataSourceService.createSearch(new SearchDTO(), null));
    }

}
