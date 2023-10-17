package unitTests;

import com.google.common.collect.Lists;
import com.tfg.review.dtos.FullSLRDTO;
import com.tfg.review.dtos.SLRDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.SLRNotFoundException;
import com.tfg.review.models.Protocol;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.Rol;
import com.tfg.review.models.SLR;
import com.tfg.review.repositories.SLRRepository;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.services.ResearcherService;
import com.tfg.review.services.implementations.SLRServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestExecutionListeners;

import java.util.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SLRServiceTest {

    @InjectMocks
    private SLRServiceImpl SLRService;

    @Mock
    private SLRRepository slrRepository;

    @Mock
    private ProtocolService protocolService;

    @Mock
    private ResearcherService researcherService;

    @Test
    public void findOne_OK(){
        when(slrRepository.findSLRById(Mockito.anyLong())).thenReturn(Optional.of(new SLR()));

        SLR result = SLRService.findOne(3L);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findOne_notFound(){
        when(slrRepository.findSLRById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(SLRNotFoundException.class, () -> SLRService.findOne(0L));
    }

    @Test
    public void findAll_OK(){
        List<SLR> slrs = Arrays.asList(new SLR(), new SLR());

        when(slrRepository.findAll()).thenReturn(slrs);

        List<SLR> result = SLRService.findAll();
        Assertions.assertEquals(slrs, result);
    }

    @Test
    public void createAndSave_ValidData_ReturnsOK(){
        SLRDTO dto = new SLRDTO();
        Long principalResearcherId = 1L;
        Researcher principal = new Researcher();

        when(researcherService.findOne(principalResearcherId)).thenReturn(principal);

        SLRDTO result = SLRService.createAndSave(dto, principalResearcherId);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getPublicVisibility());
        Assertions.assertEquals(1, result.getResearchers().size());
    }

    @Test
    public void createAndSave_NullsLR() {
        Assertions.assertThrows(NullEntityException.class, ()-> SLRService.createAndSave(null, 2L));
    }
    @Test
    public void createAndSave_NullID() {
        Assertions.assertThrows(IllegalArgumentException.class, ()-> SLRService.createAndSave(new SLRDTO(), null));
    }

    @Test
    public void update_OK() {
        SLR slr = SLR.builder().id(23L).title("Some title").description("Some description").build();

        when(slrRepository.existsById(slr.getId())).thenReturn(true);
        when(slrRepository.save(slr)).thenReturn(slr);

        FullSLRDTO result = SLRService.update(slr);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), slr.getId());
    }

    @Test
    public void update_SLRNull(){
        Assertions.assertThrows(SLRNotFoundException.class, ()-> SLRService.update(null));
    }

    @Test
    public void update_IDNotFound(){
        SLR slr = SLR.builder().id(23L).title("Some title").description("Some description").build();

        when(slrRepository.existsById(slr.getId())).thenReturn(false);
        Assertions.assertThrows(SLRNotFoundException.class, ()->SLRService.update(slr));
    }

    @Test
    public void delete_OK(){
        Long deleteID = 2L;
        SLR slr = SLR.builder().id(deleteID).build();
        Protocol protocol = Protocol.builder().id(3L).build();
        slr.setProtocol(protocol);

        when(slrRepository.findSLRById(deleteID)).thenReturn(Optional.of(slr));
        doNothing().when(protocolService).delete(protocol.getId());
        Assertions.assertDoesNotThrow(()-> SLRService.delete(deleteID));
    }

    @Test
    public void delete_NullID(){
        Assertions.assertThrows(NullEntityException.class, ()-> SLRService.delete(null));
    }

    @Test
    public void delete_IdNotFound(){
        Long deleteID = 2L;

        when(slrRepository.findSLRById(deleteID)).thenReturn(Optional.empty());
        Assertions.assertThrows(NullEntityException.class, ()-> SLRService.delete(deleteID));
    }

    @Test
    public void findByResearcherUserId_OK(){
        Long userId = 23L;

        Researcher researcher1 = Researcher.builder().userId(23L).name("Researcher 1").SLRs(new HashSet<>()).rol(Rol.PRINCIPAL).build();
        Researcher researcher2 = Researcher.builder().userId(23L).name("Researcher 1").SLRs(new HashSet<>()).rol(Rol.COLLABORATOR).build();

        SLR slr1 = new SLR();
        SLR slr2 = new SLR();
        SLR slr3 = new SLR();

        researcher1.addSLR(slr1);
        researcher1.addSLR(slr2);
        researcher2.addSLR(slr3);

        List<Researcher> researchers = Lists.newArrayList(researcher1,researcher2);
        when(researcherService.findByUserId(userId)).thenReturn(researchers);

        List<SLR> allSLR = SLRService.findByResearcherUserId(userId);
        Assertions.assertEquals(3, allSLR.size());
    }

    @Test
    public void findByResearcherUserId_NullID(){
        Assertions.assertThrows(IllegalArgumentException.class, ()-> SLRService.findByResearcherUserId(null));
    }

    @Test
    public void findByResearcherUserId_EmptyList(){
        Long userId = 11L;

        when(researcherService.findByUserId(userId)).thenReturn(Collections.emptyList());
        List<SLR> expectedList = SLRService.findByResearcherUserId(userId);

        Assertions.assertNotNull(expectedList);
        Assertions.assertTrue(expectedList.isEmpty());
    }


}
