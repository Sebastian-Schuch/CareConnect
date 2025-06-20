package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.StayDtoPage;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface StayService {

    /**
     * Get the current stay of a user.
     *
     * @param patientId the email of the user
     * @return the current stay of the user
     */
    StayDto getCurrentStay(Long patientId);

    /**
     * Create a new stay for a user.
     *
     * @param stayDtoCreate the arrival information of the user
     * @return the new stay
     * @throws NotFoundException if the user or the inpatient department is not found
     */
    StayDto createNewStay(StayDtoCreate stayDtoCreate) throws NotFoundException;

    /**
     * End the current stay of a user.
     *
     * @param stayId the id of the Stay
     * @return the ended stay
     */
    StayDto endCurrentStay(Long stayId) throws NotFoundException;

    /**
     * Get all stays of a user.
     *
     * @param patientId the id of the user
     * @return all stays of the user
     */
    StayDtoPage getAllStays(Long patientId, int page, int size);

    /**
     * Update a stay.
     *
     * @param stayDto the StayDto
     * @return the updated stay
     */
    StayDto updateStay(StayDto stayDto) throws NotFoundException;
}
