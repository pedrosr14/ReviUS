package unitTests;

import com.tfg.review.dtos.FormFieldDTO;
import com.tfg.review.exceptions.FormFieldNotFoundException;
import com.tfg.review.exceptions.FormNotFoundException;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ProtocolNotFoundException;
import com.tfg.review.models.Form;
import com.tfg.review.models.FormField;
import com.tfg.review.models.FormType;
import com.tfg.review.models.Protocol;
import com.tfg.review.repositories.FormRepository;
import com.tfg.review.services.ProtocolService;
import com.tfg.review.services.implementations.FormServiceImpl;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FormServiceTest {

    @InjectMocks
    private FormServiceImpl formService;

    @Mock
    private FormRepository formRepository;
    @Mock
    private ProtocolServiceImpl protocolService;

    @Test
    public void findAll_OK(){
        List<Form> forms = Arrays.asList(new Form(), new Form());

        when(formRepository.findAll()).thenReturn(forms);
        List<Form> result = formService.findAll();

        Assertions.assertEquals(result.size(), 2);
    }

    @Test
    public void findOne_OK(){
        Long formId = 23L;
        Form form = new Form();

        when(formRepository.findFormById(formId)).thenReturn(Optional.of(form));
        Optional<Form> optionalForm = formService.findOne(formId);

        Assertions.assertTrue(optionalForm.isPresent());
    }

    @Test
    public void findOne_KO(){
        Assertions.assertThrows(FormNotFoundException.class, ()->formService.findOne(null));
    }

    @Test
    public void findFormWithFields_OK(){
        Long protocolId = 1L;
        FormType formType = FormType.QUALITY;
        Protocol protocol = Protocol.builder().id(protocolId).build();
        Form qualityForm = Form.builder().formType(FormType.QUALITY).build();
        protocol.setQualityForm(qualityForm);

        FormFieldDTO formFieldDTO = new FormFieldDTO();

        Set<FormField> formFields = new HashSet<>();
        FormField formField = FormField.builder().form(qualityForm).build();
        formFields.add(formField);
        qualityForm.setFormFields(List.copyOf(formFields));
        List<FormFieldDTO> expectedFormFields = List.of(formFieldDTO);

        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        List<FormFieldDTO> result = formService.findFormWithFields(protocolId, formType);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedFormFields.size(), result.size());
    }

    @Test
    public void findFormWithFields_ProtocolNotFound(){
        Long protocolId = 1L;
        FormType formType = FormType.QUALITY;

        doThrow(ProtocolNotFoundException.class).when(protocolService).findOne(protocolId);

        Assertions.assertThrows(ProtocolNotFoundException.class, () -> formService.findFormWithFields(protocolId, formType));
    }

    @Test
    public void findFormWithFields_formTypeNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> formService.findFormWithFields(2L, null));
    }

    @Test
    public void findFormWithFields_FormNotFound(){
        Long protocolId = 1L;
        FormType formType = FormType.EXTRACTION;
        Protocol protocol = Protocol.builder().id(protocolId).build();
        Form qualityForm = Form.builder().formType(FormType.QUALITY).build();
        protocol.setQualityForm(qualityForm);

        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        Assertions.assertThrows(FormNotFoundException.class, () -> formService.findFormWithFields(protocolId, formType));
    }

    @Test
    public void findFormWithFields_FieldsNotFound(){
        Long protocolId = 1L;
        FormType formType = FormType.QUALITY;
        Protocol protocol = Protocol.builder().id(protocolId).build();
        Form qualityForm = Form.builder().formType(FormType.QUALITY).build();
        protocol.setQualityForm(qualityForm);

        Set<FormField> formFields = new HashSet<>();
        qualityForm.setFormFields(List.copyOf(formFields));

        when(protocolService.findOne(protocolId)).thenReturn(protocol);

        Assertions.assertThrows(FormFieldNotFoundException.class, () -> formService.findFormWithFields(protocolId, formType));
    }

    @Test
    public void createAndSave_OK(){
        Long protocolId = 1L;
        FormType formType = FormType.EXTRACTION;
        Protocol protocol = new Protocol();
        Form form = new Form();
        form.setFormType(formType);

        when(protocolService.findOne(protocolId)).thenReturn(protocol);
        when(formRepository.save(any(Form.class))).thenReturn(form);

        Form result = formService.createAndSave(form, protocolId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(form, result);
    }

    @Test
    public void createAndSave_KO(){
        Long protocolId = 1L;

        Assertions.assertThrows(NullEntityException.class, () -> formService.createAndSave(null, protocolId));
    }

    @Test
    public void update_OK(){
        Form form = new Form();

        when(formRepository.save(any(Form.class))).thenReturn(form);

        Form result = formService.update(form);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(form, result);
    }

    @Test
    public void update_KO(){
        Assertions.assertThrows(NullEntityException.class, () -> formService.update(null));
    }

    @Test
    public void delete_OK(){

        Form qualityForm = Form.builder().formType(FormType.QUALITY).build();

        Protocol relatedProtocol = Protocol.builder().build();
        qualityForm.setProtocolToQuality(relatedProtocol);
        relatedProtocol.setQualityForm(qualityForm);

        formService.delete(qualityForm);
        when(protocolService.update(any(Protocol.class))).thenReturn(null);

        Assertions.assertNull(relatedProtocol.getQualityForm());
        Assertions.assertDoesNotThrow(()-> formService.delete(qualityForm));
    }

}
