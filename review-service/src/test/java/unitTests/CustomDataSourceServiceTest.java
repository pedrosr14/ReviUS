package unitTests;

import com.tfg.review.dtos.CustomDataSourceDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.CustomDataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.CustomDataSourceRepository;
import com.tfg.review.services.implementations.CustomDataSourceServiceImpl;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomDataSourceServiceTest {

    @InjectMocks
    private CustomDataSourceServiceImpl customDataSourceService;
    @Mock
    private CustomDataSourceRepository customRepository;
    @Mock
    private ProtocolServiceImpl protocolService;

    @Test
    public void findAll_OK(){
        List<CustomDataSource> dataSourceList = Arrays.asList(new CustomDataSource(), new CustomDataSource());
        when(customRepository.findAll()).thenReturn(dataSourceList);

        List<CustomDataSource> result = customDataSourceService.findAll();
        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void findOne_OK(){
        CustomDataSource dataSource = new CustomDataSource();

        when(customRepository.findCustomDataSourceById(1L)).thenReturn(Optional.of(dataSource));
        Optional<CustomDataSource> customDataSource = customDataSourceService.findOne(1L);

        Assertions.assertTrue(customDataSource.isPresent());
    }

    @Test
    public void findOne_KO(){
        when(customRepository.findCustomDataSourceById(null)).thenReturn(Optional.empty());
        Optional<CustomDataSource> customDataSource = customDataSourceService.findOne(null);

        Assertions.assertTrue(customDataSource.isEmpty());
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 12L;
        Protocol protocol = Protocol.builder().dataSources(new HashSet<>()).build();
        CustomDataSourceDTO dto = CustomDataSourceDTO.builder().build();

        CustomDataSource expected = CustomDataSource.builder().build();

        when(protocolService.findOne(protocolId)).thenReturn(protocol);
        when(protocolService.update(protocol)).thenReturn(null);

        CustomDataSourceDTO resultDTO = customDataSourceService.createAndSave(dto, protocolId);

        Assertions.assertDoesNotThrow(()-> customDataSourceService.createAndSave(dto, protocolId));
        Assertions.assertNotNull(resultDTO);
    }

    @Test
    public void createAndSave_NullDataSource(){
        assertThrows(NullEntityException.class, ()-> customDataSourceService.createAndSave(null, 2L));
    }

    @Test
    public void createAndSave_ProtocolNotFound(){
        Long protocolId = 12L;
        doThrow(NullEntityException.class).when(protocolService).findOne(anyLong());
        assertThrows(NullEntityException.class, ()-> customDataSourceService.createAndSave(new CustomDataSourceDTO(), protocolId));
    }

    @Test
    public void update_OK(){
        Long dataSourceId = 1L;
        CustomDataSource dataSource = CustomDataSource.builder().build();
        dataSource.setId(dataSourceId);

        when(customRepository.existsById(dataSourceId)).thenReturn(true);
        when(customRepository.save(any(CustomDataSource.class))).thenReturn(dataSource);

        CustomDataSource result = customDataSourceService.update(dataSource);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dataSource, result);
    }

    @Test
    public void update_KO(){
        Long dataSourceId = 1L;
        CustomDataSource dataSource = new CustomDataSource();
        dataSource.setId(dataSourceId);

        when(customRepository.existsById(dataSourceId)).thenReturn(false);

        Assertions.assertThrows(DataSourceNotFoundException.class, () -> customDataSourceService.update(dataSource));
    }

    @Test
    public void delete_OK(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;
        CustomDataSource customDataSource = new CustomDataSource();
        Protocol protocol = new Protocol();

        when(customRepository.findCustomDataSourceById(dataSourceId)).thenReturn(Optional.of(customDataSource));
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        customDataSourceService.deleteFromProtocol(dataSourceId, protocolId);

        Assertions.assertFalse(protocol.getDataSources().contains(customDataSource));
    }

    @Test
    public void delete_KO(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;

        when(customRepository.findCustomDataSourceById(dataSourceId)).thenReturn(java.util.Optional.empty());

        assertThrows(DataSourceNotFoundException.class, () -> customDataSourceService.deleteFromProtocol(dataSourceId, protocolId));
    }

}
