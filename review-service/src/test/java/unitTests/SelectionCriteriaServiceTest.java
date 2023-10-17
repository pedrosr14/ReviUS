package unitTests;

import com.tfg.review.dtos.SelectionCriteriaDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ProtocolNotFoundException;
import com.tfg.review.exceptions.SelectionCriteriaNotFoundException;
import com.tfg.review.models.CriteriaType;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.SelectionCriteria;
import com.tfg.review.repositories.SelectionCriteriaRepository;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import com.tfg.review.services.implementations.SelectionCriteriaServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SelectionCriteriaServiceTest {

    @InjectMocks
    private SelectionCriteriaServiceImpl selectionCriteriaService;

    @Mock
    private SelectionCriteriaRepository selectionCriteriaRepository;
    @Mock
    private ProtocolServiceImpl protocolService;

    @Test
    public void findAll_OK() {
        List<SelectionCriteria> criteriaList = Arrays.asList(new SelectionCriteria(), new SelectionCriteria(), new SelectionCriteria());
        when(selectionCriteriaRepository.findAll()).thenReturn(criteriaList);

        List<SelectionCriteria> result = selectionCriteriaService.findAll();

        Assertions.assertEquals(criteriaList, result);
    }

    @Test
    public void findOne_OK() {
        Long selectionCriteriaId = 1L;
        SelectionCriteria selectionCriteria = new SelectionCriteria();
        when(selectionCriteriaRepository.existsById(selectionCriteriaId)).thenReturn(true);
        when(selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId)).thenReturn(Optional.of(selectionCriteria));

        Optional<SelectionCriteria> result = selectionCriteriaService.findOne(selectionCriteriaId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(selectionCriteria, result.get());
    }

    @Test
    public void findOne_ExistFalse() {
        Long selectionCriteriaId = 1L;
        when(selectionCriteriaRepository.existsById(selectionCriteriaId)).thenReturn(false);

        Assertions.assertThrows(SelectionCriteriaNotFoundException.class, () ->selectionCriteriaService.findOne(selectionCriteriaId));
    }

    @Test
    public void findOne_NullID() {
        Assertions.assertThrows(SelectionCriteriaNotFoundException.class, () ->selectionCriteriaService.findOne(null));
    }

    @Test
    public void getAppliedCriteria() {
        List<Long> appliedCriteriaIds = Arrays.asList(1L, 2L);

        List<SelectionCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(SelectionCriteria.builder().id(1L).build());
        criteriaList.add(SelectionCriteria.builder().id(2L).build());

        when(selectionCriteriaRepository.findSelectionCriteriaByIdIn(appliedCriteriaIds)).thenReturn(criteriaList);

        List<SelectionCriteria> result = selectionCriteriaService.getAppliedCriteria(appliedCriteriaIds);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(criteriaList.size(), result.size());
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 1L;
        SelectionCriteriaDTO criteriaDTO = new SelectionCriteriaDTO();
        criteriaDTO.setCriteriaType("INCLUSION");
        Protocol protocol = new Protocol();
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        SelectionCriteria result = new SelectionCriteria();
        result.setCriteriaType(CriteriaType.INCLUSION);
        when(selectionCriteriaRepository.save(any(SelectionCriteria.class))).thenReturn(result);

        SelectionCriteriaDTO savedDTO = selectionCriteriaService.createAndSave(criteriaDTO, protocolId);

        Assertions.assertNotNull(savedDTO);
        Assertions.assertDoesNotThrow(()-> selectionCriteriaService.createAndSave(criteriaDTO, protocolId));
    }

    @Test
    public void createAndSave_NullDTO(){
        Assertions.assertThrows(NullEntityException.class, ()-> selectionCriteriaService.createAndSave(null, 1L));
    }

    @Test
    public void createAndSave_ProtocolNotFound(){
        doThrow(ProtocolNotFoundException.class).when(protocolService).findOne(1L);
        Assertions.assertThrows(ProtocolNotFoundException.class, ()-> selectionCriteriaService.createAndSave(new SelectionCriteriaDTO(), 1L));
    }

    @Test
    public void addCriteria_OK(){
        SelectionCriteriaDTO criteriaDTO = SelectionCriteriaDTO.builder().id(1L).criteriaType("EXCLUSION").build();
        Protocol protocol = new Protocol();
        SelectionCriteria existingCriteria = SelectionCriteria.builder().criteriaType(CriteriaType.EXCLUSION).build();
        existingCriteria.setProtocols(new HashSet<>());
        Set<SelectionCriteria> oldCriteria = new HashSet<>();
        oldCriteria.add(existingCriteria);
        protocol.setSelectionCriteria(oldCriteria);

        when(protocolService.findOne(2L)).thenReturn(protocol);
        when(selectionCriteriaRepository.findSelectionCriteriaById(criteriaDTO.getId())).thenReturn(Optional.of(existingCriteria));

        SelectionCriteria result = SelectionCriteria.builder().criteriaType(CriteriaType.EXCLUSION).build();
        result.setProtocols(new HashSet<>());
        when(selectionCriteriaRepository.save(any(SelectionCriteria.class))).thenReturn(result);

        SelectionCriteriaDTO savedDTO = selectionCriteriaService.addCriteria(criteriaDTO, 2L);

        Assertions.assertNotNull(savedDTO);
        Assertions.assertDoesNotThrow(()->selectionCriteriaService.addCriteria(criteriaDTO,2L));
    }

    @Test
    public void addCriteria_NullDTO(){
        Assertions.assertThrows(NullEntityException.class, ()-> selectionCriteriaService.addCriteria(null, 1L));
    }

    @Test
    public void addCriteria_ProtocolNotFound(){
        doThrow(ProtocolNotFoundException.class).when(protocolService).findOne(1L);
        Assertions.assertThrows(ProtocolNotFoundException.class, ()-> selectionCriteriaService.addCriteria(new SelectionCriteriaDTO(), 1L));
    }

    @Test
    public void update_OK() {
        SelectionCriteria criteria = new SelectionCriteria();
        criteria.setId(1L);
        when(selectionCriteriaRepository.existsById(criteria.getId())).thenReturn(true);
        when(selectionCriteriaRepository.save(criteria)).thenReturn(criteria);

        SelectionCriteria updatedCriteria = selectionCriteriaService.update(criteria);

        Assertions.assertNotNull(updatedCriteria);
    }

    @Test
    public void update_KO() {
        SelectionCriteria criteria = new SelectionCriteria();
        when(selectionCriteriaRepository.existsById(criteria.getId())).thenReturn(false);

        Assertions.assertThrows(SelectionCriteriaNotFoundException.class, () -> selectionCriteriaService.update(criteria));
    }

    @Test
    public void deleteFromProtocol_OK(){
        Long selectionCriteriaId = 1L;
        Long protocolId = 101L;

        SelectionCriteria criteria = SelectionCriteria.builder().build();
        criteria.setProtocols(new HashSet<>());
        Protocol protocol = Protocol.builder().build();
        protocol.setSelectionCriteria(new HashSet<>());

        when(selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId)).thenReturn(Optional.of(criteria));
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        Assertions.assertDoesNotThrow(()->selectionCriteriaService.deleteFromProtocol(selectionCriteriaId, protocolId));
    }

    @Test
    public void deleteFromProtocol_KO(){
        Long selectionCriteriaId = 1L;
        Long protocolId = null;

        Assertions.assertThrows(IllegalArgumentException.class, ()->selectionCriteriaService.deleteFromProtocol(selectionCriteriaId, protocolId));
    }

    @Test
    public void delete_OK() {
        Long selectionCriteriaId = 1L;
        SelectionCriteria criteria = new SelectionCriteria();
        when(selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId)).thenReturn(Optional.of(criteria));

        Protocol protocol1 = Protocol.builder().id(1L).build();
        Protocol protocol2 = Protocol.builder().id(2L).build();
        Set<Protocol> protocols = new HashSet<>();
        protocols.add(protocol1);
        protocols.add(protocol2);
        criteria.setProtocols(protocols);

        Set<SelectionCriteria> criteriaSet = new HashSet<>();
        criteriaSet.add(criteria);
        protocol1.setSelectionCriteria(criteriaSet);
        protocol2.setSelectionCriteria(criteriaSet);


        when(protocolService.update(any(Protocol.class))).thenReturn(null);

        selectionCriteriaService.delete(selectionCriteriaId);

        Assertions.assertFalse(protocol1.getSelectionCriteria().contains(criteria));
        Assertions.assertFalse(protocol2.getSelectionCriteria().contains(criteria));
    }

    @Test
    public void delete_NullId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> selectionCriteriaService.delete(null));
    }

    @Test
    public void delete_NotFound() {
        Long selectionCriteriaId = 1L;
        when(selectionCriteriaRepository.findSelectionCriteriaById(selectionCriteriaId)).thenReturn(Optional.empty());

        Assertions.assertThrows(SelectionCriteriaNotFoundException.class, () -> selectionCriteriaService.delete(selectionCriteriaId));
    }
}
