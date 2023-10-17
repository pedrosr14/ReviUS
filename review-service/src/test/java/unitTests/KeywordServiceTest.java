package unitTests;

import com.tfg.review.dtos.KeywordDTO;
import com.tfg.review.exceptions.KeywordNotFoundException;
import com.tfg.review.exceptions.ProtocolNotFoundException;
import com.tfg.review.models.Keyword;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.KeywordRepository;
import com.tfg.review.services.implementations.KeywordServiceImpl;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.color.ProfileDataException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeywordServiceTest {

    @InjectMocks
    private KeywordServiceImpl keywordService;

    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private ProtocolServiceImpl protocolService;

    @Test
    public void findAll_OK(){
        Keyword keyword1 = Keyword.builder().keyword("test1").build();
        Keyword keyword2 = Keyword.builder().keyword("test2").build();
        List<Keyword> keywordList = Arrays.asList(keyword1, keyword2);

        when(keywordRepository.findAll()).thenReturn(keywordList);
        List<Keyword> result = keywordService.findAll();

        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void findOne_OK(){
        Long keywordId = 13L;
        Keyword keyword = Keyword.builder().keyword("test").build();

        when(keywordRepository.existsById(keywordId)).thenReturn(true);
        when(keywordRepository.findKeywordById(keywordId)).thenReturn(Optional.of(keyword));

        Optional<Keyword> result = keywordService.findOne(keywordId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findOne_KO(){
        Long keywordId = 13L;
        when(keywordRepository.existsById(keywordId)).thenReturn(false);

        assertThrows(KeywordNotFoundException.class, ()-> keywordService.findOne(keywordId));
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 1L;
        KeywordDTO keywordDTO = KeywordDTO.builder().keyword("test").build();
        Protocol protocol = new Protocol();
        Keyword keyword = Keyword.builder().keyword("test").build();
        keyword.setProtocols(new HashSet<>());

        when(protocolService.findOne(protocolId)).thenReturn(protocol);
        when(keywordRepository.save(any(Keyword.class))).thenReturn(keyword);

        Keyword result = keywordService.createAndSave(keywordDTO, protocolId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(keyword, result);
    }

    @Test
    public void createAndSave_NullKeyword(){
        Long protocolId = 1L;

        assertThrows(IllegalArgumentException.class, () -> keywordService.createAndSave(null, protocolId));
    }

    @Test
    public void createAndSave_ProtocolNotFound(){
        Long protocolId = 5L;
        KeywordDTO keywordDTO = new KeywordDTO();

        doThrow(ProtocolNotFoundException.class).when(protocolService).findOne(protocolId);

        assertThrows(ProtocolNotFoundException.class, () -> keywordService.createAndSave(keywordDTO, protocolId));
    }

    @Test
    public void update_OK(){
        Long keywordId = 1L;
        Keyword keyword = new Keyword();
        keyword.setId(keywordId);

        when(keywordRepository.existsById(keywordId)).thenReturn(true);
        when(keywordRepository.save(any(Keyword.class))).thenReturn(keyword);

        Keyword result = keywordService.update(keyword);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(keyword, result);
    }

    @Test
    public void update_KO(){
        Long keywordId = 1L;
        Keyword keyword = new Keyword();
        keyword.setId(keywordId);

        when(keywordRepository.existsById(keywordId)).thenReturn(false);

        assertThrows(KeywordNotFoundException.class, () -> keywordService.update(keyword));
    }

    @Test
    public void deleteFromProtocol_OK(){
        Long protocolId = 1L;
        Long keywordId = 2L;
        Keyword keyword = new Keyword();
        keyword.setId(keywordId);
        Protocol protocol = new Protocol();

        when(keywordRepository.existsById(keywordId)).thenReturn(true);
        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        keywordService.deleteFromProtocol(keyword, protocolId);

        Assertions.assertFalse(protocol.getKeywords().contains(keyword));
    }

    @Test
    public void deleteFromProtocol_KeywordNotFound(){
        Long protocolId = 1L;
        Long keywordId = 2L;
        Keyword keyword = new Keyword();
        keyword.setId(keywordId);

        when(keywordRepository.existsById(keywordId)).thenReturn(false);

        assertThrows(KeywordNotFoundException.class, () -> keywordService.deleteFromProtocol(keyword, protocolId));
    }

    @Test
    public void deleteFromProtocol_NullKeyword(){
        Long protocolId = 1L;

        assertThrows(KeywordNotFoundException.class, () -> keywordService.deleteFromProtocol(null, protocolId));
    }

    @Test
    public void delete_OK(){
        Long keywordId = 1L;
        Keyword keyword = new Keyword();
        keyword.setId(keywordId);

        when(keywordRepository.findKeywordById(keywordId)).thenReturn(Optional.of(keyword));

        Assertions.assertDoesNotThrow(()-> keywordService.delete(keywordId));
    }

    @Test
    public void delete_NullKeyword(){
        Long keywordId = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> keywordService.delete(keywordId));
    }

    @Test
    public void delete_keywordNotFound(){
        Long keywordId = 1L;

        when(keywordRepository.findKeywordById(keywordId)).thenReturn(Optional.empty());

        Assertions.assertThrows(KeywordNotFoundException.class, () -> keywordService.delete(keywordId));
    }
}
