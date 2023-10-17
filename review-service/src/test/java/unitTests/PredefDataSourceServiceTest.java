package unitTests;

import com.tfg.review.dtos.CustomDataSourceDTO;
import com.tfg.review.dtos.PredefDataSourceDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.models.CustomDataSource;
import com.tfg.review.models.PredefDataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.PredefDataSourceRepository;
import com.tfg.review.services.implementations.PredefDataSourceServiceImpl;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PredefDataSourceServiceTest {

    @InjectMocks
    private PredefDataSourceServiceImpl predefDataSourceService;
    @Mock
    private PredefDataSourceRepository predefDataSourceRepository;
    @Mock
    private ProtocolServiceImpl protocolService;

    @Test
    public void findAll_OK(){
        List<PredefDataSource> dataSourceList = Arrays.asList(new PredefDataSource(), new PredefDataSource());
        when(predefDataSourceRepository.findAll()).thenReturn(dataSourceList);

        List<PredefDataSource> result = predefDataSourceService.findAll();
        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void findOne_OK(){
        PredefDataSource dataSource = new PredefDataSource();

        when(predefDataSourceRepository.findPredefDataSourceById(1L)).thenReturn(Optional.of(dataSource));
        Optional<PredefDataSource> predefDataSource = predefDataSourceService.findOne(1L);

        Assertions.assertTrue(predefDataSource.isPresent());
    }

    @Test
    public void findOne_KO(){
        when(predefDataSourceRepository.findPredefDataSourceById(null)).thenReturn(Optional.empty());
        Optional<PredefDataSource> predefDataSource = predefDataSourceService.findOne(null);

        Assertions.assertTrue(predefDataSource.isEmpty());
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 12L;
        Protocol protocol = Protocol.builder().dataSources(new HashSet<>()).build();
        PredefDataSourceDTO dto = PredefDataSourceDTO.builder().build();

        when(protocolService.findOne(protocolId)).thenReturn(protocol);
        when(protocolService.update(protocol)).thenReturn(null);

        PredefDataSourceDTO resultDTO = predefDataSourceService.createAndSave(dto, protocolId);

        Assertions.assertDoesNotThrow(()-> predefDataSourceService.createAndSave(dto, protocolId));
        Assertions.assertNotNull(resultDTO);
    }

    @Test
    public void createAndSave_NullDataSource(){
        assertThrows(NullEntityException.class, ()-> predefDataSourceService.createAndSave(null, 2L));
    }

    @Test
    public void createAndSave_ProtocolNotFound(){
        Long protocolId = 12L;
        doThrow(NullEntityException.class).when(protocolService).findOne(anyLong());
        assertThrows(NullEntityException.class, ()-> predefDataSourceService.createAndSave(new PredefDataSourceDTO(), protocolId));
    }

    @Test
    public void update_OK(){
        Long dataSourceId = 1L;
        PredefDataSource dataSource = PredefDataSource.builder().build();
        dataSource.setId(dataSourceId);

        when(predefDataSourceRepository.existsById(dataSourceId)).thenReturn(true);
        when(predefDataSourceRepository.save(any(PredefDataSource.class))).thenReturn(dataSource);

        PredefDataSource result = predefDataSourceService.update(dataSource);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dataSource, result);
    }

    @Test
    public void update_KO(){
        Long dataSourceId = 1L;
        PredefDataSource dataSource = new PredefDataSource();
        dataSource.setId(dataSourceId);

        when(predefDataSourceRepository.existsById(dataSourceId)).thenReturn(false);

        Assertions.assertThrows(DataSourceNotFoundException.class, () -> predefDataSourceService.update(dataSource));
    }

    @Test
    public void delete_OK(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;
        PredefDataSource dataSource = new PredefDataSource();
        Protocol protocol = new Protocol();

        when(predefDataSourceRepository.findPredefDataSourceById(dataSourceId)).thenReturn(Optional.of(dataSource));
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        predefDataSourceService.deleteFromProtocol(dataSourceId, protocolId);

        Assertions.assertFalse(protocol.getDataSources().contains(dataSource));
    }

    @Test
    public void delete_KO(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;

        when(predefDataSourceRepository.findPredefDataSourceById(dataSourceId)).thenReturn(java.util.Optional.empty());

        assertThrows(DataSourceNotFoundException.class, () -> predefDataSourceService.deleteFromProtocol(dataSourceId, protocolId));
    }
}
