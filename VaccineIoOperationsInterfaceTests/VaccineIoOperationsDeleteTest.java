import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
public class VaccineIoOperationsDeleteTest {

    @Mock
    private VaccineIoOperationRepository repository;

    @InjectMocks
    private VaccineIoOperations service;

    private Vaccine validVaccine;
    private Vaccine invalidVaccine;

    @BeforeEach
    public void setup() {
        validVaccine = new Vaccine("VALID_CODE", "Valid Description", new VaccineType("VALID_TYPE"));
        invalidVaccine = new Vaccine(null, "Invalid Description", new VaccineType("INVALID_TYPE"));
    }

    @Test
    public void testDeleteVaccine_ValidCode() throws Exception {
        doNothing().when(repository).delete(validVaccine);

        boolean result = service.deleteVaccine(validVaccine);

        assertTrue(result);
        verify(repository, times(1)).delete(validVaccine);
    }

    @Test
    public void testDeleteVaccine_NullCode() throws Exception {
        Vaccine vaccineWithNullCode = new Vaccine(null, "Description", new VaccineType("TYPE"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.deleteVaccine(vaccineWithNullCode);
        });

        assertEquals("code must not be null", exception.getMessage());
        verify(repository, never()).delete(vaccineWithNullCode);
    }

    @Test
    public void testDeleteVaccine_EmptyCode() throws Exception {
        Vaccine vaccineWithEmptyCode = new Vaccine("", "Description", new VaccineType("TYPE"));
        doThrow(new EmptyResultDataAccessException(1)).when(repository).delete(vaccineWithEmptyCode);

        boolean result = service.deleteVaccine(vaccineWithEmptyCode);

        assertFalse(result);
        verify(repository, times(1)).delete(vaccineWithEmptyCode);
    }

    @Test
    public void testDeleteVaccine_InvalidCode() throws Exception {
        Vaccine vaccineWithInvalidCode = new Vaccine("INVALID_CODE", "Description", new VaccineType("TYPE"));
        doThrow(new EmptyResultDataAccessException(1)).when(repository).delete(vaccineWithInvalidCode);

        boolean result = service.deleteVaccine(vaccineWithInvalidCode);

        assertFalse(result);
        verify(repository, times(1)).delete(vaccineWithInvalidCode);
    }
}
