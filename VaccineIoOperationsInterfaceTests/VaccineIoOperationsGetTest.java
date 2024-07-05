import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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

@ExtendWith(MockitoExtension.class)
public class VaccineIoOperationsTest {

    @Mock
    private VaccineIoOperationRepository repository;

    @InjectMocks
    private VaccineIoOperations service;

    private VaccineType validVaccineType;
    private VaccineType invalidVaccineType;
    private Vaccine validVaccine;
    private Vaccine invalidVaccine1;
    private Vaccine invalidVaccine2;
    private Vaccine invalidVaccine3;

    @BeforeEach
    public void setup() {
        validVaccineType = new VaccineType();
        validVaccineType.setCode("VALID_TYPE");

        invalidVaccineType = new VaccineType();
        invalidVaccineType.setCode("INVALID_TYPE");

        validVaccine = new Vaccine("VALID_CODE", "Valid Description", validVaccineType);
        invalidVaccine1 = new Vaccine("INVALID_CODE_1", null, validVaccineType);  // Null description
        invalidVaccine2 = new Vaccine("INVALID_CODE_2", "Invalid Description", null);  // Null vaccineType
        invalidVaccine3 = new Vaccine("INVALID_CODE_3", "Invalid Description", invalidVaccineType);  // Invalid vaccineType
    }

    @Test
    public void testGetVaccine_Valid() throws Exception {
        when(repository.findAllByOrderByDescriptionAsc()).thenReturn(List.of(validVaccine));

        List<Vaccine> vaccines = service.getVaccine(null);

        assertNotNull(vaccines);
        assertFalse(vaccines.isEmpty());
        assertEquals(1, vaccines.size());
        assertEquals("Valid Description", vaccines.get(0).getDescription());
    }

    @Test
    public void testGetVaccine_InvalidDescription() throws Exception {
        when(repository.findAllByOrderByDescriptionAsc()).thenReturn(List.of(invalidVaccine1));

        List<Vaccine> vaccines = service.getVaccine(null);

        assertNotNull(vaccines);
        assertFalse(vaccines.isEmpty());
        assertEquals(1, vaccines.size());
        assertNull(vaccines.get(0).getDescription());
    }

    @Test
    public void testGetVaccine_NullVaccineType() throws Exception {
        when(repository.findAllByOrderByDescriptionAsc()).thenReturn(List.of(invalidVaccine2));

        List<Vaccine> vaccines = service.getVaccine(null);

        assertNotNull(vaccines);
        assertFalse(vaccines.isEmpty());
        assertEquals(1, vaccines.size());
        assertNull(vaccines.get(0).getVaccineType());
    }

    @Test
    public void testGetVaccine_InvalidVaccineType() throws Exception {
        when(repository.findAllByOrderByDescriptionAsc()).thenReturn(List.of(invalidVaccine3));

        List<Vaccine> vaccines = service.getVaccine(null);

        assertNotNull(vaccines);
        assertFalse(vaccines.isEmpty());
        assertEquals(1, vaccines.size());
        assertEquals("INVALID_TYPE", vaccines.get(0).getVaccineType().getCode());
    }
}