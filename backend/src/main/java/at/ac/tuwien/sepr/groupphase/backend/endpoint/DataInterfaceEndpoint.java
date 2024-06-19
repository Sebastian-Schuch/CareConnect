package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(DataInterfaceEndpoint.BASE_PATH)
public class DataInterfaceEndpoint {
    public static final String BASE_PATH = "/api/v1/data";


}
