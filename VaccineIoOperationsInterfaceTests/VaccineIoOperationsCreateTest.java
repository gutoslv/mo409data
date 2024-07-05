import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.vaccine.model.VaccineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class VaccineIoOperationsCreateTest {

    @Mock
    private VaccineIoOperationRepository repository;

    @InjectMocks
    private VaccineIoOperations service;

    private Vaccine validVaccine;
    private Vaccine invalidVaccineDescriptionNull;
    private Vaccine invalidVaccineTypeInvalid;

    @BeforeEach
    public void setup() {
        validVaccine = new Vaccine("VALID_CODE", "Valid Description", new VaccineType("VALID_TYPE"));
        invalidVaccineDescriptionNull = new Vaccine("VALID_CODE", null, new VaccineType("VALID_TYPE"));
        invalidVaccineTypeInvalid = new Vaccine("VALID_CODE", "Valid Description", null);
    }

    @Test
    public void testNewVaccine_ValidVaccine() throws Exception {
        when(repository.save(validVaccine)).thenReturn(validVaccine);

        Vaccine result = service.newVaccine(validVaccine);

        assertNotNull(result);
        assertEquals("VALID_CODE", result.getCode());
        verify(repository, times(1)).save(validVaccine);
    }

    @Test
    public void testNewVaccine_NullDescription() throws Exception {
        List<OHExceptionMessage> errors = new ArrayList<>();
        errors.add(new OHExceptionMessage("error", "Please insert a valid description", OHSeverityLevel.ERROR));
        doThrow(new OHDataValidationException(errors)).when(service).validateVaccine(invalidVaccineDescriptionNull, true);

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.newVaccine(invalidVaccineDescriptionNull);
        });

        assertEquals("Please insert a valid description", exception.getValidationMessages().get(0).getMessage());
        verify(repository, never()).save(invalidVaccineDescriptionNull);
    }

    @Test
    public void testNewVaccine_InvalidVaccineType() throws Exception {
        List<OHExceptionMessage> errors = new ArrayList<>();
        errors.add(new OHExceptionMessage("error", "Vaccine type is invalid", OHSeverityLevel.ERROR));
        doThrow(new OHDataValidationException(errors)).when(service).validateVaccine(invalidVaccineTypeInvalid, true);

        OHDataValidationException exception = assertThrows(OHDataValidationException.class, () -> {
            service.newVaccine(invalidVaccineTypeInvalid);
        });

        assertEquals("Vaccine type is invalid", exception.getValidationMessages().get(0).getMessage());
        verify(repository, never()).save(invalidVaccineTypeInvalid);
    }

    @Test
    public void testNewVaccine_CodeAlreadyInUse() throws Exception {
        when(service.isCodePresent("VALID_CODE")).thenReturn(true);

        OHDataIntegrityViolationException exception = assertThrows(OHDataIntegrityViolationException.class, () -> {
            service.newVaccine(validVaccine);
        });

        assertEquals("The code is already in use", exception.getMessage());
        verify(repository, never()).save(validVaccine);
    }
}
