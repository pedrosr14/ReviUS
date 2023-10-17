package unitTests;

import com.tfg.review.dtos.UserDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ResearcherNotFoundException;
import com.tfg.review.models.Researcher;
import com.tfg.review.repositories.ResearcherRepository;
import com.tfg.review.services.implementations.ResearcherServiceImpl;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResearcherServiceTest {

    @InjectMocks
    private ResearcherServiceImpl researcherService;
    @Mock
    private ResearcherRepository researcherRepository;

    @Test
    public void findAll_OK(){
        List<Researcher> researcherList = Arrays.asList(new Researcher(), new Researcher());
        when(researcherRepository.findAll()).thenReturn(researcherList);
        List<Researcher> result = researcherService.findAll();

        Assertions.assertEquals(result, researcherList);
    }

    @Test
    public void findOne_OK(){
        when(researcherRepository.findResearcherById(anyLong())).thenReturn(Optional.of(new Researcher()));
        Assertions.assertDoesNotThrow(()->researcherService.findOne(1L));
    }

    @Test
    public void findOne_KO(){
        when(researcherRepository.findResearcherById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResearcherNotFoundException.class, ()->researcherService.findOne(1L));
    }

    @Test
    public void findByUserId_OK() {
        List<Researcher> researcherList = Arrays.asList(new Researcher(), new Researcher());
        when(researcherRepository.findAllByUserId(1L)).thenReturn(researcherList);

        List<Researcher> result = researcherService.findByUserId(1L);
        Assertions.assertEquals(result, researcherList);
    }

    @Test
    public void findByUserId_KO() {
        Assertions.assertThrows(NullEntityException.class, ()->researcherService.findByUserId(null));
    }

    @Test
    public void createFromUser_OK() {
        Researcher researcher = Researcher.builder().build();
        when(researcherRepository.save(any(Researcher.class))).thenReturn(researcher);
        Researcher result = researcherService.createFromUser(new UserDTO(), 1L);

        Assertions.assertEquals(result, researcher);
    }

    @Test
    public void createFromUser_NullDTO() {
        Assertions.assertThrows(NullEntityException.class, ()->researcherService.createFromUser(null, 1L));
    }

    @Test
    public void createFromUser_NullID() {
        Assertions.assertThrows(NullEntityException.class, ()->researcherService.createFromUser(new UserDTO(), null));
    }

    @Test
    public void update_OK(){
        Researcher researcher = Researcher.builder().build();
        when(researcherRepository.findResearcherById(anyLong())).thenReturn(Optional.of(researcher));

        Assertions.assertDoesNotThrow(()-> researcherService.update(2L));
    }

    @Test
    public void update_KO(){
        when(researcherRepository.findResearcherById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResearcherNotFoundException.class, ()-> researcherService.update(2L));
    }

}
