package at.ac.tuwien.sepr.groupphase.backend.unittests.treatment;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.TreatmentEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoSparse;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentDtoSearch;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.TreatmentPageDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.TreatmentService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PatientServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreatmentEndpointTest {

    @Mock
    private TreatmentService treatmentService;

    @Mock
    private PatientServiceImpl patientServiceImpl;

    @Mock
    private UserService userService;

    @InjectMocks
    private TreatmentEndpoint treatmentEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createTreatment: valid treatment dto - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldCreateTreatment_whenGivenValidTreatmentDto() {
        TreatmentDtoCreate treatmentDtoCreate = mock(TreatmentDtoCreate.class);
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentService.createTreatment(any(TreatmentDtoCreate.class))).thenReturn(treatmentDto);

        // call createTreatment() method in Endpoint
        TreatmentDto createdTreatmentDto = treatmentEndpoint.createTreatment(treatmentDtoCreate);

        assertNotNull(createdTreatmentDto);
        verify(treatmentService, times(1)).createTreatment(treatmentDtoCreate);
    }

    @Test
    @DisplayName("updateTreatment: valid treatment dto - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdateTreatment_whenGivenValidTreatmentDto() {
        Long id = 1L;
        TreatmentDtoCreate treatmentDtoCreate = mock(TreatmentDtoCreate.class);
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentService.updateTreatment(anyLong(), any(TreatmentDtoCreate.class))).thenReturn(treatmentDto);

        // call updateTreatment() method in Endpoint
        TreatmentDto updatedTreatmentDto = treatmentEndpoint.updateTreatment(id, treatmentDtoCreate);

        assertNotNull(updatedTreatmentDto);
        verify(treatmentService, times(1)).updateTreatment(id, treatmentDtoCreate);
    }

    @Test
    @DisplayName("getTreatmentById: valid id and role - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnTreatment_whenGivenValidIdAndRole() {
        Long id = 1L;
        TreatmentDto treatmentDto = mock(TreatmentDto.class);

        when(treatmentService.getTreatmentById(id)).thenReturn(treatmentDto);

        // mock isValidRequestOfRole() method in UserService simulate that the user is a doctor
        when(userService.isValidRequestOfRole(Role.DOCTOR)).thenReturn(true);

        // call getTreatmentById() method in Endpoint
        TreatmentDto foundTreatmentDto = treatmentEndpoint.getTreatmentById(id);

        assertNotNull(foundTreatmentDto);
        verify(treatmentService, times(1)).getTreatmentById(id);
    }

    @Test
    @DisplayName("getTreatmentById: valid id and own patient request - expect success")
    @WithMockUser(roles = "PATIENT")
    void shouldReturnTreatment_whenGivenValidIdAndOwnPatientRequest() {
        Long id = 1L;
        TreatmentDto treatmentDto = mock(TreatmentDto.class);
        Long patientId = 1L;

        when(treatmentService.getTreatmentById(id)).thenReturn(treatmentDto);
        when(treatmentDto.patient()).thenReturn(mock(PatientDtoSparse.class));
        when(treatmentDto.patient().id()).thenReturn(patientId);

        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is the owner of the request
        when(patientServiceImpl.isOwnRequest(patientId)).thenReturn(true);

        // call getTreatmentById() method in Endpoint
        TreatmentDto foundTreatmentDto = treatmentEndpoint.getTreatmentById(id);

        assertNotNull(foundTreatmentDto);
        verify(treatmentService, times(1)).getTreatmentById(id);
        verify(patientServiceImpl, times(1)).isOwnRequest(patientId);
    }

    @Test
    @DisplayName("getTreatmentById: valid id but unauthorized role - expect forbidden")
    @WithMockUser(roles = "PATIENT")
    void shouldThrowForbidden_whenGivenValidIdButUnauthorizedRole() {
        Long id = 1L;
        TreatmentDto treatmentDto = mock(TreatmentDto.class);
        Long patientId = 1L;

        when(treatmentService.getTreatmentById(id)).thenReturn(treatmentDto);
        when(treatmentDto.patient()).thenReturn(mock(PatientDtoSparse.class));
        when(treatmentDto.patient().id()).thenReturn(patientId);

        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is not the owner of the request
        when(patientServiceImpl.isOwnRequest(patientId)).thenReturn(false);

        // call getTreatmentById() method in Endpoint expect forbidden
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> treatmentEndpoint.getTreatmentById(id));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(treatmentService, times(1)).getTreatmentById(id);
        verify(patientServiceImpl, times(1)).isOwnRequest(patientId);
    }

    @Test
    @DisplayName("searchTreatments: valid searchParams and doctor request - expect success")
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnTreatments_whenGivenValidSearchParamsAndRoleDoctor() {
        TreatmentDtoSearch searchParams = mock(TreatmentDtoSearch.class);
        Long patientId = 1L;
        TreatmentPageDto treatmentPageDto = mock(TreatmentPageDto.class);

        when(treatmentService.searchTreatments(searchParams)).thenReturn(treatmentPageDto);

        when(searchParams.patientId()).thenReturn(patientId);
        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is not the owner of the request
        when(userService.isValidRequestOfRole(Role.DOCTOR)).thenReturn(true);

        // call getTreatmentById() method in Endpoint expect forbidden
        TreatmentPageDto treatments = treatmentEndpoint.searchTreatments(searchParams);
        assertNotNull(treatments);
        verify(treatmentService, times(1)).searchTreatments(searchParams);
        verify(userService, times(1)).isValidRequestOfRole(Role.DOCTOR);
    }

    @Test
    @DisplayName("searchTreatments: valid searchParams and secretary request - expect success")
    @WithMockUser(roles = "SECRETARY")
    void shouldReturnTreatments_whenGivenValidSearchParamsAndRoleSecretary() {
        TreatmentDtoSearch searchParams = mock(TreatmentDtoSearch.class);
        Long patientId = 1L;
        TreatmentPageDto treatmentPageDto = mock(TreatmentPageDto.class);

        when(treatmentService.searchTreatments(searchParams)).thenReturn(treatmentPageDto);

        when(searchParams.patientId()).thenReturn(patientId);
        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is not the owner of the request
        when(userService.isValidRequestOfRole(Role.SECRETARY)).thenReturn(true);

        // call getTreatmentById() method in Endpoint expect forbidden
        TreatmentPageDto treatments = treatmentEndpoint.searchTreatments(searchParams);
        assertNotNull(treatments);
        verify(treatmentService, times(1)).searchTreatments(searchParams);
        verify(userService, times(1)).isValidRequestOfRole(Role.SECRETARY);
    }

    @Test
    @DisplayName("searchTreatments: valid searchParams and own patient request - expect success")
    @WithMockUser(roles = "PATIENT")
    void shouldReturnTreatments_whenGivenValidSearchParamsAndOwnPatientRequest() {
        TreatmentDtoSearch searchParams = mock(TreatmentDtoSearch.class);
        Long patientId = 1L;
        TreatmentPageDto treatmentPageDto = mock(TreatmentPageDto.class);

        when(treatmentService.searchTreatments(searchParams)).thenReturn(treatmentPageDto);

        when(searchParams.patientId()).thenReturn(patientId);
        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is not the owner of the request
        when(patientServiceImpl.isOwnRequest(patientId)).thenReturn(true);

        // call getTreatmentById() method in Endpoint expect forbidden
        TreatmentPageDto treatments = treatmentEndpoint.searchTreatments(searchParams);
        assertNotNull(treatments);
        verify(treatmentService, times(1)).searchTreatments(searchParams);
        verify(patientServiceImpl, times(1)).isOwnRequest(patientId);
    }

    @Test
    @DisplayName("searchTreatments: valid searchParams but unauthorized role - expect forbidden")
    @WithMockUser(roles = "PATIENT")
    void shouldThrowForbidden_whenGivenValidSearchParamsButUnauthorizedRole() {
        TreatmentDtoSearch searchParams = mock(TreatmentDtoSearch.class);
        Long patientId = 1L;

        when(searchParams.patientId()).thenReturn(patientId);
        // mock isOwnRequest() method in PatientServiceImpl simulate that the patient is not the owner of the request
        when(patientServiceImpl.isOwnRequest(patientId)).thenReturn(false);

        // call getTreatmentById() method in Endpoint expect forbidden
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> treatmentEndpoint.searchTreatments(searchParams));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(treatmentService, times(0)).searchTreatments(searchParams);
        verify(patientServiceImpl, times(1)).isOwnRequest(patientId);
    }

    @Test
    @DisplayName("getTreatmentById: invalid id - expect not found")
    @WithMockUser(roles = "DOCTOR")
    void shouldThrowNotFound_whenGivenInvalidId() {
        Long id = 999L;

        when(treatmentService.getTreatmentById(id)).thenThrow(new NotFoundException("Treatment not found"));

        // call getTreatmentById() method in Endpoint and expect NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> treatmentEndpoint.getTreatmentById(id));
        assertEquals("Treatment not found", exception.getMessage());
        verify(treatmentService, times(1)).getTreatmentById(id);
    }
}
