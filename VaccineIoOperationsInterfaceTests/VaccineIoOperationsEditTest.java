import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.model.VaccineType;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class VaccineIoOperationsEditTest {

    @Mock
    private VaccineIoOperationRepository repository;

    @InjectMocks
    private VaccineIoOperations service;

    private Vaccine validVaccine;
    private Vaccine invalidVaccine;

    @BeforeEach
    public void setup() {
        validVaccine = new Vaccine("VALID_CODE", "Valid Description", new VaccineType("VALID_TYPE"));
        invalidVaccine = new Vaccine(null, "", new VaccineType("INVALID_TYPE"));
    }

    @Test
    public void testEditVaccine_ValidVaccine() throws OHServiceException {
        when(repository.save(validVaccine)).thenReturn(validVaccine);

        Vaccine result = service.updateVaccine(validVaccine);

        assertNotNull(result);
        assertEquals("VALID_CODE", result.getCode());
        assertEquals("Valid Description", result.getDescription());
        assertEquals("VALID_TYPE", result.getVaccineType().getCode());

        verify(repository, times(1)).save(validVaccine);
    }

    @Test
    public void testEditVaccine_InvalidCode_Null() {
        Vaccine vaccineWithNullCode = new Vaccine(null, "Description", new VaccineType("TYPE"));

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.updateVaccine(vaccineWithNullCode);
        });

        assertFalse(exception.getMessages().isEmpty());
        verify(repository, never()).save(vaccineWithNullCode);
    }

    @Test
    public void testEditVaccine_InvalidCode_Empty() {
        Vaccine vaccineWithEmptyCode = new Vaccine("", "Description", new VaccineType("TYPE"));

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.updateVaccine(vaccineWithEmptyCode);
        });

        assertFalse(exception.getMessages().isEmpty());
        verify(repository, never()).save(vaccineWithEmptyCode);
    }

    @Test
    public void testEditVaccine_InvalidDescription_Empty() {
        Vaccine vaccineWithEmptyDescription = new Vaccine("VALID_CODE", "", new VaccineType("VALID_TYPE"));

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.updateVaccine(vaccineWithEmptyDescription);
        });

        assertFalse(exception.getMessages().isEmpty());
        verify(repository, never()).save(vaccineWithEmptyDescription);
    }

    @Test
    public void testEditVaccine_InvalidVaccineType() {
        Vaccine vaccineWithInvalidType = new Vaccine("VALID_CODE", "Description", null);

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.updateVaccine(vaccineWithInvalidType);
        });

        assertFalse(exception.getMessages().isEmpty());
        verify(repository, never()).save(vaccineWithInvalidType);
    }

    @Test
    public void testEditVaccine_ValidVaccine_CodeAlreadyExists() throws OHServiceException {
        Vaccine existingVaccine = new Vaccine("VALID_CODE", "Existing Description", new VaccineType("VALID_TYPE"));
        when(repository.exists(validVaccine.getCode())).thenReturn(true);

        OHDataIntegrityViolationException exception = assertThrows(OHDataIntegrityViolationException.class, () -> {
            service.updateVaccine(existingVaccine);
        });

        assertFalse(exception.getMessages().isEmpty());
        verify(repository, times(1)).exists(existingVaccine.getCode());
        verify(repository, never()).save(existingVaccine);
    }
}
