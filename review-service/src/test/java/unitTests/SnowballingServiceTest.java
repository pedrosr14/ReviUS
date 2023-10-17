package unitTests;

import com.tfg.review.dtos.SnowballingDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.SnowballingNotFoundException;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.Snowballing;
import com.tfg.review.repositories.SnowballingRepository;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.services.implementations.SnowballingServiceImpl;
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
public class SnowballingServiceTest {

    @InjectMocks
    private SnowballingServiceImpl snowballingService;

    @Mock
    private ProtocolService protocolService;

    @Mock
    private SnowballingRepository snowballingRepository;

    @Test
    public void findAll_OK(){
        List<Snowballing> snowballingList = Arrays.asList(new Snowballing(), new Snowballing());
        when(snowballingRepository.findAll()).thenReturn(snowballingList);

        List<Snowballing> result = snowballingService.findAll();
        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void findOne_OK(){
        Snowballing snowballing = new Snowballing();

        when(snowballingRepository.findSnowballingById(1L)).thenReturn(Optional.of(snowballing));
        Optional<Snowballing> snowballingOptional = snowballingService.findOne(1L);

        Assertions.assertTrue(snowballingOptional.isPresent());
    }

    @Test
    public void findOne_KO(){
        when(snowballingRepository.findSnowballingById(null)).thenReturn(Optional.empty());
        Optional<Snowballing> snowballingOptional = snowballingService.findOne(null);

        Assertions.assertTrue(snowballingOptional.isEmpty());
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 12L;
        Protocol protocol = Protocol.builder().dataSources(new HashSet<>()).build();
        SnowballingDTO dto = SnowballingDTO.builder().snowballingType("BACKWARDS").build();

        when(protocolService.findOne(protocolId)).thenReturn(protocol);
        when(protocolService.update(protocol)).thenReturn(null);

        SnowballingDTO resultDTO = snowballingService.createAndSave(dto, protocolId);

        Assertions.assertDoesNotThrow(()-> snowballingService.createAndSave(dto, protocolId));
        Assertions.assertNotNull(resultDTO);
    }

    @Test
    public void createAndSave_NullDataSource(){
        assertThrows(NullEntityException.class, ()-> snowballingService.createAndSave(null, 2L));
    }

    @Test
    public void createAndSave_ProtocolNotFound(){
        Long protocolId = 12L;
        doThrow(NullEntityException.class).when(protocolService).findOne(anyLong());
        assertThrows(NullEntityException.class, ()-> snowballingService.createAndSave(new SnowballingDTO(), protocolId));
    }

    @Test
    public void update_OK(){
        Long dataSourceId = 1L;
        Snowballing snowballing = Snowballing.builder().build();
        snowballing.setId(dataSourceId);

        when(snowballingRepository.existsById(dataSourceId)).thenReturn(true);
        when(snowballingRepository.save(any(Snowballing.class))).thenReturn(snowballing);

        Snowballing result = snowballingService.update(snowballing);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(snowballing, result);
    }

    @Test
    public void update_KO(){
        Long dataSourceId = 1L;
        Snowballing snowballing = new Snowballing();
        snowballing.setId(dataSourceId);

        when(snowballingRepository.existsById(dataSourceId)).thenReturn(false);

        Assertions.assertThrows(SnowballingNotFoundException.class, () -> snowballingService.update(snowballing));
    }

    @Test
    public void deleteFromProtocol_OK(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;
        Snowballing snowballing = new Snowballing();
        Protocol protocol = new Protocol();

        when(snowballingRepository.findSnowballingById(dataSourceId)).thenReturn(Optional.of(snowballing));
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        snowballingService.deleteFromProtocol(dataSourceId, protocolId);

        Assertions.assertFalse(protocol.getDataSources().contains(snowballing));
    }

    @Test
    public void deleteFromProtocol_KO(){
        Long dataSourceId = 1L;
        Long protocolId = 2L;

        when(snowballingRepository.findSnowballingById(dataSourceId)).thenReturn(java.util.Optional.empty());

        assertThrows(SnowballingNotFoundException.class, () -> snowballingService.deleteFromProtocol(dataSourceId, protocolId));
    }
}
