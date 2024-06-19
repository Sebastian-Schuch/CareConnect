package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

public record DataInterfaceDtoSearch(
    Date startInterval,
    Date endInterval
) {
}
