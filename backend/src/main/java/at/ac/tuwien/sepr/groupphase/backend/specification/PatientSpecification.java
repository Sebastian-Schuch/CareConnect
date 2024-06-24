package at.ac.tuwien.sepr.groupphase.backend.specification;


import at.ac.tuwien.sepr.groupphase.backend.entity.Patient;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecification {

    public static Specification<Patient> containsTextInAnyField(String searchText) {
        return (root, query, criteriaBuilder) -> {
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("firstName")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("lastName")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("email")), searchPattern)
            );
        };
    }

    public static Specification<Patient> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("credential").get("active"));
    }
}
