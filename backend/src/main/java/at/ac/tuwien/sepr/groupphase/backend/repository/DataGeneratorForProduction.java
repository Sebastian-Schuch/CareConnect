package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Admin;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;


@Component
@Profile("default")
public class DataGeneratorForProduction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AdminRepository adminRepository;

    public DataGeneratorForProduction(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostConstruct
    public void generateData() {
        LOGGER.info("Generating admin account…");
        if (adminRepository.count() > 0) {
            LOGGER.info("Admin account already exists. Skipping generation.");
            return;
        }
        Credential credential = new Credential();
        credential.setPassword("$2a$10$ejhIzlph6XfLojO/RonFy.JHJOv6ejsKo9lxibZp.iAipC0v5y/yy");
        credential.setEmail("admin@email.com");
        credential.setLastName("admin");
        credential.setFirstName("admin");
        credential.setInitialPassword(true);
        credential.setActive(true);
        credential.setRole(Role.ADMIN);
        Admin admin = new Admin();
        admin.setCredential(credential);
        adminRepository.save(admin);
        LOGGER.info("Finished generating account without error.");
    }
}
