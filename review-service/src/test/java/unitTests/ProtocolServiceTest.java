package unitTests;

import com.tfg.review.dtos.ProtocolDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ProtocolNotFoundException;
import com.tfg.review.exceptions.SLRNotFoundException;
import com.tfg.review.models.*;
import com.tfg.review.repositories.*;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProtocolServiceTest {

    @InjectMocks
    private ProtocolServiceImpl protocolService;

    @Mock
    private ProtocolRepository protocolRepository;
    @Mock
    private SLRRepository slrRepository;
    @Mock
    private SelectionCriteriaRepository selectionCriteriaRepository;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private CustomDataSourceRepository customDataSourceRepository;
    @Mock
    private PredefDataSourceRepository predefDataSourceRepository;
    @Mock
    private SnowballingRepository snowballingRepository;

    @Test
    public void findAll_OK(){
        List<Protocol> protocols = Arrays.asList(new Protocol(), new Protocol());

        when(protocolRepository.findAll()).thenReturn(protocols);

        List<Protocol> result = protocolService.findAll();
        Assertions.assertEquals(protocols, result);
    }

    @Test
    public void findOne_OK(){
        Long protocolId = 3L;
        when(protocolRepository.findProtocolById(protocolId)).thenReturn(Optional.of(new Protocol()));

        Protocol result = protocolService.findOne(protocolId);
        Assertions.assertNotNull(result);
        Assertions.assertDoesNotThrow(()-> protocolService.findOne(protocolId));
    }

    @Test
    public void findOne_KO(){
        Long protocolId = 2L;
        when(protocolRepository.findProtocolById(protocolId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProtocolNotFoundException.class, (()-> protocolService.findOne(protocolId)));
    }

    @Test
    public void createAndSave_OK(){
        Long slrID = 1L;
        SLR slr = SLR.builder().build();
        ProtocolDTO dto = new ProtocolDTO();
        Protocol protocol = ProtocolDTO.buildEntity(dto);

        when(slrRepository.findSLRById(slrID)).thenReturn(Optional.of(slr));

        Assertions.assertDoesNotThrow(()-> protocolService.createAndSave( dto, slrID));
    }

    @Test
    public void createAndSave_NullSLR(){
        Assertions.assertThrows(SLRNotFoundException.class, ()->protocolService.createAndSave(new ProtocolDTO(), null));
    }

    @Test
    public void createAndSave_NullDTO(){
        when(slrRepository.findSLRById(2L)).thenReturn(Optional.of(new SLR()));
        Assertions.assertThrows(NullEntityException.class, ()->protocolService.createAndSave(null, 2L));
    }

    @Test
    public void update_OK() {
        Protocol protocol = Protocol.builder().id(2L).build();
        when(protocolRepository.existsById(2L)).thenReturn(true);

        when(protocolRepository.save(protocol)).thenReturn(protocol);
        Assertions.assertDoesNotThrow(()-> protocolService.update(protocol));
    }

    @Test
    public void update_KO() {
        Assertions.assertThrows(ProtocolNotFoundException.class, ()-> protocolService.update(new Protocol()));
    }

    @Test
    public void delete_OK() {
        Long protocolId = 1L;
        Protocol protocol = new Protocol();
        protocol.setSlr(new SLR());

        when(protocolRepository.findProtocolById(protocolId)).thenReturn(Optional.of(protocol));
        protocolService.delete(protocolId);

        Assertions.assertDoesNotThrow(()-> protocolService.delete(protocolId));
    }

    @Test
    public void delete_KO(){
        when(protocolRepository.findProtocolById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ProtocolNotFoundException.class, ()-> protocolService.delete(2L));
    }

    @Test
    public void findSelectionCriteriaFromSearch_OK(){
        SelectionCriteria criteria1 = SelectionCriteria.builder().id(1L).build();
        SelectionCriteria criteria2 = SelectionCriteria.builder().id(2L).build();
        Set<SelectionCriteria> selectionCriteria = new HashSet<>();
        selectionCriteria.add(criteria1);
        selectionCriteria.add(criteria2);

        Protocol protocol = Protocol.builder().selectionCriteria(selectionCriteria).build();

        when(protocolRepository.findProtocolById(anyLong())).thenReturn(Optional.of(protocol));

        ArrayList<SelectionCriteria> list = Lists.newArrayList(selectionCriteria);
        List<SelectionCriteria> result = protocolService.findSelectionCriteriaFromSearch(1L);
        Assertions.assertEquals(list, result);
    }

    @Test
    public void findSelectionCriteriaFromSearch_KO(){
        when(protocolRepository.findProtocolById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ProtocolNotFoundException.class, ()-> protocolService.findSelectionCriteriaFromSearch(1L));
    }


}
