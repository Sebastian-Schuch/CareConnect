package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoDetail;

public interface SecretaryService {

    /**
     * Creates a secretary with the data given.
     *
     * @param toCreate the data to create the secretary with
     * @return the created secretary
     */
    SecretaryDtoDetail create(SecretaryDtoCreate toCreate);
}
